package org.code_house.bacnet4j.wrapper.ip;

import com.serotonin.bacnet4j.type.Encodable;
import org.code_house.bacnet4j.wrapper.api.BacNetObject;
import org.code_house.bacnet4j.wrapper.api.Device;

public interface Visitor {

    enum Flag {
        SKIP,
        CONTINUE
    }

    Flag visit(Device device);

    Flag visit(BacNetObject object);

    Flag visit(Encodable propertyValue);

    Flag visitProperty(String property, Encodable propertyValue);

}
