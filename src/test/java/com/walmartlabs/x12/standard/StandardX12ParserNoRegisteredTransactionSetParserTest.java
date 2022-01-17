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

import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.txset.TransactionSetParser;
import com.walmartlabs.x12.testing.util.AssertBaseDocumentUtil;
import com.walmartlabs.x12.testing.util.X12DocumentTestData;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * tests various use cases for the StandardX12Parser
 * when there are no registered {@link TransactionSetParser}
 *
 */
public class StandardX12ParserNoRegisteredTransactionSetParserTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private StandardX12Parser standardParser;

    @Before
    public void init() {
        standardParser = new StandardX12Parser();
    }

    @Test
    public void test_parsingValidDocumentWithNoTransactionSetParser() {
        String sourceData = X12DocumentTestData.readFile(AssertBaseDocumentUtil.X12_BASE_DOCUMENT_FILE);

        StandardX12Document x12Doc = standardParser.parse(sourceData);
        assertNotNull(x12Doc);

        List<X12Group> groups = x12Doc.getGroups();
        assertNotNull(groups);
        assertEquals(2, groups.size());

        // check group 1
        List<X12TransactionSet> transactions = groups.get(0).getTransactions();
        assertNull(transactions);

        // check group 2
        transactions = groups.get(1).getTransactions();
        assertNull(transactions);
    }

    @Test
    public void test_parsingWhenEnvelopeHeaderIsInvalid() {
        String sourceData = X12DocumentTestData.readFile("src/test/resources/x12.wrong.ISA.txt");

        exception.expect(X12ParserException.class);
        exception.expectMessage("Invalid EDI X12 message: must be wrapped in ISA/ISE");

        standardParser.parse(sourceData);
    }

    @Test
    public void test_parsingWhenEnvelopeHeaderIsMissing() {
        String sourceData = X12DocumentTestData.readFile("src/test/resources/x12.missing.ISA.txt");

        exception.expect(X12ParserException.class);
        exception.expectMessage("Invalid EDI X12 message: must be wrapped in ISA/ISE");

        standardParser.parse(sourceData);
    }

    @Test
    public void test_parsingWhenEnvelopeTrailerIsMissing() {
        String sourceData = "ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>";

        exception.expect(X12ParserException.class);
        exception.expectMessage("Invalid EDI X12 message: must be wrapped in ISA/ISE");

        standardParser.parse(sourceData);
    }

    @Test
    public void test_parsingWhenGroupHeaderIsInvalid() {
        String sourceData = X12DocumentTestData.readFile("src/test/resources/x12.wrong.GS.txt");

        exception.expect(X12ParserException.class);
        exception.expectMessage("expected one segment but found another");

        standardParser.parse(sourceData);
    }

    @Test
    public void test_parsingWhenSourceIsNull() throws IOException {
        String sourceData = null;

        StandardX12Document x12 = standardParser.parse(sourceData);
        assertNull(x12);
    }

    @Test
    public void test_parsingWhenSourceIsEmpty() throws IOException {
        String sourceData = "";

        StandardX12Document x12 = standardParser.parse(sourceData);
        assertNull(x12);
    }

    @Test
    public void test_registerWithNullTransactionSetParser() throws IOException {
        TransactionSetParser txSetParser = null;

        StandardX12Parser localParser = new StandardX12Parser();
        assertFalse(localParser.registerTransactionSetParser(txSetParser));
    }

    @Test
    public void test_registerWithNullTransactionSetParserCollection() throws IOException {
        List<TransactionSetParser> parsers = null;

        StandardX12Parser localParser = new StandardX12Parser();
        assertFalse(localParser.registerTransactionSetParser(parsers));
    }

    @Test
    public void test_registerWithEmptyTransactionSetParserCollection() throws IOException {
        List<TransactionSetParser> parsers = Collections.emptyList();

        StandardX12Parser localParser = new StandardX12Parser();
        assertFalse(localParser.registerTransactionSetParser(parsers));
    }

}