package org.code_house.bacnet4j.wrapper.api;

/**
 * Information about bacnet property.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public interface Property<T extends BacNetElement> extends BacNetElement<T> {

    int getId();
    String getName();
    PropertyType getType();

}
