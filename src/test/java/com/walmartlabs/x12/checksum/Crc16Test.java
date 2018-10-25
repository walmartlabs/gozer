package com.walmartlabs.x12.checksum;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class Crc16Test {
    CyclicRedundancyCheck crcUtil;

    @Before
    public void init() {
        crcUtil = new Crc16();
    }

    @Test
    public void test_generateCyclicRedundancyCheck_null() {
        String blockText = null;
        String crcValue = crcUtil.generateCyclicRedundancyCheck(blockText);
        assertNull(crcValue);
    }

    @Test
    public void test_generateCyclicRedundancyCheck_empty() {
        String blockText = "";
        String crcValue = crcUtil.generateCyclicRedundancyCheck(blockText);
        assertNull(crcValue);
    }

    @Test
    public void test_generateCyclicRedundancyCheck_hello_world() {
        String blockText = "hello\r\nworld";
        String crcValue = crcUtil.generateCyclicRedundancyCheck(blockText);
        assertNotNull(crcValue);
        assertEquals("FC4F", crcValue);
    }

    @Test
    public void test_generateCyclicRedundancyCheck_123456789() {
        String blockText = "123456789";
        String crcValue = crcUtil.generateCyclicRedundancyCheck(blockText);
        assertNotNull(crcValue);
        assertEquals("BB3D", crcValue);
    }

    @Test
    public void test_verifyBlockOfText_null() {
        String crcValue = null;
        String blockText = null;
        assertFalse(crcUtil.verifyBlockOfText(crcValue, blockText));
    }

    @Test
    public void test_verifyBlockOfText_mix_null() {
        String crcValue = null;
        String blockText = "";
        assertFalse(crcUtil.verifyBlockOfText(crcValue, blockText));
    }

    @Test
    public void test_verifyBlockOfText_more_mix_null() {
        String crcValue = "";
        String blockText = null;
        assertFalse(crcUtil.verifyBlockOfText(crcValue, blockText));
    }

    @Test
    public void test_verifyBlockOfText_empty() {
        String crcValue = "";
        String blockText = "";
        assertFalse(crcUtil.verifyBlockOfText(crcValue, blockText));
    }

    @Test
    public void test_verifyBlockOfText_123456789_true() {
        String crcValue = "BB3D";
        String blockText = "123456789";
        assertTrue(crcUtil.verifyBlockOfText(crcValue, blockText));
    }

    @Test
    public void test_verifyBlockOfText_123456789_false() {
        String crcValue = "FFFF";
        String blockText = "123456789";
        assertFalse(crcUtil.verifyBlockOfText(crcValue, blockText));
    }

    @Test
    public void test_verifyBlockOfText_hello_world_true() {
        String crcValue = "FC4F";
        String blockText = "hello\r\nworld";
        assertTrue(crcUtil.verifyBlockOfText(crcValue, blockText));
    }

    @Test
    public void test_verifyBlockOfText_hello_world_false() {
        String crcValue = "FFFF";
        String blockText = "hello";
        assertFalse(crcUtil.verifyBlockOfText(crcValue, blockText));
    }

    @Ignore
    @Test
    public void test_transaction() {
        String eol = "\r\n";

        StringBuilder sb = new StringBuilder();
        sb.append("ST*894*0101").append(eol);
        sb.append("G82*D*001701001701*051957769*5*123456789*073002*20180416").append(eol);
        sb.append("LS*0100").append(eol);
        sb.append("G83*1*2*CA*007800001180***007800001180*14*2*12z12P 7Up***").append(eol);
        sb.append("LE*0100").append(eol);
        sb.append("G84*8*4164").append(eol);
        sb.append("G86*0840").append(eol);

        System.out.println(crcUtil.generateCyclicRedundancyCheck(sb.toString()));
        assertTrue(crcUtil.verifyBlockOfText("8263", sb.toString()));
    }

}
