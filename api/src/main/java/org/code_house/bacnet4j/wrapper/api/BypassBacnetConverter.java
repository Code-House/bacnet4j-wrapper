package org.code_house.bacnet4j.wrapper.api;

import com.serotonin.bacnet4j.type.Encodable;

public class BypassBacnetConverter implements BacNetToJavaConverter<Encodable> {

    @Override
    public Encodable fromBacNet(Encodable datum) {
        return datum;
    }

}
