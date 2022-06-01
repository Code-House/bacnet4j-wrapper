/*
 * (C) Copyright 2018 Code-House and others.
 *
 * bacnet4j-wrapper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 *     https://www.gnu.org/licenses/gpl-3.0.txt
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

import com.serotonin.bacnet4j.type.constructed.ReadAccessResult;
import com.serotonin.bacnet4j.type.constructed.ReadAccessResult.Result;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.code_house.bacnet4j.wrapper.api.BacNetClientBase;
import org.code_house.bacnet4j.wrapper.api.BacNetClientException;
import org.code_house.bacnet4j.wrapper.api.BacNetObject;
import org.code_house.bacnet4j.wrapper.api.BaseDiscoveryCallable;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.DeviceDiscoveryListener;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.code_house.bacnet4j.wrapper.api.Type;
import org.code_house.bacnet4j.wrapper.api.util.ForwardingAdapter;
import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;

/**
 * Implementation of bacnet client based on IpNetwork/UDP transport.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class BacNetIpClient extends BacNetClientBase {

    public BacNetIpClient(IpNetwork network, int deviceId) {
        super(new LocalDevice(deviceId, new DefaultTransport(network)));
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
    public CompletableFuture<Set<Device>> doDiscoverDevices(final DeviceDiscoveryListener discoveryListener, final long timeout) {
        BaseDiscoveryCallable callable = new IpDiscoveryCallable(discoveryListener, localDevice, timeout, timeout / 10);
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
    public Set<Device> discoverDevices(final DeviceDiscoveryListener discoveryListener, final long timeout) {
        BaseDiscoveryCallable callable = new IpDiscoveryCallable(discoveryListener, localDevice, timeout, timeout / 10);
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
    protected BacNetObject createObject(Device device, int instance, Type type, SequenceOf<ReadAccessResult> readAccessResults) {
        if (readAccessResults.size() == 1) {
            SequenceOf<Result> results = readAccessResults.get(0).getListOfResults();
            if (results.size() == 4) {
                String name = results.get(2).toString();
                String units = results.get(1).toString();
                String description = results.get(3).toString();
                return new BacNetObject(device, instance, type, name, description, units);
            }
            throw new IllegalStateException("Unsupported response structure " + readAccessResults);
        }
        String name = getReadValue(readAccessResults.get(2));
        String units = getReadValue(readAccessResults.get(1));
        String description = getReadValue(readAccessResults.get(3));
        return new BacNetObject(device, instance, type, name, description, units);
    }

    private String getReadValue(ReadAccessResult readAccessResult) {
        // first index contains 0 value.. I know it is weird, but that's how bacnet4j works
        return readAccessResult.getListOfResults().get(0).getReadResult().toString();
    }

}
