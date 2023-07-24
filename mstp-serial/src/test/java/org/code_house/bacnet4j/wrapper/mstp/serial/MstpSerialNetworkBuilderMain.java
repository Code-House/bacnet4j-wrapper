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

import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.DeviceDiscoveryListener;
import org.code_house.bacnet4j.wrapper.mstp.BacNetMstpClient;
import org.code_house.bacnet4j.wrapper.mstp.MstpNetworkBuilder;

/**
 * Test of mstp client with serial adapter based on purejavacomm.
 */
class MstpSerialNetworkBuilderMain {

  public static void main(String[] args) throws Exception {
    BacNetMstpClient client = new BacNetMstpClient(
      new MstpSerialNetworkBuilder(new SerialPortManager())
        .withSerialPort("/dev/ttyUSB0")
        .withBaud(9600)
        .withDataBits((short) 8)
        .withStopBits((short) 1)
        .build(),
      1339
    );
    client.listenForDevices(new DeviceDiscoveryListener() {
      @Override
      public void deviceDiscovered(Device device) {
        System.out.println("Device discovered " + device);
      }
    });

    client.start();
    System.in.read();
    client.stop();;
  }

}