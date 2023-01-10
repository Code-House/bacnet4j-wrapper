package org.code_house.bacnet4j.wrapper.api;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.service.acknowledgement.AcknowledgementService;
import com.serotonin.bacnet4j.util.RemoteDeviceFinder.RemoteDeviceFuture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.ServiceFuture;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyMultipleAck;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyMultipleRequest;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.service.confirmed.WritePropertyRequest;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.ReadAccessResult;
import com.serotonin.bacnet4j.type.constructed.ReadAccessSpecification;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public abstract class BacNetClientBase implements BacNetClient {

    private final static AtomicInteger instances = new AtomicInteger();

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final LocalDevice localDevice;

    protected final ExecutorService executor = Executors.newFixedThreadPool(2, r -> {
        Thread thread = new Thread(r, "bacnet-client-" + instances.incrementAndGet() + "-discovery");
        thread.setDaemon(true);
        return thread;
    });

    protected BacNetClientBase(LocalDevice device) {
        localDevice = device;
    }

    @Override
    public void start() {
        try {
            localDevice.initialize();
        } catch (Exception e) {
            throw new BacNetClientException("Unable to initialize client", e);
        }
    }

    @Override
    public void stop() {
        executor.shutdown();
        localDevice.terminate();
    }

    @Override
    public Set<Device> discoverDevices(final long timeout) {
        return discoverDevices(new NoopDiscoveryListener(), timeout);
    }

    @Override
    public List<BacNetObject> getDeviceObjects(Device device) {
        try {
            ReadPropertyAck ack = localDevice.send(device.getBacNet4jAddress(),
                new ReadPropertyRequest(device.getObjectIdentifier(), PropertyIdentifier.objectList)
            ).get();
            SequenceOf<ObjectIdentifier> value = ack.getValue();

            List<BacNetObject> objects = new ArrayList<>();
            logger.debug("Received list of BACnet objects. Size {}, values {}", value.getCount(), value);
            for (ObjectIdentifier id : value) {
                logger.trace("Creating property from object identifier {}", id);
                try {
                    if (ObjectType.device.equals(id.getObjectType())) {
                        objects.add(createObject(device, id));
                        continue;
                    }
                    objects.add(createObject(device, id));
                } catch (UnsupportedTypeException e) {
                    logger.warn("Discovered unsupported property, ignoring", e);
                }
            }
            return objects;
        } catch (BACnetException e) {
            throw new BacNetClientException("Unable to get device properties", e);
        }
    }

    @Override
    public <T> T getPresentValue(BacNetObject object, BacNetToJavaConverter<T> converter) {
        try {
            ReadPropertyAck presentValue = localDevice.send(object.getDevice().getBacNet4jAddress(),
                new ReadPropertyRequest(new ObjectIdentifier(object.getType().getBacNetType(), object.getId()),
                    PropertyIdentifier.presentValue)
            ).get();

            return (T) getJavaValue(presentValue.getValue(), converter);
        } catch (BACnetException e) {
            throw new BacNetClientException("Could not get property value", e);
        }
    }

    @Override
    public List<String> getObjectPropertyNames(BacNetObject object) {
        try {
            ReadPropertyAck answer = localDevice.send(object.getDevice().getBacNet4jAddress(),
                new ReadPropertyRequest(new ObjectIdentifier(object.getType().getBacNetType(), object.getId()),
                    PropertyIdentifier.propertyList)
            ).get();

            if (answer.getValue() instanceof SequenceOf) {
                List<String> attributes = new ArrayList<>();
                for (PropertyIdentifier identifier : ((SequenceOf<PropertyIdentifier>) answer.getValue())) {
                    attributes.add(identifier.toString());
                }
                return attributes;
            }
            logger.warn("Unexpected answer for object attribute list {}, forcing empty results", answer.getValue());
            return Collections.emptyList();
        } catch (BACnetException e) {
            throw new BacNetClientException("Could not get property value", e);
        }
    }

    @Override
    public <T> T getObjectPropertyValue(BacNetObject object, String property, BacNetToJavaConverter<T> converter) {
        try {
            ReadPropertyAck propertyVal = localDevice.send(object.getDevice().getBacNet4jAddress(),
                new ReadPropertyRequest(new ObjectIdentifier(object.getType().getBacNetType(), object.getId()),
                    PropertyIdentifier.forName(property))
            ).get();

            return (T) getJavaValue(propertyVal.getValue(), converter);
        } catch (BACnetException e) {
            throw new BacNetClientException("Could not get property value", e);
        }
    }

    @Override
    public List<Object> getObjectAttributeValues(BacNetObject object, List<String> properties) {
        BypassBacnetConverter converter = new BypassBacnetConverter();
        Device device = object.getDevice();
        List<Object> values = new ArrayList<>();
        if (!device.isReadMultiple()) {
            for (String property : properties) {
                try {
                    ReadPropertyAck answer = localDevice.send(device.getBacNet4jAddress(),
                        new ReadPropertyRequest(object.getBacNet4jIdentifier(), PropertyIdentifier.forName(property))
                    ).get();
                    values.add(converter.fromBacNet(answer.getValue()));
                } catch (BACnetException e) {
                    throw new BacNetClientException("Unable to read object properties.", e);
                }
            }
        }

        List<ReadAccessSpecification> specifications = new ArrayList<>();
        for (String property : properties) {
            specifications.add(new ReadAccessSpecification(new ObjectIdentifier(object.getType().getBacNetType(),
                object.getId()), PropertyIdentifier.forName(property))
            );
        }

        try {
            ReadPropertyMultipleAck readValues = localDevice.send(device.getBacNet4jAddress(),
                new ReadPropertyMultipleRequest(new SequenceOf<>(specifications))
            ).get();
            SequenceOf<ReadAccessResult> listOfReadAccessResults = readValues.getListOfReadAccessResults();
            for (int index = 0; index < listOfReadAccessResults.getCount(); index++) {
                ReadAccessResult result = listOfReadAccessResults.get(index);
                logger.info("Reading object {} properties {}, values: {}", object, properties.get(index), result.getListOfResults());
                values.add(getJavaValue(result.getListOfResults().get(0).getReadResult().getDatum(), converter));
            }
        } catch (BACnetException e) {
            throw new BacNetClientException("Unable to read object properties.", e);
        }

        return values;
    }

    @Override
    public List<Object> getPresentValues(List<BacNetObject> objects) {
        BypassBacnetConverter converter = new BypassBacnetConverter();
        Device device = objects.get(0).getDevice();
        List<java.lang.Object> values = new ArrayList<>();
        if (!device.isReadMultiple()) {
            for (int objectIndex = 0; objectIndex < objects.size(); objectIndex++) {
                try {
                    BacNetObject object = objects.get(objectIndex);
                    ReadPropertyAck answer = localDevice.send(object.getDevice().getBacNet4jAddress(),
                        new ReadPropertyRequest(new ObjectIdentifier(object.getType().getBacNetType(), object.getId()),
                            PropertyIdentifier.presentValue)
                    ).get();
                    values.add(converter.fromBacNet(answer.getValue()));
                } catch (BACnetException e) {
                    throw new BacNetClientException("Sequential read of object present values failed", e);
                }
            }
            return values;
        }

        List<ReadAccessSpecification> specifications = new ArrayList<>();
        for (int propertyIndex = 0; propertyIndex < objects.size(); propertyIndex++) {
            BacNetObject object = objects.get(propertyIndex);
            specifications.add(new ReadAccessSpecification(new ObjectIdentifier(object.getType().getBacNetType(), object.getId()),
                PropertyIdentifier.presentValue)
            );

            if (propertyIndex % 3 == 0 || propertyIndex + 1 == objects.size()) {
                try {
                    ReadPropertyMultipleAck readValues = localDevice.send(device.getBacNet4jAddress(),
                        new ReadPropertyMultipleRequest(new SequenceOf<>(specifications)))
                    .get();
                    specifications.clear();
                    SequenceOf<ReadAccessResult> listOfReadAccessResults = readValues.getListOfReadAccessResults();
                    for (int index = 0; index < listOfReadAccessResults.getCount(); index++) {
                        ReadAccessResult result = listOfReadAccessResults.get(index);
                        logger.info("Reading property {} value from {}", objects.get(propertyIndex), result.getListOfResults());
                        values.add(getJavaValue(result.getListOfResults().get(0).getReadResult().getDatum(), converter));
                    }
                } catch (BACnetException e) {
                    throw new BacNetClientException("Unable to read properties.", e);
                }
            }
        }
        return values;
    }

    private <T> T getJavaValue(Encodable datum, BacNetToJavaConverter<T> converter) {
        if (converter == null) {
            return null;
        }
        return converter.fromBacNet(datum);
    }

    private <T> Encodable getBacNetValue(T object, JavaToBacNetConverter<T> converter) {
        if (converter == null) {
            return null;
        }
        return converter.toBacNet(object);
    }

    @Override
    public <T> void setPresentValue(BacNetObject object, T value, JavaToBacNetConverter<T> converter) {
        setPresentValue(object, value, converter, 0);
    }

    @Override
    public <T> void setPresentValue(BacNetObject object, T value, JavaToBacNetConverter<T> converter, int priority) {
        setPresentValue(object, value, converter, Priorities.get(priority).orElse(null));
    }

    @Override
    public <T> void setPresentValue(BacNetObject object, T value, JavaToBacNetConverter<T> converter, Priority priority) {
        setObjectPropertyValue(object, "present-value", value, converter, priority);
    }

    @Override
    public <T> void setObjectPropertyValue(BacNetObject object, String property, T value, JavaToBacNetConverter<T> converter) {
        setObjectPropertyValue(object, property, value, converter, 0);
    }

    @Override
    public <T> void setObjectPropertyValue(BacNetObject object, String property, T value, JavaToBacNetConverter<T> converter, int priority) {
        setObjectPropertyValue(object, property, value, converter, Priorities.get(priority).orElse(null));
    }

    @Override
    public <T> void setObjectPropertyValue(BacNetObject object, String property, T value, JavaToBacNetConverter<T> converter, Priority priority) {
        Encodable bacNetValue = getBacNetValue(value, converter);
        UnsignedInteger bacnetPriority = Optional.ofNullable(priority)
            .map(Priority::getPriority)
            .map(UnsignedInteger::new)
            .orElse(null);
        ServiceFuture send = localDevice.send(object.getDevice().getBacNet4jAddress(),
            new WritePropertyRequest(object.getBacNet4jIdentifier(), PropertyIdentifier.forName(property), null, bacNetValue, bacnetPriority)
        );
        try {
            send.get();
        } catch (BACnetException e) {
            if (!"Timeout waiting for response.".equals(e.getMessage())) {
                throw new BacNetClientException("Could not set property value", e);
            }
            logger.warn("Ignoring timeout for write property since bacnet4j misses simple ACK's.");
        }
    }

    private BacNetObject createObject(Device device, ObjectIdentifier id) {
        if (device.isReadMultiple()) {
            List<ReadAccessSpecification> specs = new ArrayList<>();
            specs.add(new ReadAccessSpecification(id, PropertyIdentifier.presentValue));
            specs.add(new ReadAccessSpecification(id, PropertyIdentifier.units));
            specs.add(new ReadAccessSpecification(id, PropertyIdentifier.objectName));
            specs.add(new ReadAccessSpecification(id, PropertyIdentifier.description));

            try {
                ReadPropertyMultipleAck propertyDescriptorAck = localDevice.send(device.getBacNet4jAddress(),
                    new ReadPropertyMultipleRequest(new SequenceOf<>(specs))
                ).get();
                SequenceOf<ReadAccessResult> readAccessResults = propertyDescriptorAck.getListOfReadAccessResults();
                return createObject(device, id.getInstanceNumber(), Type.valueOf(id.getObjectType()),
                    readAccessResults
                );
            } catch (BACnetException e) {
                throw new BacNetClientException("Unable to fetch property description", e);
            }
        }

        return createObjectSingleRead(device, id);
    }

    private BacNetObject createObjectSingleRead(Device device, ObjectIdentifier id) {
        Encodable presentValue = readOrNull(device, id, PropertyIdentifier.presentValue);
        Encodable units = readOrNull(device, id, PropertyIdentifier.units);
        Encodable objectName = readOrNull(device, id, PropertyIdentifier.objectName);
        Encodable description = readOrNull(device, id, PropertyIdentifier.description);
        if (presentValue == null && units == null && objectName == null && description == null) {
            //throw new BacNetClientException("Could not construct object " + id + " for device " + device, new IllegalArgumentException());
            return new BacNetObject(device, id.getInstanceNumber(), Type.valueOf(id.getObjectType()), null, null, null);
        }
        return new BacNetObject(
            device, id.getInstanceNumber(), Type.valueOf(id.getObjectType()),
            objectName == null ? "" : objectName.toString(),
            description == null ? "" : description.toString(),
            units == null ? null : units.toString()
        );
    }

    protected abstract BacNetObject createObject(Device device, int instance, Type type, SequenceOf<ReadAccessResult> readAccessResults);

    protected <T> T readOrNull(Device device, ObjectIdentifier object, PropertyIdentifier identifier) {
        ObjectIdentifier id = new ObjectIdentifier(object.getObjectType(), object.getInstanceNumber());
        try {
            ReadPropertyAck presentValue = localDevice.send(device.getBacNet4jAddress(),
                new ReadPropertyRequest(id, identifier)
            ).get();
            return (T) presentValue.getValue();
        } catch (BACnetException e) {
            logger.trace("Failed to retrieve property {} for device {} object {}", identifier, device, object, e);
            return null;
        }
    }

}
