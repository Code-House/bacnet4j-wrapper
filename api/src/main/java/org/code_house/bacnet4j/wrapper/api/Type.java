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

import com.serotonin.bacnet4j.type.enumerated.ObjectType;

/**
 * Bacnet property type.
 *
 * @author Łukasz Dywicki <luke@code-house.org>
 */
public enum Type {

    ANALOG_INPUT(ObjectType.analogInput),
    ANALOG_OUTPUT(ObjectType.analogOutput),
    ANALOG_VALUE(ObjectType.analogValue),
    BINARY_INPUT(ObjectType.binaryInput),
    BINARY_OUTPUT(ObjectType.binaryOutput),
    BINARY_VALUE(ObjectType.binaryValue);

    private ObjectType bacNetType;

    private Type(ObjectType bacNetType) {
        this.bacNetType = bacNetType;
    }

    public ObjectType getBacNetType() {
        return bacNetType;
    }

    @Override
    public String toString() {
        return bacNetType + " Type";
    }

    public static Type valueOf(ObjectType objectType) {
        for (Type type : values()) {
            if (type.bacNetType.equals(objectType)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unsuported bacnet object type " + objectType);
    }

}
