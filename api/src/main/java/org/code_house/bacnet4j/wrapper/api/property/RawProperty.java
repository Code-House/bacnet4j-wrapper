package org.code_house.bacnet4j.wrapper.api.property;

import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.code_house.bacnet4j.wrapper.api.Type;

/**
 * @author dl02
 */
public class RawProperty implements Property {

    private final int code;
    private Type type;

    public RawProperty(int code) {
        this.code = code;
    }

    @Override
    public int getId() {
        return code;
    }

    @Override
    public Device getDevice() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Type getType() {
        return type;
    }
}
