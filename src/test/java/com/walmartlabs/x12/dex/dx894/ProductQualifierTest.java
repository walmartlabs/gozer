package com.walmartlabs.x12.dex.dx894;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProductQualifierTest {

    @Test
    public void test_valid_value() {
        assertEquals(ProductQualifier.EN, ProductQualifier.convertyProductQualifier("EN"));
    }

    @Test
    public void test_invalid_value() {
        assertEquals(ProductQualifier.UNKNOWN, ProductQualifier.convertyProductQualifier("BOGUS"));
    }

    @Test
    public void test_invalid_empty() {
        assertEquals(ProductQualifier.UNKNOWN, ProductQualifier.convertyProductQualifier(""));
    }

    @Test
    public void test_invalid_null() {
        assertEquals(ProductQualifier.UNKNOWN, ProductQualifier.convertyProductQualifier(null));
    }

}
