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

import com.serotonin.bacnet4j.npdu.mstp.MasterNode;
import com.serotonin.bacnet4j.npdu.mstp.MstpNetwork;
import com.serotonin.bacnet4j.util.sero.JsscSerialPortInputStream;
import com.serotonin.bacnet4j.util.sero.JsscSerialPortOutputStream;
import com.serotonin.bacnet4j.util.sero.SerialPortWrapper;

import jssc.SerialPort;
import jssc.SerialPortException;

/**
 * Network builder which opens serial port connection using JSSC.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class JsscMstpNetworkBuilder extends MstpNetworkBuilder {

    public MstpNetwork build() throws SerialPortException {
        SerialPort serialPort = new SerialPort(getSerialPort());
        boolean opened = serialPort.openPort();

        if (!opened) {
            throw new SerialPortException(getSerialPort(), "build()", "Could not open port");
        }

        serialPort.setParams(getBaud(), getDataBits(), getStopBits(), getParity());

        JsscSerialPortInputStream in = new JsscSerialPortInputStream(serialPort);
        JsscSerialPortOutputStream out = new JsscSerialPortOutputStream(serialPort);

        final MasterNode node = new MasterNode(getSerialPort(), in, out, (byte) getStation(), 2);
        node.setMaxInfoFrames(5);
        node.setUsageTimeout(100);
        return new MstpNetwork(node, 0);
    }
}
