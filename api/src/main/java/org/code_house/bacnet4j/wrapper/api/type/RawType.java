package org.code_house.bacnet4j.wrapper.api.type;

import org.code_house.bacnet4j.wrapper.api.BacNetElement;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.code_house.bacnet4j.wrapper.api.Type;

/**
 * @author dl02
 */
public class RawType<T extends BacNetElement> extends AbstractType<T> implements Type<T> {

    protected RawType(int code, String name) {
        super(code, name);
    }

    @Override
    public Property create(BacNetElement element, int instanceNumber, String name, String description) {
        return null;
    }

}
