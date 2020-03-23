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

package com.walmartlabs.x12.standard;

import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.txset.TransactionSetParser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class StandardX12ParserTest {

    private StandardX12Parser standardParser;

    @Before
    public void init() {
        standardParser = new StandardX12Parser();
    }

    @Test
    public void test_parseInterchangeControlHeader_invalidSegment() {
        try {
            String sourceData = new String(Files.readAllBytes(Paths.get("src/test/resources/x12.wrong.ISA.txt")));
            standardParser.parse(sourceData);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            e.printStackTrace();
            assertEquals("Invalid EDI X12 message: must be wrapped in ISA/ISE", e.getMessage());
        } catch (IOException e) {
            fail("expected parsing exception");
        }
    }

    @Test
    public void test_parseInterchangeControlHeader_missing() {
        try {
            String sourceData = new String(Files.readAllBytes(Paths.get("src/test/resources/x12.missing.ISA.txt")));
            standardParser.parse(sourceData);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            assertEquals("Invalid EDI X12 message: must be wrapped in ISA/ISE", e.getMessage());
        } catch (IOException e) {
            fail("expected parsing exception");
        }
    }

    @Test
    public void test_parseInterchangeControlHeader_one_line_file() {
        try {
            String sourceData = "ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>";
            standardParser.parse(sourceData);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            assertEquals("Invalid EDI X12 message: must be wrapped in ISA/ISE", e.getMessage());
        }
    }

    @Test
    public void test_parseGroupHeader_invalidSegment() {
        try {
            String sourceData = new String(Files.readAllBytes(Paths.get("src/test/resources/x12.wrong.GS.txt")));
            standardParser.parse(sourceData);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            e.printStackTrace();
            assertEquals("expected GS segment but found XX", e.getMessage());
        } catch (IOException e) {
            fail("expected parsing exception");
        }
    }

    @Test
    public void test_ParsingWhenSourceIsNull() throws IOException {
        String sourceData = null;
        StandardX12Document x12 = standardParser.parse(sourceData);
        assertNull(x12);
    }

    @Test
    public void test_ParsingWhenSourceIsEmpty() throws IOException {
        String sourceData = "";
        StandardX12Document x12 = standardParser.parse(sourceData);
        assertNull(x12);
    }

    @Test
    public void test_creationWithNullTransactionSetParser() throws IOException {
        StandardX12Parser localParser = new StandardX12Parser();
        assertFalse(localParser.registerTransactionSetParser((TransactionSetParser) null));
    }

    @Test
    public void test_creationWithNullTransactionSetParserCollection() throws IOException {
        List<TransactionSetParser> parsers = null;
        StandardX12Parser localParser = new StandardX12Parser();
        assertFalse(localParser.registerTransactionSetParser(parsers));
    }

    @Test
    public void test_creationWithEmptuyTransactionSetParserCollection() throws IOException {
        List<TransactionSetParser> parsers = Collections.emptyList();
        StandardX12Parser localParser = new StandardX12Parser();
        assertFalse(localParser.registerTransactionSetParser(parsers));
    }
}