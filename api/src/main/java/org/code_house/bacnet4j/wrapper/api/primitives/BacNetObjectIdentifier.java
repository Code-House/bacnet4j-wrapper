package org.code_house.bacnet4j.wrapper.api.primitives;

public class BacNetObjectIdentifier {

    private final int type;
    private final int instance;

    public BacNetObjectIdentifier(int type, int instance) {
        this.type = type;
        this.instance = instance;
    }

    public int getType() {
        return type;
    }

    public int getInstance() {
        return instance;
    }
}
