package org.code_house.bacnet4j.wrapper.api;

import java.util.Map;

public class BacNetRoot implements BacNetElement<BacNetRoot> {

    public static BacNetRoot INSTANCE = new BacNetRoot();

    private BacNetRoot() {

    }

    @Override
    public <X> BacNetValue<X> get(Property property) {
        return null;
    }

    @Override
    public Map<Property, BacNetValue> get(Property... properties) {
        return null;
    }

    @Override
    public Map<PropertyType, BacNetValue> get(PropertyType... properties) {
        return null;
    }

    @Override
    public BacNetRoot getParent() {
        return null;
    }

    @Override
    public BacNetContext getContext() {
        return null;
    }

}
