/*
 * (C) Copyright 2022 Code-House and others.
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
import java.util.Objects;

public class BacNetObject {

    private final Device device;
    private final int id;
    private final Type type;

    private final String name;
    private final String description;
    private final String units;

    public BacNetObject(Device device, int id, Type type) {
        this(device, id, type, "", "", "");
    }

    public BacNetObject(Device device, int id, Type type, String name, String description, String units) {
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

    public Type getType() {
        return type;
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

    public ObjectIdentifier getBacNet4jIdentifier() {
        return new ObjectIdentifier(type.getBacNetType(), id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BacNetObject)) {
            return false;
        }
        BacNetObject that = (BacNetObject) o;
        return getId() == that.getId() && Objects.equals(getDevice(), that.getDevice())
            && getType() == that.getType() && Objects.equals(name, that.name)
            && Objects.equals(description, that.description) && Objects.equals(units,
            that.units);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDevice(), getId(), getType(), name, description, units);
    }

    @Override
    public String toString() {
        return "BacNet Object[" + device.getInstanceNumber() + "." + type.name() + "." + id + "]";
    }

}
