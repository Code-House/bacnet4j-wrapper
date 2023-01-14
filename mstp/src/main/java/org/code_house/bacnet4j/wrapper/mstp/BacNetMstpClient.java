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

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.npdu.mstp.MstpNetwork;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.constructed.ReadAccessResult;
import com.serotonin.bacnet4j.type.constructed.ReadAccessResult.Result;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import org.code_house.bacnet4j.wrapper.api.BacNetClientBase;
import org.code_house.bacnet4j.wrapper.api.BacNetObject;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Type;
import org.code_house.bacnet4j.wrapper.device.mstp.MstpDevice;

/**
 * Implementation of bacnet client based on serial transport.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class BacNetMstpClient extends BacNetClientBase {

    public BacNetMstpClient(MstpNetwork network, int deviceId) {
        this(network, deviceId, 60_000, 5_000);
    }

    public BacNetMstpClient(MstpNetwork network, int deviceId, int timeout, int segTimeout) {
        super(new LocalDevice(deviceId, createTransport(network, timeout, segTimeout)),
            (remoteDevice) -> new MstpDevice(remoteDevice.getInstanceNumber(), remoteDevice.getAddress())
        );
    }

    @Override
    protected BacNetObject createObject(Device device, int instance, Type type, SequenceOf<ReadAccessResult> readAccessResults) {
        SequenceOf<Result> result = readAccessResults.get(0).getListOfResults();
        String name = result.get(2).toString();
        String units = result.get(1).toString();
        String description = result.get(3).toString();

        return new BacNetObject(device, instance, type, name, description, units);
    }

    private static DefaultTransport createTransport(MstpNetwork network, int timeout, int segTimeout) {
        DefaultTransport transport = new DefaultTransport(network);
        transport.setTimeout(timeout);
        transport.setSegTimeout(segTimeout);
        return transport;
    }

}
