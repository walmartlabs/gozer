package com.walmartlabs.x12.util;

@FunctionalInterface
public interface TriConsumer<A, B, C> {

    void accept(A argA, B argB, C argC);

}
