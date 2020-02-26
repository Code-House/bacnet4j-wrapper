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
package org.code_house.bacnet4j.wrapper.mstp;

import org.code_house.bacnet4j.wrapper.api.Device;
import com.serotonin.bacnet4j.type.constructed.Address;

/**
 * Device variant which assumes MSTP network operation,
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public class MstpDevice extends Device {

    public MstpDevice(int instanceNumber, byte[] address, int networkNumber) {
        super(instanceNumber, address, networkNumber);
    }

    public MstpDevice(int instanceNumber, Address address) {
        super(instanceNumber, address);
    }

}
