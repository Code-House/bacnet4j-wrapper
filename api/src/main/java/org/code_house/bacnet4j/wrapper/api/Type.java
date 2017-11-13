package org.code_house.bacnet4j.wrapper.api;

/**
 * Information about bacnet types.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public interface Type<T extends BacNetElement> {

    /**
     * Bacnet code for given type.
     *
     * @return Code of bacnet type.
     */
    int getCode();

    /**
     * Bacnet type name.
     *
     * @return Type name.
     */
    String getName();

    /**
     * Factory method for creating properties.
     *
     * @param element
     * @param instanceNumber
     * @param name
     * @param description
     * @return
     */
    Property create(T element, int instanceNumber, String name, String description);
}
