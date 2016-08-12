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

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Simple discovery listener which just returns all devices.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class CollectingDiscoveryListener implements DeviceDiscoveryListener {

    private final Set<Device> devices = new LinkedHashSet<>();

    @Override
    public void deviceDiscovered(Device device) {
        devices.add(device);
    }

    public Set<Device> getDevices() {
        return devices;
    }
}
