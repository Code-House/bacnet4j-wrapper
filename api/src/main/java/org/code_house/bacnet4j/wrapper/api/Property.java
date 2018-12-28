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

import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;

public class Property {

    private final Device device;
    private final int id;
    private final String name;
    private final String description;
    private final String units;
    private final Type type;

    public Property(Device device, int id, Type type) {
        this(device, id, "", "", "", type);
    }

    public Property(Device device, int id, String name, String description, String units, Type type) {
        this.device = device;
        this.id = id;
        this.name = name;
        this.description = description;
        this.units = units;
        this.type = type;
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

    public ObjectIdentifier getBacNet4jIdentifier() {
        return new ObjectIdentifier(type.getBacNetType(), id);
    }

    @Override
    public String toString() {
        return "Property[" + device.getInstanceNumber() + "." + type.name() + "." + id + "]";
    }

}
