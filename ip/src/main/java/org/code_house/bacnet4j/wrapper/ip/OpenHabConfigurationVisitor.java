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
package org.code_house.bacnet4j.wrapper.ip;

import com.serotonin.bacnet4j.type.Encodable;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.code_house.bacnet4j.wrapper.api.Type;

import java.util.List;

import static java.lang.String.format;

/**
 * Very simple configuration configuration generator - dumps network to openhab items syntax.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
class OpenHabConfigurationVisitor implements Visitor {

    private final boolean groups;
    private final boolean tags;
    private Device device;

    OpenHabConfigurationVisitor(boolean groups, boolean tags) {
        this.groups = groups;
        this.tags = tags;
    }

    @Override
    public Flag visit(Device device) {
        this.device = device;
        return Flag.CONTNUE;
    }

    @Override
    public Flag visit(Property property) {
        System.out.println(format("%s %s \"%s\" %s %s {bacnet=\"device=%d,type=%s,id=%d\"}",
            openhabType(property),
            openhabName(property),
            openhabLabel(property),
            groups ? openhabGroups(property) : "",
            tags ? openhabTags(property) : "",
            device.getInstanceNumber(),
            property.getType().getName(),
            property.getId()
        ));

        return Flag.SKIP;
    }

    @Override
    public Flag visit(Encodable propertyValue) {
        return Flag.SKIP;
    }

    private static String openhabType(Property property) {
        switch (property.getType()) {
            case BINARY_INPUT:
            case BINARY_OUTPUT:
            case BINARY_VALUE:
                return "Switch";
        }

        return "Number";
    }

    private static String openhabName(Property property) {
        return property.getName()
            .replace(" ", "_")
            .replaceAll("\\)$", "")
            .replaceAll("}$", "")
            .replaceAll("]$", "")
            .replace("(", "_")
            .replace(")", "_")
            .replace("{", "_")
            .replace("}", "_")
            .replace("[", "_")
            .replace("]", "_");
    }

    private static String openhabLabel(Property property) {
        return property.getName();
    }

    private String openhabGroups(Property property) {
        return "(" +
            "BacNet" + "," +
            "BacNet_" + property.getUnits()  + "," +
            "Device_" + device.getInstanceNumber() + "," +
            openhabName(property) + "_" + property.getId() + "," +
            property.getType().getName() + "_" + property.getId() +
        ")";
    }

    private Object openhabTags(Property property) {
        return "[" +
            "\"Device_" + device.getInstanceNumber() + "\"," +
            "\"" + property.getType().getName() +"\"" +
        "]";
    }
}
