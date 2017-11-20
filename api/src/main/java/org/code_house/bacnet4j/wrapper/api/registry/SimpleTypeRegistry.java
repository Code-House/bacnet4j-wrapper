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

import org.code_house.bacnet4j.wrapper.api.*;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Basic implementation of type registry.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
class SimpleTypeRegistry implements BacNetTypeRegistry {

    private Map<Integer, BacNetType> types = new ConcurrentHashMap<>();

    @Override
    public Optional<BacNetType> lookup(int code) {
        return Optional.ofNullable(types.get(code));
    }

    @Override
    public <P extends BacNetElement, T, X extends BacNetType<P, T>> Optional<X> lookup(Class<X> type) {
        for (BacNetType registeredType : types.values()) {
            if (registeredType.getClass() == type) {
                return Optional.of((X) registeredType);
            }
        }
        return Optional.empty();
    }

    @Override
    public <P extends BacNetElement, T, X extends BacNetType<P, T>> Optional<X> lookup(X type) {
        if (types.containsValue(type)) {
            return Optional.of(type);
        }
        return Optional.empty();
    }

    void register(BacNetType type) {
        if (types.containsKey(type.getCode())) {
            throw new BacNetClientException("Code " + type.getCode() + " already reserved by " + types.get(type.getCode()));
        }
        this.types.put(type.getCode(), type);
    }

}
