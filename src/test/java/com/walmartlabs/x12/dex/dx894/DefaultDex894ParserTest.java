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
package com.walmartlabs.x12.dex.dx894;

import com.walmartlabs.x12.exceptions.X12ParserException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DefaultDex894ParserTest {

    DefaultDex894Parser dexParser;

    @Before
    public void init() {
        dexParser = new DefaultDex894Parser();
    }

    @Test
    public void test_token_parser_ThreeCharacter() {
        assertEquals("DXS", dexParser.segmentIdentifier("DXS*9251230013*DX*004010UCS*1*9254850000"));
    }

    @Test
    public void test_token_parser_TwoCharacter() {
        assertEquals("ST", dexParser.segmentIdentifier("ST*9251230013*DX*004010UCS*1*9254850000"));
    }

    @Test
    public void test_token_parser_None() {
        assertEquals("", dexParser.segmentIdentifier("*ST*9251230013*DX*004010UCS*1*9254850000"));
    }

    @Test
    public void test_convertStringToInteger_Null() {
        assertEquals(null, dexParser.convertStringToInteger(null));
    }

    @Test
    public void test_convertStringToInteger_None() {
        assertEquals(null, dexParser.convertStringToInteger(""));
    }

    @Test
    public void test_convertStringToInteger_Number() {
        assertEquals(new Integer(1), dexParser.convertStringToInteger("1"));
    }

    @Test(expected = X12ParserException.class)
    public void test_convertStringToInteger_Alpha() {
        dexParser.convertStringToInteger("X");
    }

    @Test
    public void test_convertStringToBigDecimal_Null() {
        assertEquals(null, dexParser.convertStringToBigDecimal(null, 2));
    }

    @Test
    public void test_convertStringToBigDecimal_None() {
        assertEquals(null, dexParser.convertStringToBigDecimal("", 2));
    }

    @Test
    public void test_convertStringToBigDecimal_Number() {
        assertEquals("1.00", dexParser.convertStringToBigDecimal("1", 2).toString());
    }

    @Test
    public void test_convertStringToBigDecimal_Negative_Number() {
        assertEquals("-1.00", dexParser.convertStringToBigDecimal("-1", 2).toString());
    }

    @Test(expected = X12ParserException.class)
    public void test_convertStringToBigDecimal_Alpha() {
        dexParser.convertStringToBigDecimal("X", 2);
    }

    @Test
    public void testParsingShipment_null() throws IOException {
        String dexTransmission = null;
        assertNull(dexParser.parse(dexTransmission));
    }

    @Test
    public void testParsingShipment_empty() throws IOException {
        String dexTransmission = "";
        assertNull(dexParser.parse(dexTransmission));
    }

    @Test(expected = X12ParserException.class)
    public void testParsingShipment_invalid() throws IOException {
        String dexTransmission = "invalid";
        dexParser.parse(dexTransmission);
    }

    @Test(expected = X12ParserException.class)
    public void testParsingShipmentWithMissingDxe() throws IOException {
        byte[] dexBytes = Files.readAllBytes(Paths.get("src/test/resources/dex/894/dex.sample.missing.dxe.txt"));
        dexParser.parse(new String(dexBytes));
    }

    @Test(expected = X12ParserException.class)
    public void testParsingShipmentWithMismatchedTransactions() throws IOException {
        byte[] dexBytes = Files.readAllBytes(Paths.get("src/test/resources/dex/894/dex.sample.mismatched.st.txt"));
        dexParser.parse(new String(dexBytes));
    }

}