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
import com.serotonin.bacnet4j.ServiceFuture;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyMultipleAck;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyMultipleRequest;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.service.confirmed.WritePropertyRequest;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.ReadAccessResult;
import com.serotonin.bacnet4j.type.constructed.ReadAccessSpecification;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import org.code_house.bacnet4j.wrapper.api.*;
import org.code_house.bacnet4j.wrapper.api.util.ForwardingAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of bacnet client based on IpNetwork/UDP transport.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class BacNetIpClient implements BacNetClient {

    private final static AtomicInteger instances = new AtomicInteger();

    private final Logger logger = LoggerFactory.getLogger(BacNetIpClient.class);

    private final LocalDevice localDevice;

    private ExecutorService executor = Executors.newFixedThreadPool(2, r -> {
        Thread thread = new Thread(r, "bacnet-client-" + instances.incrementAndGet() + "-discovery");
        thread.setDaemon(true);
        return thread;
    });

    public BacNetIpClient(IpNetwork network, int deviceId) {
        DefaultTransport transport = new DefaultTransport(network);

        localDevice = new LocalDevice(deviceId, transport);
    }

    public BacNetIpClient(String ip, String broadcast, int port, int deviceId) {
        this(new IpNetworkBuilder().localBindAddress(ip).broadcastIp(broadcast).port(port).build(), deviceId);
    }

    public BacNetIpClient(String broadcast, int port, int deviceId) {
        this(new IpNetworkBuilder().broadcastIp(broadcast).port(port).build(), deviceId);
    }

    public BacNetIpClient(String ip, String broadcast, int deviceId) {
        this(new IpNetworkBuilder().localBindAddress(ip).broadcastIp(broadcast).build(), deviceId);
    }

    public BacNetIpClient(String broadcast, int deviceId) {
        this(new IpNetworkBuilder().broadcastIp(broadcast).build(), deviceId);
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
        DiscoveryCallable callable = new DiscoveryCallable(discoveryListener, localDevice, timeout, timeout / 10);
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
        DiscoveryCallable callable = new DiscoveryCallable(discoveryListener, localDevice, timeout, timeout / 10);
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

    @Override
    public List<Property> getDeviceProperties(Device device) {
        try {
            ReadPropertyAck ack = localDevice.send(device.getBacNet4jAddress(),
                    new ReadPropertyRequest(device.getObjectIdentifier(), PropertyIdentifier.objectList)).get();
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
        Encodable bacNetValue = getBacNetValue(value, converter);
        ServiceFuture send = localDevice.send(property.getDevice().getBacNet4jAddress(), new WritePropertyRequest(property.getBacNet4jIdentifier(), PropertyIdentifier.presentValue, null, bacNetValue, null));
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
        List<ReadAccessSpecification> specs = new ArrayList<ReadAccessSpecification>();
        specs.add(new ReadAccessSpecification(id, PropertyIdentifier.presentValue));
        specs.add(new ReadAccessSpecification(id, PropertyIdentifier.units));
        specs.add(new ReadAccessSpecification(id, PropertyIdentifier.objectName));
        specs.add(new ReadAccessSpecification(id, PropertyIdentifier.description));
        try {
            ReadPropertyMultipleAck propertyDescriptorAck = localDevice.send(device.getBacNet4jAddress(),
                    new ReadPropertyMultipleRequest(new SequenceOf<>(specs))).get();
            SequenceOf<ReadAccessResult> readAccessResults = propertyDescriptorAck.getListOfReadAccessResults();

            String name = getReadValue(readAccessResults.get(3));
            String units = getReadValue(readAccessResults.get(2));
            String description = getReadValue(readAccessResults.get(4));
            // present value used for mapping
            // TypeMapping type = getPropertyType(readAccessResults.get(1));

            return new Property(device, id.getInstanceNumber(), name, description, units,
                    Type.valueOf(id.getObjectType()));
        } catch (BACnetException e) {
            throw new BacNetClientException("Unable to fetch property description", e);
        }
    }

    private String getReadValue(ReadAccessResult readAccessResult) {
        // first index contains 0 value.. I know it is weird, but that's how bacnet4j works
        return readAccessResult.getListOfResults().get(1).getReadResult().toString();
    }

}
