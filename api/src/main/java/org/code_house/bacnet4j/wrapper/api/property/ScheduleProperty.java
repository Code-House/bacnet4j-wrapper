package org.code_house.bacnet4j.wrapper.api.property;

import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.PropertyType;

/**
 * @author dl02
 */
public class ScheduleProperty extends AbstractProperty<Device> {

    public ScheduleProperty(Device device, PropertyType type, int id) {
        this(device, type, id, "", "");
    }

    public ScheduleProperty(Device device, PropertyType type, int id, String name, String description) {
        super(device, type, id, name, description);
    }


}
