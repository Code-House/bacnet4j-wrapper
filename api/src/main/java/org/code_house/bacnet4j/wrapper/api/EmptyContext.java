package org.code_house.bacnet4j.wrapper.api;

import java.util.Optional;

public final class EmptyContext implements Context {

    @Override
    public Optional get(PropertyType type) {
        return Optional.empty();
    }

}
