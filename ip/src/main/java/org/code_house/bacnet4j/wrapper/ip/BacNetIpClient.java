/*
 * (C) Copyright 2016 Code-House and others.
 *
 * bacnet4j-wrapper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 *     https://www.gnu.org/licenses/gpl-2.0.txt
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.code_house.bacnet4j.wrapper.ip;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.ServiceFuture;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkUtils;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyMultipleAck;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyMultipleRequest;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.service.confirmed.WritePropertyRequest;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.ReadAccessResult;
import com.serotonin.bacnet4j.type.constructed.ReadAccessSpecification;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Null;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import org.code_house.bacnet4j.wrapper.api.*;
import org.code_house.bacnet4j.wrapper.api.primitives.BacNetNull;
import org.code_house.bacnet4j.wrapper.api.primitives.BacNetObjectIdentifier;
import org.code_house.bacnet4j.wrapper.api.registry.StandardTypeRegistry;
import org.code_house.bacnet4j.wrapper.util.DiscoveryCallable;
import org.code_house.bacnet4j.wrapper.util.ForwardingAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of bacnet client based on IpNetwork/UDP transport.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class BacNetIpClient implements BacNetClient, BacNetNetwork, BacNetContext {

    private final static AtomicInteger instances = new AtomicInteger();

    private final Logger logger = LoggerFactory.getLogger(BacNetIpClient.class);

    private Map<Integer, Address> deviceMap = new ConcurrentHashMap<>();
    private final LocalDevice localDevice;
    private final BacNetTypeRegistry typeRegistry;

    private ExecutorService executor = Executors.newFixedThreadPool(2, r -> {
        Thread thread = new Thread(r, "bacnet-client-" + instances.incrementAndGet() + "-discovery");
        thread.setDaemon(true);
        return thread;
    });

    public BacNetIpClient(BacNetTypeRegistry typeRegistry, IpNetwork network, int deviceId) {
        this.localDevice = new LocalDevice(deviceId, new DefaultTransport(network));
        this.typeRegistry = typeRegistry;
    }

    public BacNetIpClient(IpNetwork network, int deviceId) {
        this(new StandardTypeRegistry(), network, deviceId);
    }

    public BacNetIpClient(String ip, String broadcast, int port, int deviceId) {
        this(new IpNetworkBuilder().withLocalBindAddress(ip).withBroadcast(broadcast, 24).withPort(port).build(), deviceId);
    }

    public BacNetIpClient(String broadcast, int port, int deviceId) {
        this(new IpNetworkBuilder().withBroadcast(broadcast, 24).withPort(port).build(), deviceId);
    }

    public BacNetIpClient(String ip, String broadcast, int deviceId) {
        this(new IpNetworkBuilder().withLocalBindAddress(ip).withBroadcast(broadcast, 24).build(), deviceId);
    }

    public BacNetIpClient(String broadcast, int deviceId) {
        this(new IpNetworkBuilder().withBroadcast(broadcast, 24).build(), deviceId);
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
    public CompletableFuture<Set<Device>> doDiscoverDevices(final DeviceDiscoveryListener discoveryListener, final long timeout) {
        DiscoveryCallable callable = new DiscoveryCallable(this, this::registerDevice, discoveryListener, localDevice, timeout, timeout / 10);
        ForwardingAdapter listener = new ForwardingAdapter(executor, callable);
        localDevice.getEventHandler().addListener(listener);
        localDevice.sendGlobalBroadcast(new WhoIsRequest());
        return CompletableFuture.supplyAsync(() -> {
            try {
                return callable.call();
            } catch (Exception e) {
                throw new BacNetClientException("Could not complete discovery task", e);
            }
        }, executor).whenComplete((devices, throwable) -> {
            localDevice.getEventHandler().removeListener(listener);
        });
    }

    @Override
    public Set<Device> discoverDevices(final long timeout) {
        return discoverDevices(new NoopDiscoveryListener(), timeout);
    }

    @Override
    public Set<Device> discoverDevices(final DeviceDiscoveryListener discoveryListener, final long timeout) {
        DiscoveryCallable callable = new DiscoveryCallable(this, this::registerDevice, discoveryListener, localDevice, timeout, timeout / 10);
        ForwardingAdapter listener = new ForwardingAdapter(executor, callable);
        try {
            localDevice.getEventHandler().addListener(listener);
            Future<Set<Device>> future = executor.submit(callable);
            localDevice.sendGlobalBroadcast(new WhoIsRequest());
            // this will block for at least timeout milliseconds
            return future.get();
        } catch (ExecutionException e) {
            logger.error("Device discovery have failed", e);
        } catch (InterruptedException e) {
            logger.error("Could not discover devices due to timeout", e);
        } finally {
            localDevice.getEventHandler().removeListener(listener);
        }
        return Collections.emptySet();
    }

    private void registerDevice(RemoteDevice remoteDevice) {
        this.deviceMap.put(remoteDevice.getInstanceNumber(), remoteDevice.getAddress());
    }

    @Override
    public List<Property> getDeviceProperties(Device device) {
        try {
            ReadPropertyAck ack = localDevice.send(address(device),
                new ReadPropertyRequest(identifier(device.getObjectIdentifier()), PropertyIdentifier.objectList)).get();
            SequenceOf<ObjectIdentifier> value = ack.getValue();

            List<Property> properties = new ArrayList<>();
            for (ObjectIdentifier id : value) {
                if (ObjectType.device.equals(id.getObjectType())) {
                    continue;
                }
                properties.add(createProperty(device, id));
            }
            return properties;
        } catch (BACnetException e) {
            throw new BacNetClientException("Unable to get device properties", e);
        }
    }

    private ObjectIdentifier identifier(BacNetObjectIdentifier objectIdentifier) {
        return new ObjectIdentifier(objectIdentifier.getType(), objectIdentifier.getInstance());
    }

    @Override
    public <T> BacNetValue<T> getPropertyValue(Property property) {
        if (!(property instanceof ManageableProperty)) {
            return null; // BacNetNull
        }

        try {
            ReadPropertyAck presentValue = localDevice.send(address(property.getParent()),
                new ReadPropertyRequest(getBacnetIdentifier(property),
                    PropertyIdentifier.presentValue)).get();

            return getJavaValue(presentValue.getValue());
        } catch (BACnetException e) {
            throw new BacNetClientException("Could not get property value", e);
        }
    }

    @Override
    public List<Object> getPropertyValues(List<Property> properties) {
        BacNetElement device = properties.get(0).getParent();


        List<Object> values = new ArrayList<>();
        List<ReadAccessSpecification> specifications = new ArrayList<>();
        for (int propertyIndex = 0; propertyIndex < properties.size(); propertyIndex++) {
            Property property = properties.get(propertyIndex);
            specifications.add(new ReadAccessSpecification(getBacnetIdentifier(property), PropertyIdentifier.presentValue));

            if (propertyIndex % 3 == 0 || propertyIndex + 1 == properties.size()) {
                try {
                    ReadPropertyMultipleAck readValues = localDevice.send(address(device), new ReadPropertyMultipleRequest(new SequenceOf<>(specifications))).get();
                    specifications.clear();
                    SequenceOf<ReadAccessResult> listOfReadAccessResults = readValues.getListOfReadAccessResults();
                    for (int index = 0; index < listOfReadAccessResults.getCount(); index++) {
                        ReadAccessResult result = listOfReadAccessResults.get(index + 1);
                        logger.info("Reading property {} value from {}", properties.get(propertyIndex), result.getListOfResults());
                        values.add(getJavaValue(result.getListOfResults().get(1).getReadResult().getDatum()));
                    }
                } catch (BACnetException e) {
                    throw new BacNetClientException("Unable to read properties.", e);
                }
            }
        }

        return values;
    }

    private Address address(BacNetElement device) {
        if (device instanceof Device) {
            int id = ((Device) device).getInstanceNumber();
            if (deviceMap.containsKey(id)) {
                return deviceMap.get(id);
            } else {
                throw new IllegalArgumentException("Device id " + id + " is not known yet");
            }
        }

        throw new IllegalArgumentException(device + " is not device");
    }

    private <T> BacNetValue<T> getJavaValue(Encodable datum) {
        if (datum == null || datum instanceof Null) {
            return null; //BacNetNull.instance;
        }

        throw new AbstractMethodError("Operation not supported yet.");
        //return converter.fromBacNet(null /*datum*/);
    }

    private <T> Encodable getBacNetValue(BacNetValue<T> value) {
        if (value == null || value instanceof BacNetNull) {
            return Null.instance;
        }

        throw new AbstractMethodError("Operation not supported yet.");
        //return converter.toBacNet(object);
    }

    private ObjectIdentifier getBacnetIdentifier(Property property) {
        ObjectType objectType = ObjectType.forId(property.getType().getCode());
        return new ObjectIdentifier(objectType, property.getId());
    }

    @Override
    public <T> void setPropertyValue(Property property, BacNetValue<T> value) {
        Encodable bacNetValue = getBacNetValue(value);
        ObjectIdentifier objectIdentifier = getBacnetIdentifier(property);
        ServiceFuture send = localDevice.send(address(property.getParent()), new WritePropertyRequest(objectIdentifier,
            PropertyIdentifier.presentValue, null, bacNetValue, null));
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
        /*
        List<ReadAccessSpecification> specs = new ArrayList<>();
        specs.add(new ReadAccessSpecification(id, PropertyIdentifier.units));
        specs.add(new ReadAccessSpecification(id, PropertyIdentifier.objectName));
        specs.add(new ReadAccessSpecification(id, PropertyIdentifier.description));
        */

        //try {
            /*
            ReadPropertyMultipleAck propertyDescriptorAck = localDevice.send(address(device),
                new ReadPropertyMultipleRequest(new SequenceOf<>(specs))).get();
            SequenceOf<ReadAccessResult> readAccessResults = propertyDescriptorAck.getListOfReadAccessResults();

            String name = getReadValue(readAccessResults.get(1));
            String description = getReadValue(readAccessResults.get(2));
            String units = getReadValue(readAccessResults.get(0));
            */

            Optional<PropertyType<Device, Property<Device>>> propertyType = typeRegistry.lookup(id.getObjectType().intValue())
                .filter(type -> type instanceof PropertyType)
                .map(type -> ((PropertyType<Device, Property<Device>>) type));

            return propertyType.map(type -> type.create(device, id.getInstanceNumber()/*, name, description*/))
                .orElse(null);
        //} catch (BACnetException e) {
        //    throw new BacNetClientException("Unable to fetch property description", e);
        //}
    }

    private String getReadValue(ReadAccessResult readAccessResult) {
        return readAccessResult.getListOfResults().get(0).getReadResult().toString();
    }


    @Override
    public BacNetTypeRegistry getTypeRegistry() {
        return typeRegistry;
    }

    @Override
    public <X> BacNetValue<X> get(Property property) {
        return getPropertyValue(property);
    }

    @Override
    public Map<Property, BacNetValue> get(Property... properties) {
        Map<Property, BacNetValue> values = new LinkedHashMap<>();
        for (Property property : properties) {
            values.put(property, get(property));
        }
        return values;
    }

    @Override
    public Map<PropertyType, BacNetValue> get(PropertyType... properties) {
        return null;
    }

    @Override
    public BacNetRoot getParent() {
        return null;
    }

    @Override
    public BacNetContext getContext() {
        return this;
    }
}
