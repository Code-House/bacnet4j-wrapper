package org.code_house.bacnet4j.wrapper.api.type;

import org.code_house.bacnet4j.wrapper.api.BacNetElement;
import org.code_house.bacnet4j.wrapper.api.Type;

import java.util.Objects;

/**
 * @author dl02
 */
public abstract class AbstractType<T extends BacNetElement> implements Type<T> {
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
    public int hashCode() {
        return Objects.hash(getCode(), getName());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getCode() + ":" + getName() + ")";
    }
}
