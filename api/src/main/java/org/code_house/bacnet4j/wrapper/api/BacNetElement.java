package org.code_house.bacnet4j.wrapper.api;

/**
 * @author dl02
 */
public interface BacNetElement {

    Object get(Property property);

    <T> T get(Property property, BacNetToJavaConverter<T> converter);

}
