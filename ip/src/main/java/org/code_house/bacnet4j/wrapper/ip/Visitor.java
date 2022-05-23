package org.code_house.bacnet4j.wrapper.ip;

import com.serotonin.bacnet4j.type.Encodable;
import org.code_house.bacnet4j.wrapper.api.Device;
import org.code_house.bacnet4j.wrapper.api.Property;

public interface Visitor {

    enum Flag {
        SKIP,
        CONTNUE
    }

    Flag visit(Device device);

    Flag visit(Property property);

    Flag visit(Encodable propertyValue);

    Flag visitAttribute(String attribute, Encodable propertyValue);

}
