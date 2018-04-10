package com.walmartlabs.x12.checksum;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class LuhnMod10ChecksumTest {

    Checksum util;

    @Before
    public void init() {
        util = new LuhnMod10Checksum();
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

    @Test (expected = NumberFormatException.class)
    public void test_generateChecksumDigit_nonNumber() {
        String number = "A123";
        String checkDigit = util.generateChecksumDigit(number);
        assertNull(checkDigit);
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
