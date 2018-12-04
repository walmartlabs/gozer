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
package com.walmartlabs.x12;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class X12ParserDefaultMethodTest {

    X12Parser defaultParser;

    @Before
    public void init() {
        defaultParser = new MockX12Parser();
    }

    @Test
    public void test_token_parser_ThreeCharacter() {
        assertEquals("DXS", defaultParser.extractSegmentIdentifier("DXS*9251230013*DX*004010UCS*1*9254850000"));
    }

    @Test
    public void test_token_parser_TwoCharacter() {
        assertEquals("ST", defaultParser.extractSegmentIdentifier("ST*9251230013*DX*004010UCS*1*9254850000"));
    }

    @Test
    public void test_token_parser_None() {
        assertEquals("", defaultParser.extractSegmentIdentifier("*ST*9251230013*DX*004010UCS*1*9254850000"));
    }

    @Test
    public void test_token_parser_NoAsterisk() {
        assertEquals("", defaultParser.extractSegmentIdentifier("TESTING 123"));
    }

    @Test
    public void test_token_parser_Empty() {
        assertEquals("", defaultParser.extractSegmentIdentifier(""));
    }

    @Test
    public void test_token_parser_Null() {
        assertEquals("", defaultParser.extractSegmentIdentifier(null));
    }

    @Test
    public void test_splitSourceDataIntoSegments() throws IOException {
        byte[] dexBytes = Files.readAllBytes(Paths.get("src/test/resources/dex/894/dex.sample.1.txt"));
        List<String> segmentsList = defaultParser.splitSourceDataIntoSegments(new String(dexBytes));
        assertNotNull(segmentsList);
        assertEquals(22, segmentsList.size());
        assertEquals("DXS*9251230013*DX*004010UCS*1*9254850000", segmentsList.get(0));
        assertEquals("DXE*1*2", segmentsList.get(21));
    }

    @Test
    public void test_splitSourceDataIntoSegments_Empty() {
        List<String> segmentsList = defaultParser.splitSourceDataIntoSegments("");
        assertNotNull(segmentsList);
        assertEquals(0, segmentsList.size());
    }

    @Test
    public void test_splitSourceDataIntoSegments_Null() {
        List<String> segmentsList = defaultParser.splitSourceDataIntoSegments(null);
        assertNotNull(segmentsList);
        assertEquals(0, segmentsList.size());
    }

    @Test
    public void test_splitSegmentIntoDataElements() {
        List<String> elements = defaultParser.splitSegmentIntoDataElements("DXS*9251230013*DX*004010UCS*1*9254850000");
        assertNotNull(elements);
        assertEquals(6, elements.size());
        assertEquals("DXS", elements.get(0));
        assertEquals("9251230013", elements.get(1));
        assertEquals("DX", elements.get(2));
        assertEquals("004010UCS", elements.get(3));
        assertEquals("1", elements.get(4));
        assertEquals("9254850000", elements.get(5));
    }

    @Test
    public void test_splitSegmentIntoDataElements_NoAsterick() {
        List<String> elements = defaultParser.splitSegmentIntoDataElements("TEST ME");
        assertNotNull(elements);
        assertEquals(1, elements.size());
        assertEquals("TEST ME", elements.get(0));
    }

    @Test
    public void test_splitSegmentIntoDataElements_Empty() {
        List<String> elements = defaultParser.splitSegmentIntoDataElements("");
        assertNotNull(elements);
        assertEquals(0, elements.size());
    }

    @Test
    public void test_splitSegmentIntoDataElements_Null() {
        List<String> elements = defaultParser.splitSegmentIntoDataElements(null);
        assertNotNull(elements);
        assertEquals(0, elements.size());
    }

    private class MockX12Parser implements X12Parser<X12Document> {

        @Override
        public X12Document parse(String sourceData) {
            return null;
        }

    }

}
