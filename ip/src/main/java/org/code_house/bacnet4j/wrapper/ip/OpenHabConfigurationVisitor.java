/*
 * (C) Copyright 2017 Code-House and others.
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
package org.code_house.bacnet4j.wrapper.ip;

import com.serotonin.bacnet4j.type.Encodable;
import org.code_house.bacnet4j.wrapper.api.BacNetObject;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;

import java.io.PrintStream;

import static java.lang.String.format;

/**
 * Very simple configuration configuration generator - dumps network to openhab items syntax.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
class OpenHabConfigurationVisitor implements Visitor {

    private final boolean groups;
    private final boolean tags;
    private final PrintStream output;
    private Device device;

    OpenHabConfigurationVisitor(boolean groups, boolean tags, PrintStream writer) {
        this.groups = groups;
        this.tags = tags;
        this.output = writer;
    }

    @Override
    public Flag visit(Device device) {
        this.device = device;
        return Flag.CONTINUE;
    }

    @Override
    public Flag visit(BacNetObject object) {
        output.println(format("%s %s \"%s\" %s %s {bacnet=\"device=%d,type=%s,id=%d\"}",
            openhabType(object),
            openhabName(object),
            openhabLabel(object),
            groups ? openhabGroups(object) : "",
            tags ? openhabTags(object) : "",
            device.getInstanceNumber(),
            object.getType().getName(),
            object.getId()
        ));

        return Flag.SKIP;
    }

    @Override
    public Flag visit(Encodable propertyValue) {
        return Flag.SKIP;
    }

    @Override
    public Flag visitProperty(String property, Encodable propertyValue) {
        return Flag.SKIP;
    }

    private static String openhabType(BacNetObject object) {
        switch (object.getType()) {
            case BINARY_INPUT:
            case BINARY_OUTPUT:
            case BINARY_VALUE:
                return "Switch";
        }

        return "Number";
    }

    private static String openhabName(BacNetObject object) {
        return object.getName().trim()
            .replaceAll("[^a-zA-Z0-9\\s]", "")
            .replaceAll("\\s", "_")
            // in the end - we replace non ascii characters
            .replaceAll("[^\\x00-\\x7F]", "_");
    }

    private static String openhabLabel(BacNetObject object) {
        return object.getName();
    }

    private String openhabGroups(BacNetObject object) {
        return "(" +
            "BacNet" + "," +
            "BacNet_" + object.getUnits()  + "," +
            "Device_" + device.getInstanceNumber() + "," +
            openhabName(object) + "_" + object.getId() + "," +
            object.getType().getName() + "_" + object.getId() +
        ")";
    }

    private Object openhabTags(BacNetObject object) {
        return "[" +
            "\"Device_" + device.getInstanceNumber() + "\"," +
            "\"" + object.getType().getName() +"\"" +
        "]";
    }
}
