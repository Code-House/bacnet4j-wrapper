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

import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.BinaryPV;
import com.serotonin.bacnet4j.type.primitive.*;

import java.lang.Boolean;

/**
 * Mappings between java/bacnet and vice versa data types.
 *
 * @author Łukasz Dywicki <luke@code-house.org>
 */
public class TypeMappings {

    public static final TypeMapping<CharacterString, String> STRING = new TypeMapping<CharacterString, String>() {
        @Override
        public String fromBacNet(CharacterString encodable) {
            return encodable.getValue();
        }
        @Override
        public CharacterString toBacNet(String value) {
            return new CharacterString(value);
        }

        @Override
        public Class<String> getJavaType() {
            return String.class;
        }
    };

    public static final TypeMapping<Real, Float> REAL = new TypeMapping<Real, Float>() {
        @Override
        public Float fromBacNet(Real encodable) {
            return encodable.floatValue();
        }
        @Override
        public Real toBacNet(Float value) {
            return new Real(value);
        }

        @Override
        public Class<Float> getJavaType() {
            return Float.class;
        }
    };

    public static final TypeMapping<UnsignedInteger, Integer> INTEGER = new TypeMapping<UnsignedInteger, Integer>() {
        @Override
        public Integer fromBacNet(UnsignedInteger encodable) {
            return encodable.intValue();
        }

        @Override
        public UnsignedInteger toBacNet(Integer value) {
            return new UnsignedInteger(value);
        }

        @Override
        public Class<Integer> getJavaType() {
            return Integer.class;
        }
    };

    private static final TypeMapping<BinaryPV, Boolean> BOOLEAN = new TypeMapping<BinaryPV, Boolean>() {
        @Override
        public Boolean fromBacNet(BinaryPV encodable) {
            return BinaryPV.active == encodable;
        }

        @Override
        public Encodable toBacNet(Boolean value) {
            return value ? BinaryPV.active : BinaryPV.inactive;
        }

        @Override
        public Class<Boolean> getJavaType() {
            return Boolean.class;
        }
    };

    public static TypeMapping fromBacNet(Class<? extends Encodable> encodable) {
        if (BinaryPV.class.isAssignableFrom(encodable)) {
            return BOOLEAN;
        } else if (Real.class.isAssignableFrom(encodable)) {
            return REAL;
        } else if (UnsignedInteger.class.isAssignableFrom(encodable)) {
            return INTEGER;
        } else if (CharacterString.class.isAssignableFrom(encodable)) {
            return STRING;
        }
        throw new IllegalArgumentException("Unsupported type " + encodable);
    }

    public static TypeMapping fromJava(Class<?> aClass) {
        if (Boolean.class.isAssignableFrom(aClass)) {
            return BOOLEAN;
        } else if (Float.class.isAssignableFrom(aClass)) {
            return REAL;
        } else if (Integer.class.isAssignableFrom(aClass)) {
            return INTEGER;
        } else if (String.class.isAssignableFrom(aClass)) {
            return STRING;
        }
        throw new IllegalArgumentException("Unsupported type " + aClass);
    }
}
