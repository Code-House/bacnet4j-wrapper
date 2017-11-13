package org.code_house.bacnet4j.wrapper.api.type;

import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.code_house.bacnet4j.wrapper.api.property.ScheduleProperty;

/**
 * @author dl02
 */
public class ScheduleType<T extends Device> extends AbstractType<T> {

    public ScheduleType() {
        super(17, "Schedule");
    }

    @Override
    public Property create(Device device, int instanceNumber, String name, String description) {
        return new ScheduleProperty(device, this, instanceNumber, name, description);
    }
}
