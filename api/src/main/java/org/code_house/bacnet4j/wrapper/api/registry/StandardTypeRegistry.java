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

import org.code_house.bacnet4j.wrapper.api.type.SimpleType;

/**
 * Type registry covering some of standard bacnet property types.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class StandardTypeRegistry extends SimpleTypeRegistry {

    public StandardTypeRegistry() {
        register(new SimpleType(0, "Analog Input"));
        register(new SimpleType(1, "Analog Output"));
        register(new SimpleType(2, "Analog Value"));

        register(new SimpleType(3, "Binary Input"));
        register(new SimpleType(4, "Binary Output"));
        register(new SimpleType(5, "Binary Value"));

        register(new SimpleType(6, "Calendar"));
        register(new SimpleType(7, "Command"));
        register(new SimpleType(8, "Device"));

        register(new SimpleType(10, "File"));

        register(new SimpleType(13, "Multi State Input"));
        register(new SimpleType(14, "Multi State Output"));
        register(new SimpleType(19, "Multi State Value"));

        register(new SimpleType(15, "Notification Class"));

        register(new SimpleType(17, "Schedule"));

        register(new SimpleType(20, "Trend Log"));

        register(new SimpleType(21, "life Safety Point"));
        register(new SimpleType(22, "life Safety Zone"));

        register(new SimpleType(39, "Bit String Value"));
        register(new SimpleType(40, "Character String Value"));
        register(new SimpleType(47, "Octet String Value"));

        register(new SimpleType(45, "Integer Value"));
        register(new SimpleType(46, "Large Analog Value"));
        register(new SimpleType(48, "Positive Integer Value"));

        register(new SimpleType(43, "Date Time Pattern Value"));
        register(new SimpleType(44, "Date Time Value"));

        register(new SimpleType(41, "Date Pattern Value"));
        register(new SimpleType(42, "Date Value"));

        register(new SimpleType(49, "Time Pattern Value"));
        register(new SimpleType(50, "Time Value"));

        register(new SimpleType(28, "Load Control"));
        register(new SimpleType(53, "Channel"));
        register(new SimpleType(54, "Lighting Output"));
        register(new SimpleType(55, "Binary Lighting Output"));
    }

}
