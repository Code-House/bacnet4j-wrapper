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
package org.code_house.bacnet4j.wrapper.api.registry;

import org.code_house.bacnet4j.wrapper.api.BacNetClientException;
import org.code_house.bacnet4j.wrapper.api.Type;
import org.code_house.bacnet4j.wrapper.api.TypeRegistry;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Basic implementation of type registry.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
class SimpleTypeRegistry implements TypeRegistry {

    private Map<Integer, Type> types = new ConcurrentHashMap<>();

    @Override
    public Optional<Type> lookup(int code) {
        return Optional.ofNullable(types.get(code));
    }

    void register(Type type) {
        if (types.containsKey(type.getCode())) {
            throw new BacNetClientException("Code " + type.getCode() + " already reserved by " + types.get(type.getCode()));
        }
        this.types.put(type.getCode(), type);
    }

}
