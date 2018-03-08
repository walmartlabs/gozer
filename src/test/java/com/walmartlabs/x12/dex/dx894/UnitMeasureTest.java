package com.walmartlabs.x12.dex.dx894;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnitMeasureTest {

    @Test
    public void test_valid_value() {
        assertEquals(UnitMeasure.BX, UnitMeasure.convertUnitMeasure("BX"));
    }

    @Test
    public void test_invalid_value() {
        assertEquals(UnitMeasure.UNKNOWN, UnitMeasure.convertUnitMeasure("BOGUS"));
    }

    @Test
    public void test_invalid_empty() {
        assertEquals(UnitMeasure.UNKNOWN, UnitMeasure.convertUnitMeasure(""));
    }

    @Test
    public void test_invalid_null() {
        assertEquals(UnitMeasure.UNKNOWN, UnitMeasure.convertUnitMeasure(null));
    }

}
