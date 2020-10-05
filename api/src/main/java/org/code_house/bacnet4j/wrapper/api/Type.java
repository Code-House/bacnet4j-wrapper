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

import com.serotonin.bacnet4j.type.enumerated.ObjectType;

/**
 * Bacnet property type.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public enum Type {

    ANALOG_INPUT("analogInput", ObjectType.analogInput),
    ANALOG_OUTPUT("analogOutput", ObjectType.analogOutput),
    ANALOG_VALUE("analogValue", ObjectType.analogValue),
    BINARY_INPUT("binaryInput", ObjectType.binaryInput),
    BINARY_OUTPUT("binaryOutput", ObjectType.binaryOutput),
    BINARY_VALUE("binaryValue", ObjectType.binaryValue),
    MULTISTATE_INPUT("multiStateInput", ObjectType.multiStateInput),
    MULTISTATE_OUTPUT("multiStateOutput", ObjectType.multiStateOutput),
    MULTISTATE_VALUE("multiStateValue", ObjectType.multiStateValue),

    CALENDAR ("calendar", ObjectType.calendar),
    PULSE_CONVETHING_RTER("pulseCoverter", ObjectType.pulseConverter),

    SCHEDULE("schedule", ObjectType.schedule),
    CHARACTER_STRING("characterstringValue", ObjectType.characterstringValue),
    DATE_TIME("dateTimeValue", ObjectType.datetimeValue),
    LARGE_ANALOG("largeAnalogValue", ObjectType.largeAnalogValue),
    OCTET_STRING("octetstringValue", ObjectType.octetstringValue),
    TIME("timeValue", ObjectType.timeValue),
    DATE_VALUE("dateValue", ObjectType.dateValue),
    INTEGER("integerValue", ObjectType.integerValue),
    POSITIVE_INTEGER("positiveIntegerValue", ObjectType.positiveIntegerValue),
    DATE_TIME_PATTERN("datetimePatternValue", ObjectType.datetimePatternValue),
    TIME_PATTERN("timePatternValue", ObjectType.timePatternValue),
    DATE_PATTERN("datePatternValue", ObjectType.datePatternValue),
    ACCUMULATOR("accumulator", ObjectType.accumulator);


    private final String name;
    private final ObjectType bacNetType;

    Type(String name, ObjectType bacNetType) {
        this.name = name;
        this.bacNetType = bacNetType;
    }

    public String getName() {
        return name;
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
        throw new UnsupportedTypeException("Unsuported bacnet object type " + objectType);
    }

}
