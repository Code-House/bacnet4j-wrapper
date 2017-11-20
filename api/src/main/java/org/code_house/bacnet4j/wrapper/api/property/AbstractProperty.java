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
package org.code_house.bacnet4j.wrapper.api.property;

import org.code_house.bacnet4j.wrapper.api.*;

import java.util.Map;

/**
 * Property which have single value, scalar or basic representation in general.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public abstract class AbstractProperty<T extends BacNetElement> implements Property<T> {

    private final T parent;
    private final int id;
    private final PropertyType type;
    private final String name;
    private final String description;

    public AbstractProperty(T parent, PropertyType type, int id) {
        this(parent, type, id, "", "");
    }

    public AbstractProperty(T parent, PropertyType type, int id, String name, String description) {
        this.parent = parent;
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PropertyType getType() {
        return type;
    }


    @Override
    public BacNetValue get(Property property) {
        return parent.get(property);
    }

    @Override
    public Map<Property, BacNetValue> get(Property... properties) {
        return parent.get(properties);
    }

    @Override
    public Map<PropertyType, BacNetValue> get(PropertyType ... properties) {
        return parent.get(properties);
    }

    @Override
    public T getParent() {
        return parent;
    }

    @Override
    public BacNetContext getContext() {
        return parent.getContext();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + parent + "." + type + "." + id + "]";
    }

}
