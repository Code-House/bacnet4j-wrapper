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

import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Simple class to run discovery across all network interfaces, fetch discovered devices properties and print out openhab
 * configuration for it.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class GeneratorMain {

    public static void main(String[] args) throws Exception {
        PrintStream output = args.length < 6 ? System.out : new PrintStream(new FileOutputStream(args[5]));
        new NetworkProgram(
            new OpenHabConfigurationVisitor(
                args.length < 4 || Boolean.parseBoolean(args[3]),
                args.length < 5 || Boolean.parseBoolean(args[4]),
                output
            )
        ).run(args);

        output.close();
    }
}
