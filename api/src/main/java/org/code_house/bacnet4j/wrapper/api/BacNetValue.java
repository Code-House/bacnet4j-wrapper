package org.code_house.bacnet4j.wrapper.api;

public interface BacNetValue<T> {

    T get();
    void set(T value);

}
