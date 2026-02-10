/**
Copyright (c) 2018-present, Walmart, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.walmartlabs.x12.util;

import com.walmartlabs.x12.exceptions.X12ParserException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @Test
    public void test_convertStringToInteger_Alpha() {
        assertThrows(X12ParserException.class, () -> ConversionUtil.convertStringToInteger("X"));
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

    @Test
    public void test_convertStringToBigDecimal_Alpha() {
        assertThrows(X12ParserException.class, () -> ConversionUtil.convertStringToBigDecimal("X", 2));
    }
}
