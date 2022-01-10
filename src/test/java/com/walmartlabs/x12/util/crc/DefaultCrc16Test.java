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

package com.walmartlabs.x12.util.crc;

import com.walmartlabs.x12.dex.dx894.DefaultDex894Validator;
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
    public void test_generateCyclicRedundancyCheck_123456789_padding_negative() {
        String blockText = "123456789";
        String crcValue = crcUtil.generateCyclicRedundancyCheck(blockText, -1);
        assertNotNull(crcValue);
        assertEquals("BB3D", crcValue);
    }

    @Test
    public void test_generateCyclicRedundancyCheck_123456789_padding_zero() {
        String blockText = "123456789";
        String crcValue = crcUtil.generateCyclicRedundancyCheck(blockText, 0);
        assertNotNull(crcValue);
        assertEquals("BB3D", crcValue);
    }

    @Test
    public void test_generateCyclicRedundancyCheck_123456789_padding_smaller() {
        String blockText = "123456789";
        String crcValue = crcUtil.generateCyclicRedundancyCheck(blockText, 2);
        assertNotNull(crcValue);
        assertEquals("BB3D", crcValue);
    }

    @Test
    public void test_generateCyclicRedundancyCheck_123456789_padding_equal() {
        String blockText = "123456789";
        String crcValue = crcUtil.generateCyclicRedundancyCheck(blockText, 4);
        assertNotNull(crcValue);
        assertEquals("BB3D", crcValue);
    }

    @Test
    public void test_generateCyclicRedundancyCheck_123456789_padding_larger() {
        String blockText = "123456789";
        String crcValue = crcUtil.generateCyclicRedundancyCheck(blockText, 8);
        assertNotNull(crcValue);
        assertEquals("0000BB3D", crcValue);
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
    public void test_verifyBlockOfText_123456789_true_lower_case() {
        String crcValue = "bb3d";
        String blockText = "123456789";
        assertTrue(crcUtil.verifyBlockOfText(crcValue, blockText));
    }

    @Test
    public void test_verifyBlockOfText_123456789_noMatch() {
        String crcValue = "FFFF";
        String blockText = "123456789";
        assertFalse(crcUtil.verifyBlockOfText(crcValue, blockText));
    }

    @Test
    public void test_verifyBlockOfText_hello_world_matches() {
        String crcValue = "FC4F";
        String blockText = "hello\r\nworld";
        assertTrue(crcUtil.verifyBlockOfText(crcValue, blockText));
    }

    @Test
    public void test_verifyBlockOfText_hello_world_noMatch() {
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

    @Test
    public void test_transaction_padding() {
        String eol = "\r\n";

        StringBuilder sb = new StringBuilder();
        sb.append("ST*894*10000").append(eol);
        sb.append("G82*C*2378008*051957769*1085*008506768*000000*20181128").append(eol);
        sb.append("LS*0100").append(eol);
        sb.append("G83*1*1*EA*004750001744****2.69*1*SMOKED SAU STICKS OR").append(eol);
        sb.append("LE*0100").append(eol);
        sb.append("G84*1*269*00").append(eol);
        sb.append("G86*8B92").append(eol);

        assertTrue(crcUtil.verifyBlockOfText("5FA", sb.toString()));
        assertFalse(crcUtil.verifyBlockOfText("05FA", sb.toString()));
        assertTrue(crcUtil.verifyBlockOfText("05FA", sb.toString(), DefaultDex894Validator.DEX_CRC_VALUE_MIN_SIZE));
        assertTrue(crcUtil.verifyBlockOfText("5FA", sb.toString(), -1));
        assertTrue(crcUtil.verifyBlockOfText("5FA", sb.toString(), 0));
    }

    @Test
    public void test_from_customer() {
        String eol = "\r\n";

        StringBuilder sb = new StringBuilder();
        sb.append("ST*895*1000").append(eol);
        sb.append("G87*R*D*020012345678*E219*1").append(eol);
        sb.append("G86*0B72").append(eol);

        String value = crcUtil.generateCyclicRedundancyCheck(sb.toString());
        System.out.println(value);
        assertTrue(crcUtil.verifyBlockOfText("27B8", sb.toString()));
    }
}
