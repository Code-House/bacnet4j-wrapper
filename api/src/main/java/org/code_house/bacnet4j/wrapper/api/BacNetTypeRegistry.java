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
package org.code_house.bacnet4j.wrapper.api;

import java.util.Optional;

/**
 * PropertyType mapping registry - takes bacnet code - gives type representation.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public interface BacNetTypeRegistry {

    /**
     * Attempts to find property type based on its code.
     *
     * @param code Code of bacnet property type.
     * @return PropertyType of property or empty object.
     */
    Optional<BacNetType> lookup(int code);

    /**
     * Attempts to find property type based on it's type class.
     *
     * @param type Type representing bacnet type.
     * @return PropertyType of property or empty object.
     */
    <P extends BacNetElement, T, X extends BacNetType<P, T>> Optional<X> lookup(Class<X> type);

    /**
     * Confirms if given type instance is supported.
     *
     * @param type Type of bacnet property.
     * @return PropertyType of property or empty object.
     */
    <P extends BacNetElement, T, X extends BacNetType<P, T>> Optional<X> lookup(X type);

}
