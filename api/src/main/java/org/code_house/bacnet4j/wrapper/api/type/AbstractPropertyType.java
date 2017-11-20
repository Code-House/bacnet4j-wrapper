package org.code_house.bacnet4j.wrapper.api.type;

import org.code_house.bacnet4j.wrapper.api.BacNetElement;
import org.code_house.bacnet4j.wrapper.api.Property;
import org.code_house.bacnet4j.wrapper.api.PropertyType;

import java.util.Objects;

/**
 * @author dl02
 */
public abstract class AbstractPropertyType<P extends BacNetElement, T extends Property<P>> extends AbstractType<P, T> implements PropertyType<P, T> {

    protected AbstractPropertyType(int code, String name) {
        super(code, name);
    }

}
