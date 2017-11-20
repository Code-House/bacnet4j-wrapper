package org.code_house.bacnet4j.wrapper.api;

public interface BacNetType<P extends BacNetElement, T> {

    /**
     * Bacnet code for given type.
     *
     * @return Code of bacnet type.
     */
    int getCode();

    /**
     * Bacnet type name.
     *
     * @return PropertyType name.
     */
    String getName();

    /**
     * Factory method for creating properties.
     *
     * @param parent
     * @param instanceNumber
     * @return
     */
    T create(P parent, int instanceNumber);

    T create(P parent, int instanceNumber, Context context);

}
