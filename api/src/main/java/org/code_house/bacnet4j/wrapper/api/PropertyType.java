package org.code_house.bacnet4j.wrapper.api;

/**
 * Marker for types which represent various properties in bacnet standard.
 *
 * @author Łukasz Dywicki &lt;luke@code-house.org&gt;
 */
public interface PropertyType<P extends BacNetElement, T extends Property<P>> extends BacNetType<P, T> {

}
