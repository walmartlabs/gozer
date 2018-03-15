package com.walmartlabs.x12.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class RetailSellingUnitUtilTest {

    RetailSellingUnitUtil util;

    @Before
    public void init() {
        util = new RetailSellingUnitUtil();
    }

    @Test
    public void test_retailNumber_largerThanFourteenDigits() {
        String retailNumber = "123456789012345";
        String itf14 = util.convertRetailNumberToItf14(retailNumber);
        assertNull(itf14);
    }

    @Test
    public void test_gtin14() {
        String retailNumber = "00014100078609";
        String itf14 = util.convertRetailNumberToItf14(retailNumber);
        assertNotNull(itf14);
        assertEquals("00014100078609", itf14);
    }

    @Test
    public void test_upc() {
        String retailNumber = "036121163003";
        String itf14 = util.convertRetailNumberToItf14(retailNumber);
        assertNotNull(itf14);
        assertEquals("00361211630036", itf14);
    }

    @Test
    public void test_gtin8() {
        String retailNumber = "01801624";
        String itf14 = util.convertRetailNumberToItf14(retailNumber);
        assertNotNull(itf14);
        assertEquals("00000018016246", itf14);
    }

    @Test
    public void test_padRetailNumber_null() {
        String number = null;
        String paddedNumber = util.padRetailNumber(number);
        assertNull(paddedNumber);
    }

    @Test
    public void test_padRetailNumber_empty() {
        String number = "";
        String paddedNumber = util.padRetailNumber(number);
        assertNull(paddedNumber);
    }

    @Test
    public void test_padRetailNumber() {
        String number = "7992739871";
        String paddedNumber = util.padRetailNumber(number);
        assertNotNull(paddedNumber);
        assertEquals("0007992739871", paddedNumber);
    }

    @Test
    public void test_padRetailNumberTooBig() {
        String number = "123456789012345";
        String paddedNumber = util.padRetailNumber(number);
        assertNotNull(paddedNumber);
        assertEquals(number, paddedNumber);
    }

    @Test
    public void test_verifyChecksumDigit_null() {
        String number = null;
        assertFalse(util.verifyChecksumDigit(number));
    }

    @Test
    public void test_verifyChecksumDigit_empty() {
        String number = "";
        assertFalse(util.verifyChecksumDigit(number));
    }

    @Test
    public void test_verifyChecksumDigit_oneDigit() {
        String number = "1";
        assertFalse(util.verifyChecksumDigit(number));
    }

    @Test
    public void test_verifyChecksumDigit_incorrect() {
        String number = "79927398714";
        assertFalse(util.verifyChecksumDigit(number));
    }

    @Test
    public void test_verifyChecksumDigit() {
        String number = "79927398712";
        assertTrue(util.verifyChecksumDigit(number));
    }

    @Test
    public void test_retrieveChecksumDigit_null() {
        String retailNumber = null;
        String cd = util.retrieveChecksumDigit(retailNumber);
        assertNull(cd);
    }

    @Test
    public void test_retrieveChecksumDigit_empty() {
        String retailNumber = "";
        String cd = util.retrieveChecksumDigit(retailNumber);
        assertNull(cd);
    }

    @Test
    public void test_retrieveChecksumDigit_oneDigit() {
        String retailNumber = "1";
        String cd = util.retrieveChecksumDigit(retailNumber);
        assertNotNull(cd);
        assertEquals("1", cd);
    }

    @Test
    public void test_retrieveChecksumDigit() {
        String retailNumber = "79927398713";
        String cd = util.retrieveChecksumDigit(retailNumber);
        assertNotNull(cd);
        assertEquals("3", cd);
    }
}
