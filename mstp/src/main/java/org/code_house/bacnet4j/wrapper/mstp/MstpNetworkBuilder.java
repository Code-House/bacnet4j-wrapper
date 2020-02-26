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

import com.serotonin.bacnet4j.npdu.mstp.MstpNetwork;

/**
 * Common mstp related options which include serial parameters.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public abstract class MstpNetworkBuilder {

    private String serialPort;
    private int station;

    private int baud = 38_400;
    private short dataBits = 8;
    private short stopBits = 1;
    private short parity = 0;

    public MstpNetworkBuilder withStation(int station) {
        this.station = station;
        return this;
    }

    public int getStation() {
        return station;
    }

    public MstpNetworkBuilder withSerialPort(String port) {
        this.serialPort = port;
        return this;
    }

    public String getSerialPort() {
        return serialPort;
    }

    public MstpNetworkBuilder withBaud(int baud) {
        this.baud = baud;
        return this;
    }

    public int getBaud() {
        return baud;
    }

    public MstpNetworkBuilder withDataBits(short dataBits) {
        this.dataBits = dataBits;
        return this;
    }

    public short getDataBits() {
        return dataBits;
    }

    public MstpNetworkBuilder withStopBits(short stopBits) {
        this.stopBits = stopBits;
        return this;
    }

    public short getStopBits() {
        return stopBits;
    }

    public MstpNetworkBuilder withParity(short parity) {
        this.parity = parity;
        return this;
    }

    public short getParity() {
        return parity;
    }

    public abstract MstpNetwork build() throws Exception;

}
