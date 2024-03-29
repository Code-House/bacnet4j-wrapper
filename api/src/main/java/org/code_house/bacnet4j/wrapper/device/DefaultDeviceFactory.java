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
package org.code_house.bacnet4j.wrapper.device;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.type.constructed.Address;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.device.ip.IpDevice;
import org.code_house.bacnet4j.wrapper.device.mstp.MstpDevice;

public class DefaultDeviceFactory implements DeviceFactory {

  @Override
  public Device createDevice(RemoteDevice remoteDevice) {
    Address address = remoteDevice.getAddress();
    byte[] bytes = address.getMacAddress().getBytes();
    if (bytes.length == 1) {
      return new MstpDevice(remoteDevice.getInstanceNumber(), address);
    }
    return new IpDevice(remoteDevice.getInstanceNumber(), address);
  }

}
