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
package org.code_house.bacnet4j.wrapper.ip;

import com.serotonin.bacnet4j.npdu.NetworkUtils;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;

import java.io.PrintStream;
import java.util.StringJoiner;

import static java.lang.String.format;

/**
 * Very simple visitor - dumps network structure to csv file.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
class CsvVisitor implements Visitor {

    private final PrintStream output;

    CsvVisitor(PrintStream writer) {
        this.output = writer;

        output.println("Network,Address,DeviceId,ObjectType,ObjectIdentifier,Name,Units,Description");
    }

    @Override
    public Flag visit(Device device) {
        return Flag.CONTNUE;
    }

    @Override
    public Flag visit(Property property) {
        Device device = property.getDevice();
        String line = new StringJoiner(",")
                .add("" + device.getNetworkNumber())
                .add(quote(NetworkUtils.toString(new OctetString(device.getAddress()))))
                .add("" + device.getInstanceNumber())
                .add(property.getType().getName())
                .add("" + property.getId())
                .add(quote(property.getName()))
                .add(quote(property.getUnits()))
                .add(quote(property.getDescription()))
            .toString();
        output.println(line);
        return Flag.SKIP;
    }

    private String quote(String value) {
        return "\"" + value.replace("\"", "\\\"") + "\"";
    }

    @Override
    public Flag visit(Encodable propertyValue) {
        return Flag.SKIP;
    }

}
