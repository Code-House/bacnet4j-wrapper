package org.code_house.bacnet4j.wrapper.api;

import java.util.Optional;

/**
 * Additional information which can be passed to type during object creation.
 */
public interface Context {

    Optional get(PropertyType type);

}
