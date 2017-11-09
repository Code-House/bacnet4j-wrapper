package org.code_house.bacnet4j.wrapper.api;

/**
 * Information about bacnet property.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public interface Property {

    int getId();
    Device getDevice();
    String getName();
    Type getType();

}
