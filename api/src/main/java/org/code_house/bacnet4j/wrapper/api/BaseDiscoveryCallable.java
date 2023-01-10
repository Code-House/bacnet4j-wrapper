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
import com.serotonin.bacnet4j.util.DiscoveryUtils;
import com.serotonin.bacnet4j.util.PropertyReferences;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.bacnet4j.util.RequestUtils;
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
public abstract class BaseDiscoveryCallable extends DeviceEventAdapter implements Callable<Set<Device>> {

    private final Logger logger = LoggerFactory.getLogger(DiscoveryCallable.class);

    private final DeviceDiscoveryListener listener;
    private final LocalDevice localDevice;
    private final long timeout;
    private final long sleep;
    private final Set<Device> devices = new LinkedHashSet<>();
    private Throwable exception;

    public BaseDiscoveryCallable(DeviceDiscoveryListener listener, LocalDevice localDevice, long timeout, long sleep) {
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
            final ObjectIdentifier oid = d.getObjectIdentifier();

            // Get the device's supported services
            if (d.getServicesSupported() == null) {
                final ReadPropertyAck supportedServicesAck = (ReadPropertyAck) localDevice
                    .send(d, new ReadPropertyRequest(oid, PropertyIdentifier.protocolServicesSupported)).get();
                d.setDeviceProperty(PropertyIdentifier.protocolServicesSupported, supportedServicesAck.getValue());
            }

            // Uses the readProperties method here because this list will probably be extended.
            final PropertyReferences properties = new PropertyReferences();
            addIfMissing(d, properties, PropertyIdentifier.objectName);
            addIfMissing(d, properties, PropertyIdentifier.protocolVersion);
            addIfMissing(d, properties, PropertyIdentifier.vendorIdentifier);
            addIfMissing(d, properties, PropertyIdentifier.modelName);
            addIfMissing(d, properties, PropertyIdentifier.maxSegmentsAccepted);

            if (properties.size() > 0) {
                // Only send a request if we have to.
                final PropertyValues values = RequestUtils.readProperties(localDevice, d, properties, false, null);

                values.forEach((opr) -> {
                    final Encodable value = values.getNullOnError(oid, opr.getPropertyIdentifier());
                    d.setDeviceProperty(opr.getPropertyIdentifier(), value);
                });
            }

            if (properties.size() > 0) {
                // Only send a request if we have to.
                final PropertyValues values = RequestUtils.readProperties(localDevice, d, properties, false, null);

                values.forEach((opr) -> {
                    final Encodable value = values.getNullOnError(oid, opr.getPropertyIdentifier());
                    d.setDeviceProperty(opr.getPropertyIdentifier(), value);
                });
            }
        } catch (BACnetException e) {
            logger.error("Could not collect additional device information", e);
        }
        Device device = createDevice(d);
        if (d.getModelName() != null && !d.getModelName().isEmpty()) {
            device.setModelName(d.getModelName());
        }
        if (d.getVendorName() != null && !d.getVendorName().isEmpty()) {
            device.setVendorName(d.getVendorName());
        }
        if (d.getName() != null && !d.getName().isEmpty()) {
            device.setName(d.getName());
        }
        if (!d.getServicesSupported().isReadPropertyMultiple()) {
            device.setReadPropertyMultiple(false);
        }
        devices.add(device);
        this.listener.deviceDiscovered(device);
    }

    protected abstract Device createDevice(RemoteDevice remoteDevice);

    @Override
    public void listenerException(Throwable e) {
        this.exception = e;
    }

    private static void addIfMissing(final RemoteDevice d, final PropertyReferences properties, final PropertyIdentifier pid) {
        if (d.getDeviceProperty(pid) == null) {
            properties.add(d.getObjectIdentifier(), pid);
        }
    }
}
