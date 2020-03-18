package com.walmartlabs.x12.util;

import java.util.Objects;

@FunctionalInterface
public interface TriConsumer<A, B, C> {

    void accept(A argA, B argB, C argC);

    /**
     * 
     * Adding a TriConsumer 
     */
    default TriConsumer<A, B, C> andThen(
        TriConsumer<? super A, ? super B, ? super C> after) {
        Objects.requireNonNull(after);

        return (argA, argB, argC) -> {
            accept(argA, argB, argC);
            after.accept(argA, argB, argC);
        };
    }
}
