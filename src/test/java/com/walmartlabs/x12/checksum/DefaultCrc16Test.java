package com.walmartlabs.x12.checksum;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DefaultCrc16Test {
    CyclicRedundancyCheck crcUtil;

    @Before
    public void init() {
        crcUtil = new DefaultCrc16();
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

    @Test
    public void test_transaction() {
        String eol = "\r\n";

        StringBuilder sb = new StringBuilder();
        sb.append("ST*894*0001").append(eol);
        sb.append("G82*D*8325649401*051957769*000001*182737015*PL1124*20170911").append(eol);
        sb.append("LS*0100").append(eol);
        sb.append("G83*1*1*CA*007800001182***007800001182*14*2*12z12P Cherry 7UP***").append(eol);
        sb.append("G83*2*1*CA*007800007016***007800007016*14*2*12z12P DtRiteCherry***").append(eol);
        sb.append("G83*3*1*CA*007800011616***007800011616*14*2*12z12p SK Grape***").append(eol);
        sb.append("G83*4*1*CA*007800001084***007800001084*14*2*12z12p AW Ten RtBr***").append(eol);
        sb.append("LE*0100").append(eol);
        sb.append("G84*4*5600").append(eol);
        sb.append("G86*1310B1BF").append(eol);

        assertTrue(crcUtil.verifyBlockOfText("5800", sb.toString()));
    }

}
