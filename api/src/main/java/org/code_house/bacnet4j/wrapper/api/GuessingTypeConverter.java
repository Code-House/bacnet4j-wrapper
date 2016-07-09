/*
 * (C) Copyright 2016 Code-House and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.code_house.bacnet4j.wrapper.api;

import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.BinaryPV;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.Real;

/**
 * @author Łukasz Dywicki <luke@code-house.org>
 */
public class GuessingTypeConverter implements TypeMapping<Object> {

    private final Type type;

    public  GuessingTypeConverter(Type type) {
        this.type = type;
    }

    @Override
    public Object fromBacNet(Encodable encodable) {
        if (type == Type.ANALOG_INPUT) {
            return new Float(((Real) encodable).floatValue());
        }
        return encodable.toString();
    }

    @Override
    public Encodable toBacNet(Object object) {
        if (object instanceof Integer) {
            return new Real((Integer) object);
        } else if (object instanceof String) {
            return new CharacterString((String) object);
        } else if (object instanceof Float) {
            return new Real((Float) object);
        } else if (object instanceof Boolean) {
            return (Boolean) object ? BinaryPV.active : BinaryPV.inactive;
        } else {
            return new CharacterString(object.toString());
        }
    }
}
