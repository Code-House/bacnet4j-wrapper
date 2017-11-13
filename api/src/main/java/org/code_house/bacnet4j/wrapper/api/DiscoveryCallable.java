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
package org.code_house.bacnet4j.wrapper.api;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.util.DiscoveryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Callable responsible for calling collecting discovered devices and also sending discovery notifications.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class DiscoveryCallable extends DeviceEventAdapter implements Callable<Set<Device>> {

    private final Logger logger = LoggerFactory.getLogger(DiscoveryCallable.class);

    private final BacNetClient client;
    private final DeviceDiscoveryListener listener;
    private final LocalDevice localDevice;
    private final long timeout;
    private final long sleep;
    private final Set<Device> devices = new LinkedHashSet<>();
    private Throwable exception;

    public DiscoveryCallable(BacNetClient client, DeviceDiscoveryListener listener, LocalDevice localDevice, long timeout, long sleep) {
        this.client = client;
        this.listener = listener;
        this.localDevice = localDevice;
        this.timeout = timeout;
        this.sleep = sleep;
    }

    @Override
    public Set<Device> call() throws Exception {
        long time = 0;
        do {
            time += sleep;
            Thread.sleep(sleep);
        } while (time < timeout);

        if (exception != null) {
            throw new BacNetClientException("Could not finish discovery due to error", exception);
        }
        return devices;
    }

    @Override
    public void iAmReceived(RemoteDevice d) {
        try {
            DiscoveryUtils.getExtendedDeviceInformation(localDevice, d);
        } catch (BACnetException e) {
            logger.error("Could not collect additional device information", e);
        }
        Device device = new Device(client, d.getInstanceNumber(), d.getAddress());
        if (d.getModelName() != null && !d.getModelName().isEmpty()) {
            device.setModelName(d.getModelName());
        }
        if (d.getVendorName() != null && !d.getVendorName().isEmpty()) {
            device.setVendorName(d.getVendorName());
        }
        if (d.getName() != null && !d.getName().isEmpty()) {
            device.setName(d.getModelName());
        }
        devices.add(device);
        this.listener.deviceDiscovered(device);
    }

    @Override
    public void listenerException(Throwable e) {
        this.exception = e;
    }
}
