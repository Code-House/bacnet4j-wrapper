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
package org.code_house.bacnet4j.wrapper.ip;

import com.serotonin.bacnet4j.type.Encodable;
import org.code_house.bacnet4j.wrapper.api.BypassBacnetConverter;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.*;

/**
 * Simple class to run discovery across all network interfaces, fetch discovered devices properties and it's values.
 *
 * @author Łukasz Dywicki <luke@code-house.org>
 */
public class DiscoveryMain {

    public static void main(String[] args) throws Exception {
        // For each interface ...
        System.out.println("Fetching network interfaces");
        List<String> interfaceIPs = new ArrayList<>();
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
            NetworkInterface networkInterface = en.nextElement();
            if (!networkInterface.isLoopback()) {

                // .. and for each address ...
                for (Iterator<InterfaceAddress> it = networkInterface.getInterfaceAddresses().iterator(); it.hasNext();) {

                    // ... get IP and Subnet
                    InterfaceAddress interfaceAddress = it.next();

                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast != null) {
                        interfaceIPs.add(broadcast.getHostAddress());
                    }
                }
            }
        }

        if (interfaceIPs.isEmpty()) {
            System.out.println("No broadcast interfaces found");
        }

        for (String broadcast : interfaceIPs) {
            System.out.println("Fetching devices for " + broadcast + " addres with 30 second timeout");
            BacNetIpClient client = new BacNetIpClient(broadcast, 1339);
            client.start();

            Set<Device> devices = client.discoverDevices(30000L);
            if (devices.isEmpty()) {
                System.out.println(" => No Devices found");
            } else {
                for (Device device : devices) {
                    System.out.println("  => Device id " + device.getInstanceNumber());
                    System.out.println("     Metadata");
                    System.out.println("       Address: " + device.getHostAddress() + ":" + device.getPort());
                    System.out.println("       Name: " + device.getName());
                    System.out.println("       Model: " + device.getModelName());
                    System.out.println("       Vendor: " + device.getVendorName());

                    List<Property> properties = client.getDeviceProperties(device);
                    if (properties.isEmpty()) {
                        System.out.println("      => No properties found");
                    } else {
                        System.out.println("      => Properties:");
                        for (Property property : client.getDeviceProperties(device)) {
                            Encodable propertyValue = client.getPropertyValue(property, new BypassBacnetConverter());
                            System.out.println(
                                String.format("          => Type %s id: %d, present value '%s' type %s",
                                    property.getType().name(),
                                    property.getId(),
                                    propertyValue,
                                    propertyValue != null ? propertyValue.getClass().getName() : "<null>"
                                )
                            );
                            System.out.println("             Metadata");
                            System.out.println("               Name: " + property.getName());
                            System.out.println("               Units: " + property.getUnits());
                            System.out.println("               Description: " + property.getDescription());
                        }
                    }

                }
            }

            client.stop();
        }

        System.out.println("Discovery complete");
    }
}
