package org.code_house.bacnet4j.wrapper.api;

import java.util.Map;

/**
 *
 * @author dl02
 */
public interface BacNetElement<T extends BacNetElement> {

    //Object get(Property property);

    <X> BacNetValue<X> get(Property property);

    Map<Property, BacNetValue> get(Property ... properties);

    Map<PropertyType, BacNetValue> get(PropertyType ... properties);

    T getParent();

    BacNetContext getContext();

}
