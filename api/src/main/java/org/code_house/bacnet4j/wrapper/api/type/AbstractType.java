package org.code_house.bacnet4j.wrapper.api.type;

import org.code_house.bacnet4j.wrapper.api.*;

import java.util.Objects;

/**
 * @author dl02
 */
public abstract class AbstractType<P extends BacNetElement, T> implements BacNetType<P, T> {
    protected final int code;
    protected final String name;

    protected AbstractType(int code, String name) {
        this.name = name;
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimpleType)) return false;
        SimpleType that = (SimpleType) o;
        return getCode() == that.getCode() &&
            Objects.equals(getName(), that.getName());
    }

    @Override
    public T create(P parent, int instanceNumber) {
        return create(parent, instanceNumber, new EmptyContext());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCode(), getName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getCode() + ":" + getName() + ")";
    }
}
