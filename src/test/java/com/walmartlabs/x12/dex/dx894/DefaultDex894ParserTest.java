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
import com.walmartlabs.x12.testing.util.X12DocumentTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultDex894ParserTest {

    DefaultDex894Parser dexParser;

    @BeforeEach
    public void init() {
        dexParser = new DefaultDex894Parser();
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

    @Test
    public void testParsingShipment_invalid() throws IOException {
        String dexTransmission = "invalid";
        assertThrows(X12ParserException.class, () -> dexParser.parse(dexTransmission));
    }

    @Test
    public void testParsingShipmentWithMissingDxe() throws IOException {
        byte[] dexBytes = X12DocumentTestData.readFileAsBytes("src/test/resources/dex/894/dex.sample.missing.dxe.txt");
        assertThrows(X12ParserException.class, () -> dexParser.parse(new String(dexBytes)));
    }

    @Test
    public void testParsingShipmentWithMismatchedTransactions() throws IOException {
        byte[] dexBytes = X12DocumentTestData.readFileAsBytes("src/test/resources/dex/894/dex.sample.mismatched.st.txt");
        assertThrows(X12ParserException.class, () -> dexParser.parse(new String(dexBytes)));
    }

    @Test
    public void testParsingInvalidSegments() throws IOException {
        byte[] dexBytes = X12DocumentTestData.readFileAsBytes("src/test/resources/dex/894/dex.sample.invalid.segments.txt");
        assertThrows(X12ParserException.class, () -> dexParser.parse(new String(dexBytes)));
    }

}