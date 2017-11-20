package org.code_house.bacnet4j.wrapper.api.type;

import org.code_house.bacnet4j.wrapper.api.BacNetType;
import org.code_house.bacnet4j.wrapper.api.Context;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.code_house.bacnet4j.wrapper.api.property.ScheduleProperty;

/**
 * @author dl02
 */
public class ScheduleType extends AbstractPropertyType<Device, ScheduleProperty> {

    public static final int IDENTIFIER = 17;
    public static final ScheduleType INSTANCE = new ScheduleType();

    private ScheduleType() {
        super(IDENTIFIER, "Schedule");
    }

    @Override
    public ScheduleProperty create(Device device, int instanceNumber, Context context) {
        return new ScheduleProperty(device, this, instanceNumber);
    }
}
