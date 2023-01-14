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
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import org.code_house.bacnet4j.wrapper.device.DeviceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A discovery event adapter responsible for calling {@link DeviceDiscoveryListener} which is declared
 * by bacnet4j-wrapper api.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class DiscoveryEventAdapter extends DeviceEventAdapter {

    private final Logger logger = LoggerFactory.getLogger(DiscoveryEventAdapter.class);

    protected final DeviceDiscoveryListener listener;
    protected final DeviceFactory deviceFactory;
    protected final LocalDevice localDevice;
    public DiscoveryEventAdapter(DeviceDiscoveryListener listener, DeviceFactory deviceFactory, LocalDevice localDevice) {
        this.listener = listener;
        this.deviceFactory = deviceFactory;
        this.localDevice = localDevice;
    }

    protected Device createDevice(RemoteDevice d) {
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
        } catch (BACnetException e) {
            logger.error("Could not collect additional device information", e);
        }
        Device device = deviceFactory.createDevice(d);
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
        return device;
    }

    @Override
    public void iAmReceived(RemoteDevice d) {
        Device device = createDevice(d);
        this.listener.deviceDiscovered(device);
    }

    @Override
    public void listenerException(Throwable e) {
        logger.warn("Error while running discovery process", e);
    }

    private static void addIfMissing(final RemoteDevice d, final PropertyReferences properties, final PropertyIdentifier pid) {
        if (d.getDeviceProperty(pid) == null) {
            properties.add(d.getObjectIdentifier(), pid);
        }
    }
}
