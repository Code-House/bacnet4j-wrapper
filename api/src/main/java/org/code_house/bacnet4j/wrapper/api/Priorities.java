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
package org.code_house.bacnet4j.wrapper.api;

import java.util.*;

/**
 * Constants related to bacnet priorities.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public interface Priorities {

    Priority PRIORITY_1 = new DefaultPriority(1, "Manual-Life Safety");
    Priority PRIORITY_2 = new DefaultPriority(2, "Automatic-Life Safety");
    Priority PRIORITY_3 = new DefaultPriority(3, "Available");
    Priority PRIORITY_4 = new DefaultPriority(4, "Available");
    Priority PRIORITY_5 = new DefaultPriority(5, "Critical Equipment Control");
    Priority PRIORITY_6 = new DefaultPriority(6, "Minimum On/Off");
    Priority PRIORITY_7 = new DefaultPriority(7, "Available");
    Priority PRIORITY_8 = new DefaultPriority(8, "Manual Operator");
    Priority PRIORITY_9 = new DefaultPriority(9, "Available");
    Priority PRIORITY_10 = new DefaultPriority(10, "Available");
    Priority PRIORITY_11 = new DefaultPriority(11, "Available");
    Priority PRIORITY_12 = new DefaultPriority(12, "Available");
    Priority PRIORITY_13 = new DefaultPriority(13, "Available");
    Priority PRIORITY_14 = new DefaultPriority(14, "Available");
    Priority PRIORITY_15 = new DefaultPriority(15, "Available");
    Priority PRIORITY_16 = new DefaultPriority(16, "Available");

    Map<Integer, Priority> PRIORITIES = Collections.unmodifiableMap(new HashMap<Integer, Priority>() {{
        put(1, PRIORITY_1);
        put(2, PRIORITY_2);
        put(3, PRIORITY_3);
        put(4, PRIORITY_4);
        put(5, PRIORITY_5);
        put(6, PRIORITY_6);
        put(7, PRIORITY_7);
        put(8, PRIORITY_8);
        put(9, PRIORITY_9);
        put(10, PRIORITY_10);
        put(11, PRIORITY_11);
        put(12, PRIORITY_12);
        put(13, PRIORITY_13);
        put(14, PRIORITY_14);
        put(15, PRIORITY_15);
        put(16, PRIORITY_16);
    }});

    static Optional<Priority> get(int priority) {
        return Optional.ofNullable(PRIORITIES.get(priority));
    }

}
