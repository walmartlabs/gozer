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

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.txset.TransactionSetParser;
import com.walmartlabs.x12.standard.txset.UnhandledTransactionSet;
import com.walmartlabs.x12.testing.util.AssertBaseDocumentUtil;
import com.walmartlabs.x12.testing.util.X12DocumentTestData;
import com.walmartlabs.x12.testing.util.txset.aaa.AaaTransactionSetParser;
import com.walmartlabs.x12.testing.util.txset.bbb.BbbTransactionSetParser;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 *
 * test standard parser w/ a simple generic X12 document (x12.base.txt)
 * and a variety of scenarios with different
 * registered transaction set parsers
 *
 */
public class StandardX12ParserUsingX12BaseDocumentTest {

    private final String sourceData = X12DocumentTestData.readFile(AssertBaseDocumentUtil.X12_BASE_DOCUMENT_FILE);
    private StandardX12Parser standardParser = new StandardX12Parser();

    @Test
    public void test_Parsing_BaseDocument_no_transaction_parsers() throws IOException {
        StandardX12Document x12 = standardParser.parse(sourceData);
        AssertBaseDocumentUtil.assertBaseDocumentNoParsers(x12);
    }

    @Test
    public void test_Parsing_BaseDocument_with_unhandled() throws IOException {
        this.registerTransactionSetParsers();

        StubUnhandledTransactionSet uts = new StubUnhandledTransactionSet();
        standardParser.registerUnhandledTransactionSet(uts);

        StandardX12Document x12 = standardParser.parse(sourceData);
        AssertBaseDocumentUtil.assertBaseDocument(x12);
        assertEquals("YYZ",  uts.unhandledTxSetId);
    }

    @Test
    public void test_Parsing_BaseDocument_register_using_setter() throws IOException {
        this.registerTransactionSetParsers();

        StandardX12Document x12 = standardParser.parse(sourceData);
        AssertBaseDocumentUtil.assertBaseDocument(x12);
    }

    @Test
    public void test_Parsing_BaseDocument_register_using_collection() throws IOException {
        this.registerUsingCollection();

        StandardX12Document x12 = standardParser.parse(sourceData);
        AssertBaseDocumentUtil.assertBaseDocument(x12);
    }

    @Test
    public void test_Parsing_BaseDocument_register_using_mixed() throws IOException {
        this.registerMixed();

        StandardX12Document x12 = standardParser.parse(sourceData);
        AssertBaseDocumentUtil.assertBaseDocument(x12);
    }

    @Test
    public void test_Parsing_BaseDocument_empty_lines_at_end() throws IOException {
        this.registerUsingCollection();

        String sourceData = X12DocumentTestData.readFile("src/test/resources/x12.base.no.line.breaks.empty.line.txt");
        StandardX12Document x12 = standardParser.parse(sourceData);
        AssertBaseDocumentUtil.assertBaseDocument(x12);
    }

    @Test
    public void test_Parsing_BaseDocument_no_line_breaks_different_delimiter() throws IOException {
        this.registerUsingCollection();

        String sourceData = X12DocumentTestData.readFile("src/test/resources/x12.base.no.line.breaks.odd.char.txt");
        StandardX12Document x12 = standardParser.parse(sourceData);
        AssertBaseDocumentUtil.assertBaseDocument(x12);
    }

    @Test
    public void test_Parsing_BaseDocument_no_line_breaks_short_file() {
        this.registerUsingCollection();

        try {
            String sourceData = "ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ";
            standardParser.parse(sourceData);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            assertEquals("Invalid EDI X12 message: must be wrapped in ISA/ISE", e.getMessage());
        }
    }

    @Test
    public void test_Parsing_BaseDocument_no_line_breaks() throws IOException {
        this.registerUsingCollection();

        String sourceData = X12DocumentTestData.readFile("src/test/resources/x12.base.no.line.breaks.txt");
        StandardX12Document x12 = standardParser.parse(sourceData);
        AssertBaseDocumentUtil.assertBaseDocument(x12);
    }

    @Test
    public void test_Parsing_BaseDocument_no_line_breaks_no_delim() throws IOException {
        this.registerUsingCollection();

        try {
            String sourceData = X12DocumentTestData.readFile("src/test/resources/x12.no.line.break.no.delim.txt");
            standardParser.parse(sourceData);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            assertEquals("Invalid EDI X12 message: must be wrapped in ISA/ISE", e.getMessage());
        }
    }

    private void registerTransactionSetParsers() {
        standardParser.registerTransactionSetParser(new AaaTransactionSetParser());
        standardParser.registerTransactionSetParser((TransactionSetParser)null);
        standardParser.registerTransactionSetParser(new BbbTransactionSetParser());
    }

    private void registerUsingCollection() {
        List<TransactionSetParser> parsers = new ArrayList<>();
        parsers.add(new AaaTransactionSetParser());
        parsers.add(new BbbTransactionSetParser());

        standardParser.registerTransactionSetParser(parsers);
    }

    private void registerMixed() {
        List<TransactionSetParser> parsers = new ArrayList<>();
        parsers.add(new BbbTransactionSetParser());

        standardParser.registerTransactionSetParser(new AaaTransactionSetParser());
        standardParser.registerTransactionSetParser(parsers);
    }

    public class StubUnhandledTransactionSet implements UnhandledTransactionSet {

        String unhandledTxSetId;

        @Override
        public void unhandledTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group) {
            assertNotNull(transactionSegments);
            assertFalse(transactionSegments.isEmpty());
            String txSetId = transactionSegments.get(0).getElement(1);
            assertEquals("YYZ", txSetId);
            this.unhandledTxSetId = txSetId;
        }
    }
}