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

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import org.code_house.bacnet4j.wrapper.api.BaseDiscoveryCallable;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.DeviceDiscoveryListener;

/**
 * Callable which creates IP devices - to be used with IP clients.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class IpDiscoveryCallable extends BaseDiscoveryCallable {

    public IpDiscoveryCallable(DeviceDiscoveryListener listener, LocalDevice localDevice, long timeout, long sleep) {
        super(listener, localDevice, timeout, sleep);
    }

    @Override
    protected Device createDevice(RemoteDevice remoteDevice) {
        return new IpDevice(remoteDevice.getInstanceNumber(), remoteDevice.getAddress());
    }

}
