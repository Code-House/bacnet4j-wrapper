package org.code_house.bacnet4j.wrapper.api;

import java.util.ArrayList;
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
    public List<Property> getDeviceProperties(Device device) {
        try {
            ReadPropertyAck ack = localDevice.send(device.getBacNet4jAddress(),
                    new ReadPropertyRequest(device.getObjectIdentifier(), PropertyIdentifier.objectList)).get();
            SequenceOf<ObjectIdentifier> value = ack.getValue();

            List<Property> properties = new ArrayList<>();
            logger.debug("Received list of BACnet properties. Size {}, values {}", value.getCount(), value);
            for (ObjectIdentifier id : value) {
                logger.trace("Creating property from object identifier {}", id);
                try {
                    if (ObjectType.device.equals(id.getObjectType())) {
                        properties.add(createProperty(device, id));
                        continue;
                    }
                    properties.add(createProperty(device, id));
                } catch (UnsupportedTypeException e) {
                    logger.warn("Discovered unsupported property, ignoring", e);
                }
            }
            return properties;
        } catch (BACnetException e) {
            throw new BacNetClientException("Unable to get device properties", e);
        }
    }

    @Override
    public <T> T getPropertyValue(Property property, BacNetToJavaConverter<T> converter) {
        try {
            ReadPropertyAck presentValue = localDevice.send(property.getDevice().getBacNet4jAddress(),
                new ReadPropertyRequest(new ObjectIdentifier(property.getType().getBacNetType(), property.getId()),
                    PropertyIdentifier.presentValue)).get();

            return (T) getJavaValue(presentValue.getValue(), converter);
        } catch (BACnetException e) {
            throw new BacNetClientException("Could not get property value", e);
        }
    }

    @Override
    public List<Object> getPropertyValues(List<Property> properties) {
        BypassBacnetConverter converter = new BypassBacnetConverter();
        Device device = properties.get(0).getDevice();
        List<Object> values = new ArrayList<>();
        List<ReadAccessSpecification> specifications = new ArrayList<>();
        for (int propertyIndex = 0; propertyIndex < properties.size(); propertyIndex++) {
            Property property = properties.get(propertyIndex);
            specifications.add(new ReadAccessSpecification(new ObjectIdentifier(property.getType().getBacNetType(), property.getId()), PropertyIdentifier.presentValue));

            if (propertyIndex % 3 == 0 || propertyIndex + 1 == properties.size()) {
                try {
                    ReadPropertyMultipleAck readValues = localDevice.send(device.getBacNet4jAddress(), new ReadPropertyMultipleRequest(new SequenceOf<ReadAccessSpecification>(specifications))).get();
                    specifications.clear();
                    SequenceOf<ReadAccessResult> listOfReadAccessResults = readValues.getListOfReadAccessResults();
                    for (int index = 0; index < listOfReadAccessResults.getCount(); index++) {
                        ReadAccessResult result = listOfReadAccessResults.get(index + 1);
                        logger.info("Reading property {} value from {}", properties.get(propertyIndex), result.getListOfResults());
                        values.add(getJavaValue(result.getListOfResults().get(1).getReadResult().getDatum(), converter));
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
    public <T> void setPropertyValue(Property property, T value, JavaToBacNetConverter<T> converter) {
        setPropertyValue(property, value, converter, 0);
    }

    @Override
    public <T> void setPropertyValue(Property property, T value, JavaToBacNetConverter<T> converter, int priority) {
        setPropertyValue(property, value, converter, Priorities.get(priority).orElse(null));
    }

    @Override
    public <T> void setPropertyValue(Property property, T value, JavaToBacNetConverter<T> converter, Priority priority) {
        Encodable bacNetValue = getBacNetValue(value, converter);
        UnsignedInteger bacnetPriority = Optional.ofNullable(priority)
            .map(Priority::getPriority)
            .map(UnsignedInteger::new)
            .orElse(null);
        ServiceFuture send = localDevice.send(property.getDevice().getBacNet4jAddress(),
            new WritePropertyRequest(property.getBacNet4jIdentifier(), PropertyIdentifier.presentValue, null, bacNetValue, bacnetPriority));
        try {
            send.get();
        } catch (BACnetException e) {
            if (!"Timeout waiting for response.".equals(e.getMessage())) {
                throw new BacNetClientException("Could not set property value", e);
            }
            logger.warn("Ignoring timeout for write property since bacnet4j misses simple ACK's.");
        }
    }

    private Property createProperty(Device device, ObjectIdentifier id) {
        List<ReadAccessSpecification> specs = new ArrayList<>();
        specs.add(new ReadAccessSpecification(id, PropertyIdentifier.presentValue));
        specs.add(new ReadAccessSpecification(id, PropertyIdentifier.units));
        specs.add(new ReadAccessSpecification(id, PropertyIdentifier.objectName));
        specs.add(new ReadAccessSpecification(id, PropertyIdentifier.description));

        try {
            ReadPropertyMultipleAck propertyDescriptorAck = localDevice.send(device.getBacNet4jAddress(),
                new ReadPropertyMultipleRequest(new SequenceOf<>(specs))).get();
            SequenceOf<ReadAccessResult> readAccessResults = propertyDescriptorAck.getListOfReadAccessResults();
            return createProperty(device, id.getInstanceNumber(), Type.valueOf(id.getObjectType()),
                readAccessResults
            );
        } catch (BACnetException e) {
            throw new BacNetClientException("Unable to fetch property description", e);
        }
    }

    protected abstract Property createProperty(Device device, int instance, Type type, SequenceOf<ReadAccessResult> readAccessResults);

}
