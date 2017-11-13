package org.code_house.bacnet4j.wrapper.api.property;

import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Type;
import org.code_house.bacnet4j.wrapper.api.property.AbstractProperty;

/**
 * @author dl02
 */
public class ScheduleProperty extends AbstractProperty {

    public ScheduleProperty(Device device, Type type, int id) {
        this(device, type, id, "", "");
    }

    public ScheduleProperty(Device device, Type type, int id, String name, String description) {
        super(device, type, id, name, description);
    }


}
