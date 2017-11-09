package org.code_house.bacnet4j.wrapper.api;

/**
 * Information about bacnet types.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public interface Type {

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
     * @param device
     * @param idInstanceNumber
     * @param name
     * @param description
     * @param units
     * @return
     */
    Property create(Device device, int idInstanceNumber, String name, String description, String units);
}
