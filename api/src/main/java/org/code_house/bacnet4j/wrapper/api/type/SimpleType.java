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

import org.code_house.bacnet4j.wrapper.api.BacNetElement;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.code_house.bacnet4j.wrapper.api.property.RawProperties;
import org.code_house.bacnet4j.wrapper.api.property.SimpleProperty;

/**
 * Type representing scalar value.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class SimpleType<T extends Device> extends AbstractType<T> {

    public SimpleType(int code, String name) {
        super(code, name);
    }

    @Override
    public Property create(Device device, int instanceNumber, String name, String description) {
        String units = (String) device.get(RawProperties.UNITS);
        return new SimpleProperty(device, this, instanceNumber, name, description, units);
    }

}
