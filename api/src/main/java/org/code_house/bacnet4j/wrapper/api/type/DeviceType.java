package org.code_house.bacnet4j.wrapper.api.type;

import org.code_house.bacnet4j.wrapper.api.BacNetNetwork;
import org.code_house.bacnet4j.wrapper.api.Context;
import org.code_house.bacnet4j.wrapper.api.Device;

public class DeviceType extends AbstractType<BacNetNetwork, Device> {

    public static final int IDENTIFIER = 8;

    public static final DeviceType INSTANCE = new DeviceType();

    private DeviceType() {
        super(IDENTIFIER, "Device");
    }

    @Override
    public Device create(BacNetNetwork parent, int instanceNumber, Context context) {
        /*
        Map<Property, BacNetValue> values = parent.get(
            new DeviceProperty(instanceNumber, PropertyTypes.vendor_name),
            new DeviceProperty(instanceNumber, PropertyTypes.vendor_identifier)
        );
        */
        return new Device(parent, instanceNumber, null);
    }
}
