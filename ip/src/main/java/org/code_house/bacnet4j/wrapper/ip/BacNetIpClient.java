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
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkUtils;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.constructed.ReadAccessResult;
import com.serotonin.bacnet4j.type.constructed.ReadAccessResult.Result;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import org.code_house.bacnet4j.wrapper.api.BacNetClientBase;
import org.code_house.bacnet4j.wrapper.api.BacNetObject;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Type;

/**
 * Implementation of bacnet client based on IpNetwork/UDP transport.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class BacNetIpClient extends BacNetClientBase {

    public BacNetIpClient(IpNetwork network, int deviceId) {
        super(new LocalDevice(deviceId, new DefaultTransport(network)));
    }

    public BacNetIpClient(String ip, String broadcast, int port, int deviceId, boolean reuseAddress) {
        this(new IpNetworkBuilder().withLocalBindAddress(ip).withBroadcast(broadcast, 24).withPort(port)
            .withReuseAddress(reuseAddress).build(), deviceId);
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

    /**
     * Explicitly register a network router which is known beforehand.
     *
     * This method is introduced for situations where network is fixed and all details are known to
     * caller. Adding network router early on allow to communicate other networks without pre-flight
     * discovery requests to populate routing table.
     *
     * @param network Network number.
     * @param ipAddress Router ip address.
     * @param port Router port.
     */
    public void addNetworkRouter(int network, String ipAddress, int port) {
        OctetString address = IpNetworkUtils.toOctetString(ipAddress, port);
        this.localDevice.getNetwork().getTransport().addNetworkRouter(network, address);
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
