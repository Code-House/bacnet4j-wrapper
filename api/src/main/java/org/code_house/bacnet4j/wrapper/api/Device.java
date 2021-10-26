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
package org.code_house.bacnet4j.wrapper.api;

import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import java.util.Arrays;
import java.util.Objects;

/**
 * Representation of bacnet device.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class Device {

    private final int instanceNumber;
    private final byte[] address;
    private final int networkNumber;

    private String modelName = "";
    private String vendorName = "";
    private String name = "";


    public Device(int instanceNumber, byte[] address, int networkNumber) {
        this.instanceNumber = instanceNumber;
        this.address = address;
        this.networkNumber = networkNumber;
    }

    public Device(int instanceNumber, Address address) {
        this(instanceNumber, address.getMacAddress().getBytes(), address.getNetworkNumber().intValue());
    }

    public ObjectIdentifier getObjectIdentifier() {
        return new ObjectIdentifier(ObjectType.device, instanceNumber);
    }

    public int getInstanceNumber() {
        return instanceNumber;
    }

    public byte[] getAddress() {
        return address;
    }

    public int getNetworkNumber() {
        return networkNumber;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Address getBacNet4jAddress() {
        return new Address(networkNumber, address);
    }

    @Override
    public String toString() {
        return "Device [" + instanceNumber + " " + new OctetString(address) + " network " + networkNumber + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Device)) {
            return false;
        }
        Device device = (Device) o;
        return getInstanceNumber() == device.getInstanceNumber()
            && getNetworkNumber() == device.getNetworkNumber()
            && Arrays.equals(getAddress(), device.getAddress()) && Objects.equals(getModelName(), device.getModelName())
            && Objects.equals(getVendorName(), device.getVendorName())
            && Objects.equals(getName(), device.getName());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getInstanceNumber(),
            getNetworkNumber(),
            getModelName(),
            getVendorName(),
            getName()
        );
        result = 31 * result + Arrays.hashCode(getAddress());
        return result;
    }
}
