/*
 * (C) Copyright 2017 Code-House and others.
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
package org.code_house.bacnet4j.wrapper.api.type;

import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.code_house.bacnet4j.wrapper.api.Type;
import org.code_house.bacnet4j.wrapper.api.property.SimpleProperty;

import java.util.Objects;

/**
 * Type representing scalar value.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class SimpleType implements Type {

    private final int code;
    private final String name;

    public SimpleType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public Property create(Device device, int id, String name, String description, String units) {
        return new SimpleProperty(device, this, id, name, description, units);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleType)) return false;
        SimpleType that = (SimpleType) o;
        return getCode() == that.getCode() &&
            Objects.equals(getName(), that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getName());
    }

    @Override
    public String toString() {
        return "SimpleType(" + getCode() + ":" + getName() + ")";
    }
}
