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
package org.code_house.bacnet4j.wrapper.mstp;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.code_house.bacnet4j.wrapper.api.BacNetClientBase;
import org.code_house.bacnet4j.wrapper.api.BacNetClientException;
import org.code_house.bacnet4j.wrapper.api.BaseDiscoveryCallable;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.DeviceDiscoveryListener;
import org.code_house.bacnet4j.wrapper.api.util.ForwardingAdapter;
import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.npdu.mstp.MstpNetwork;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;

/**
 * Implementation of bacnet client based on serial transport.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class BacNetMstpClient extends BacNetClientBase {

    public BacNetMstpClient(MstpNetwork network, int deviceId) {
        super(new LocalDevice(deviceId, new DefaultTransport(network)));
    }

    public BacNetMstpClient(String port, int deviceId) throws Exception {
        this(new JsscMstpNetworkBuilder().withSerialPort(port).build(), deviceId);
    }

    @Override
    public CompletableFuture<Set<Device>> doDiscoverDevices(final DeviceDiscoveryListener discoveryListener, final long timeout) {
        BaseDiscoveryCallable callable = new MstpDiscoveryCallable(discoveryListener, localDevice, timeout, timeout / 10);
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
        BaseDiscoveryCallable callable = new MstpDiscoveryCallable(discoveryListener, localDevice, timeout, timeout / 10);
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

}
