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
package org.code_house.bacnet4j.wrapper.api.property;

import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.code_house.bacnet4j.wrapper.api.Type;

/**
 * Property which have single value, scalar or basic representation in general.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class SimpleProperty implements Property {

    private final Device device;
    private final int id;
    private final Type type;
    private final String name;
    private final String description;
    private final String units;

    public SimpleProperty(Device device, Type type, int id) {
        this(device, type, id, "", "", "");
    }

    public SimpleProperty(Device device, Type type, int id, String name, String description, String units) {
        this.device = device;
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.units = units;
    }

    public Device getDevice() {
        return device;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUnits() {
        return units;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "SimpleProperty[" + device.getInstanceNumber() + "." + type + "." + id + "]";
    }

}
