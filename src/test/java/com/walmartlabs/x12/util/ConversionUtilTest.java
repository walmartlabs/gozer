package com.walmartlabs.x12.util;

import com.walmartlabs.x12.exceptions.X12ParserException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConversionUtilTest {

    @Test
    public void test_convertStringToInteger_Null() {
        assertEquals(null, ConversionUtil.convertStringToInteger(null));
    }

    @Test
    public void test_convertStringToInteger_None() {
        assertEquals(null, ConversionUtil.convertStringToInteger(""));
    }

    @Test
    public void test_convertStringToInteger_Number() {
        assertEquals(new Integer(1), ConversionUtil.convertStringToInteger("1"));
    }

    @Test(expected = X12ParserException.class)
    public void test_convertStringToInteger_Alpha() {
        ConversionUtil.convertStringToInteger("X");
    }

    @Test
    public void test_convertStringToBigDecimal_Null() {
        assertEquals(null, ConversionUtil.convertStringToBigDecimal(null, 2));
    }

    @Test
    public void test_convertStringToBigDecimal_None() {
        assertEquals(null, ConversionUtil.convertStringToBigDecimal("", 2));
    }

    @Test
    public void test_convertStringToBigDecimal_Number() {
        assertEquals("1.00", ConversionUtil.convertStringToBigDecimal("1", 2).toString());
    }

    @Test
    public void test_convertStringToBigDecimal_Negative_Number() {
        assertEquals("-1.00", ConversionUtil.convertStringToBigDecimal("-1", 2).toString());
    }

    @Test(expected = X12ParserException.class)
    public void test_convertStringToBigDecimal_Alpha() {
        ConversionUtil.convertStringToBigDecimal("X", 2);
    }
}
