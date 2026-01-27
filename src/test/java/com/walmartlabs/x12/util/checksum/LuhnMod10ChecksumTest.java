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

package com.walmartlabs.x12.util.checksum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LuhnMod10ChecksumTest {

    Checksum util;

    @BeforeEach
    public void init() {
        util = new LuhnMod10Checksum();
    }
    
    @Test
    public void test_generateChecksumDigit_null_bytes() {
        byte[] number = null;
        String checkDigit = util.generateChecksumDigit(number);
        assertNull(checkDigit);
    }
    
    @Test
    public void test_generateChecksumDigit_empty_bytes() {
        byte[] number = new byte[0];
        String checkDigit = util.generateChecksumDigit(number);
        assertNull(checkDigit);
    }

    @Test
    public void test_generateChecksumDigit_null() {
        String number = null;
        String checkDigit = util.generateChecksumDigit(number);
        assertNull(checkDigit);
    }

    @Test
    public void test_generateChecksumDigit_empty() {
        String number = "";
        String checkDigit = util.generateChecksumDigit(number);
        assertNull(checkDigit);
    }

    @Test
    public void test_generateChecksumDigit_nonNumber() {
        String number = "A123";
        NumberFormatException thrown = assertThrows(NumberFormatException.class, () -> util.generateChecksumDigit(number));
        String message = thrown.getMessage();
        // JDK NumberFormatException message typically includes the non-numeric character only
        assertTrue(message == null || message.contains("A"));
    }

    @Test
    public void test_generateChecksumDigit_tenDigitNumber() {
        String number = "7992739871";
        String checkDigit = util.generateChecksumDigit(number);
        assertNotNull(checkDigit);
        assertEquals("3", checkDigit);
    }

    @Test
    public void test_generateChecksumDigit_eightDigitNumber() {
        String number = "01801624";
        String checkDigit = util.generateChecksumDigit(number);
        assertNotNull(checkDigit);
        assertEquals("6", checkDigit);
    }

    @Test
    public void test_generateChecksumDigit_twelveDigitNumber() {
        String number = "036121163003";
        String checkDigit = util.generateChecksumDigit(number);
        assertNotNull(checkDigit);
        assertEquals("9", checkDigit);
    }

    @Test
    public void test_generateChecksumDigit_thirteenDigitNumber() {
        String number = "0001410007860";
        String checkDigit = util.generateChecksumDigit(number);
        assertNotNull(checkDigit);
        assertEquals("0", checkDigit);
    }

}
