/*
 * (C) Copyright 2023 Code-House and others.
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
package org.code_house.bacnet4j.wrapper.mstp.serial;

import com.serotonin.bacnet4j.npdu.mstp.MasterNode;
import com.serotonin.bacnet4j.npdu.mstp.MstpNetwork;
import com.serotonin.bacnet4j.util.sero.SerialPortWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.code_house.bacnet4j.wrapper.mstp.MstpNetworkBuilder;
import purejavacomm.CommPort;
import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;

public class MstpSerialNetworkBuilder extends MstpNetworkBuilder {

  private final SerialPortManager serialPortManager;

  public MstpSerialNetworkBuilder(SerialPortManager serialPortManager) {
    this.serialPortManager = serialPortManager;
  }

  @Override
  public MstpNetwork build() throws Exception {
    CommPortIdentifier identifier = serialPortManager.getIdentifier(getSerialPort());

    MasterNode node = new MasterNode(new ManagedSerialPort(identifier, getBaud(), getDataBits(), getStopBits(), getParity()),
      (byte)this.getStation(), 2);
    node.setMaxInfoFrames(5);
    node.setUsageTimeout(100);
    return new MstpNetwork(node, 0);
  }

  static class ManagedSerialPort extends SerialPortWrapper {

    private final CommPortIdentifier identifier;
    private final int baud;
    private final int dataBits;
    private final int stopBits;
    private final int parity;
    private SerialPort port;

    ManagedSerialPort(CommPortIdentifier port, int baud, int dataBits, int stopBits, int parity) {
      this.identifier = port;
      this.baud = baud;
      this.dataBits = dataBits;
      this.stopBits = stopBits;
      this.parity = parity;
    }

    @Override
    public void close() throws Exception {
      if (port != null) {
        port.close();
      }
    }

    @Override
    public void open() throws Exception {
      CommPort port = identifier.open("bacnet4j-wrapper", 2000);
      if (!(port instanceof SerialPort)) {
        throw new IllegalStateException("Given port is not a serial port");
      }

      this.port = (SerialPort) port;
      this.port.setSerialPortParams(baud, dataBits, stopBits, parity);
    }

    @Override
    public InputStream getInputStream() {
      if (port != null) {
        try {
          return port.getInputStream();
        } catch (IOException e) {
          throw new RuntimeException("Could not open input stream for port " + identifier.getName(), e);
        }
      }
      return null;
    }

    @Override
    public OutputStream getOutputStream() {
      if (port != null) {
        try {
          return port.getOutputStream();
        } catch (IOException e) {
          throw new RuntimeException("Could not open output stream for port " + identifier.getName(), e);
        }
      }
      return null;
    }

    @Override
    public String getCommPortId() {
      return identifier.getName();
    }
  }
}
