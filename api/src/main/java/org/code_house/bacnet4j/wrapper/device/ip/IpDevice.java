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
package org.code_house.bacnet4j.wrapper.device.ip;

import com.serotonin.bacnet4j.npdu.ip.IpNetworkUtils;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import org.code_house.bacnet4j.wrapper.api.Device;

/**
 * Device variant which assumes IP network operation,
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class IpDevice extends Device {

    public IpDevice(int instanceNumber, byte[] address, int networkNumber) {
        super(instanceNumber, address, networkNumber);
    }

    public IpDevice(int instanceNumber, Address address) {
        super(instanceNumber, address);
    }

    public IpDevice(int instanceNumber, String ip, int port, int networkNumber) {
        this(instanceNumber, IpNetworkUtils.toAddress(networkNumber, ip, port));
    }

    public String getHostAddress() {
        return IpNetworkUtils.getInetAddress(new OctetString(getAddress())).getHostAddress();
    }

    public int getPort() {
        return IpNetworkUtils.getPort(new OctetString(getAddress()));
    }


}
