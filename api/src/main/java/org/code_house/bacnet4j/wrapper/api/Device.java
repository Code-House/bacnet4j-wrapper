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
package org.code_house.bacnet4j.wrapper.api;

import org.code_house.bacnet4j.wrapper.api.primitives.BacNetObjectIdentifier;
import org.code_house.bacnet4j.wrapper.api.type.DeviceType;

import java.util.Map;

/**
 * Representation of bacnet device.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class Device implements BacNetElement<BacNetNetwork> {

    private final BacNetNetwork parent;
    private final BacNetAddress address;
    private final int instanceNumber;

    private String modelName = "";
    private String vendorName = "";
    private String name = "";


    public Device(BacNetNetwork parent, int instanceNumber, BacNetAddress address) {
        this.parent = parent;
        this.instanceNumber = instanceNumber;
        this.address = address;
    }

    public BacNetObjectIdentifier getObjectIdentifier() {
        return new BacNetObjectIdentifier(DeviceType.IDENTIFIER, instanceNumber);
    }

    public int getInstanceNumber() {
        return instanceNumber;
    }

    public BacNetAddress getAddress() {
        return address;
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

    @Override
    public <X> BacNetValue<X> get(Property property) {
        return null;
    }

    @Override
    public Map<Property, BacNetValue> get(Property... properties) {
        return null;
    }

    @Override
    public Map<PropertyType, BacNetValue> get(PropertyType... properties) {
        return null;
    }

    @Override
    public BacNetNetwork getParent() {
        return null;
    }

    @Override
    public BacNetContext getContext() {
        return null;
    }

    @Override
    public String toString() {
        return "Device [" + instanceNumber + " " + address + "]";
    }

}
