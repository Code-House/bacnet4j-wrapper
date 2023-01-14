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
package org.code_house.bacnet4j.wrapper.api;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.bacnet4j.util.RequestUtils;
import org.code_house.bacnet4j.wrapper.device.DeviceFactory;
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
public class BlockingDiscoveryCallable extends DiscoveryEventAdapter implements Callable<Set<Device>> {

    private final long timeout;
    private final long sleep;
    private final Set<Device> devices = new LinkedHashSet<>();
    private Throwable exception;

    public BlockingDiscoveryCallable(DeviceDiscoveryListener listener, DeviceFactory deviceFactory, LocalDevice localDevice, long timeout, long sleep) {
        super(listener, deviceFactory, localDevice);
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
        Device device = createDevice(d);
        devices.add(device);
        this.listener.deviceDiscovered(device);
    }

    @Override
    public void listenerException(Throwable e) {
        this.exception = e;
    }

}
