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

import static org.junit.Assert.assertNull;

public class DefaultDex894ParserTest {

    DefaultDex894Parser dexParser;

    @Before
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

    @Test(expected = X12ParserException.class)
    public void testParsingInvalidSegments() throws IOException {
        byte[] dexBytes = Files.readAllBytes(Paths.get("src/test/resources/dex/894/dex.sample.invalid.segments.txt"));
        dexParser.parse(new String(dexBytes));
    }

}