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
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.exceptions.X12ParserException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import sample.aaa.AaaTransactionSetParser;
import sample.aaa.TypeAaaTransactionSet;
import sample.bbb.BbbTransactionSetParser;
import sample.bbb.TypeBbbTransactionSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StandardX12ParserTest {

    private static byte[] x12Bytes;
    private StandardX12Parser standardParser;

    @BeforeClass
    public static void setup() throws IOException {
        x12Bytes = Files.readAllBytes(Paths.get("src/test/resources/x12.base.txt"));
    }
    
    @Before
    public void init() {
        standardParser = createParserWithRegistration();
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

    @Test
    public void test_Parsing_BaseDocument_register_each() throws IOException {
        String sourceData = new String(x12Bytes);
        StandardX12Document x12 = standardParser.parse(sourceData);
        standardParser.registerUnhandledTransactionSet((a,b) -> {
            assertEquals("","");
        });
        this.verifyParsingOfBaseDocument(x12);
    }
    
    @Test
    public void test_Parsing_BaseDocument_register_collection() throws IOException {
        String sourceData = new String(x12Bytes);
        StandardX12Parser localParser = this.createParserWithRegistrationViaCollection();
        StandardX12Document x12 = localParser.parse(sourceData);
        this.verifyParsingOfBaseDocument(x12);
    }
    
    @Test
    public void test_Parsing_BaseDocument_register_mixed() throws IOException {
        String sourceData = new String(x12Bytes);
        StandardX12Parser localParser = this.createParserWithRegistrationMixed();
        StandardX12Document x12 = localParser.parse(sourceData);
        this.verifyParsingOfBaseDocument(x12);
    }
    
    @Test
    public void test_Parsing_BaseDocument_empty_lines_at_end() throws IOException {
        byte[] x12MsgBytes = Files.readAllBytes(Paths.get("src/test/resources/x12.base.no.line.breaks.empty.line.txt"));
        String sourceData = new String(x12MsgBytes);
        StandardX12Parser localParser = this.createParserWithRegistrationViaCollection();
        StandardX12Document x12 = localParser.parse(sourceData);
        this.verifyParsingOfBaseDocument(x12);
    }
    
    @Test
    public void test_Parsing_BaseDocument_no_line_breaks_different_delimiter() throws IOException {
        byte[] x12MsgBytes = Files.readAllBytes(Paths.get("src/test/resources/x12.base.no.line.breaks.odd.char.txt"));
        String sourceData = new String(x12MsgBytes);
        StandardX12Parser localParser = this.createParserWithRegistrationViaCollection();
        StandardX12Document x12 = localParser.parse(sourceData);
        this.verifyParsingOfBaseDocument(x12);
    }
    
    @Test
    public void test_Parsing_BaseDocument_no_line_breaks_short_file() {
        try {
            String sourceData = "ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ";
            StandardX12Parser localParser = this.createParserWithRegistrationViaCollection();
            localParser.parse(sourceData);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            assertEquals("Invalid EDI X12 message: must be wrapped in ISA/ISE", e.getMessage());
        }
    }
    
    @Test
    public void test_Parsing_BaseDocument_no_line_breaks() throws IOException {
        byte[] x12MsgBytes = Files.readAllBytes(Paths.get("src/test/resources/x12.base.no.line.breaks.txt"));
        String sourceData = new String(x12MsgBytes);
        StandardX12Parser localParser = this.createParserWithRegistrationViaCollection();
        StandardX12Document x12 = localParser.parse(sourceData);
        this.verifyParsingOfBaseDocument(x12);
    }
    
    @Test
    public void test_Parsing_BaseDocument_no_line_breaks_no_delim() throws IOException {
        try {
            byte[] x12MsgBytes = Files.readAllBytes(Paths.get("src/test/resources/x12.no.line.break.no.delim.txt"));
            String sourceData = new String(x12MsgBytes);
            StandardX12Parser localParser = this.createParserWithRegistrationViaCollection();
            localParser.parse(sourceData);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            assertEquals("Invalid EDI X12 message: must be wrapped in ISA/ISE", e.getMessage());
        } catch (IOException e) {
            fail("expected parsing exception");
        }
    }
    
    private void verifyParsingOfBaseDocument(StandardX12Document x12) {
        assertNotNull(x12);
        
        // ISA segment
        InterchangeControlEnvelope isa = x12.getInterchangeControlEnvelope();
        assertNotNull(isa);
        assertEquals("01", isa.getAuthorizationInformationQualifier());
        assertEquals("0000000000", isa.getAuthorizationInformation());
        assertEquals("01", isa.getSecurityInformationQualifier());
        assertEquals("0000000000", isa.getSecurityInformation());
        assertEquals("ZZ", isa.getInterchangeIdQualifier());
        assertEquals("ABCDEFGHIJKLMNO", isa.getInterchangeSenderId());
        assertEquals("ZZ", isa.getInterchangeIdQualifier_2());
        assertEquals("123456789012345", isa.getInterchangeReceiverId());
        assertEquals("101127", isa.getInterchangeDate());
        assertEquals("1719", isa.getInterchangeTime());
        assertEquals("U", isa.getInterchangeControlStandardId());
        assertEquals("00400", isa.getInterchangeControlVersion());
        assertEquals("000000049", isa.getInterchangeControlNumber());
        assertEquals("0", isa.getAcknowledgementRequested());
        assertEquals("P", isa.getUsageIndicator());
        assertEquals(">", isa.getElementSeparator());
        
        // ise trailer
        InterchangeControlEnvelope ise = x12.getInterchangeControlEnvelope();
        assertNotNull(ise);
        assertEquals("000000049", ise.getInterchangeControlNumber());
        assertEquals(new Integer(2), ise.getNumberOfGroups());

        // groups
        assertNotNull(x12.getGroups());
        assertEquals(2, x12.getGroups().size());
        
        // group 1
        // GS*SH*4405197800*999999999*20111206*1045*00*X*004060
        X12Group group1 = x12.getGroups().get(0);
        assertEquals("SH", group1.getFunctionalCodeId());
        assertEquals("4405197800", group1.getApplicationSenderCode());
        assertEquals("999999999", group1.getApplicationReceiverCode());
        assertEquals("20111206", group1.getDate());
        assertEquals("1045", group1.getTime());
        assertEquals("00", group1.getHeaderGroupControlNumber());
        assertEquals("X", group1.getResponsibleAgencyCode());
        assertEquals("004060", group1.getVersion());
        
        List<X12TransactionSet> group1TxList = group1.getTransactions();
        assertNotNull(group1TxList);
        assertEquals(2, group1TxList.size());
        X12TransactionSet tx1 = group1TxList.get(0);
        assertTrue(tx1 instanceof TypeAaaTransactionSet);
        assertEquals("AAA", tx1.getTransactionSetIdentifierCode());
        assertEquals("1", ((TypeAaaTransactionSet)tx1).getAaaOnlyValue());
        X12TransactionSet tx2 = group1TxList.get(1);
        assertTrue(tx2 instanceof TypeBbbTransactionSet);
        assertEquals("BBB", tx2.getTransactionSetIdentifierCode());
        assertEquals("2", ((TypeBbbTransactionSet)tx2).getValue());
        
        // group 2
        // GS*SH*4405197800*999999999*20111206*1045*99*X*004060
        X12Group group2 = x12.getGroups().get(1);
        assertEquals("SH", group2.getFunctionalCodeId());
        assertEquals("4405197800", group2.getApplicationSenderCode());
        assertEquals("999999999", group2.getApplicationReceiverCode());
        assertEquals("20111206", group2.getDate());
        assertEquals("1045", group2.getTime());
        assertEquals("99", group2.getHeaderGroupControlNumber());
        assertEquals("X", group2.getResponsibleAgencyCode());
        assertEquals("004060", group2.getVersion());
        
        List<X12TransactionSet> group2TxList = group2.getTransactions();
        assertNotNull(group2TxList);
        assertEquals(1, group2TxList.size());
        X12TransactionSet tx3 = group2TxList.get(0);
        assertTrue(tx3 instanceof TypeAaaTransactionSet);
        assertEquals("AAA", tx3.getTransactionSetIdentifierCode());
        assertEquals("3", ((TypeAaaTransactionSet)tx3).getAaaOnlyValue());
    }
    
    private StandardX12Parser createParserWithRegistration() {
        StandardX12Parser standardParser = new StandardX12Parser();
        standardParser.registerTransactionSetParser(new AaaTransactionSetParser());
        standardParser.registerTransactionSetParser((TransactionSetParser)null);
        standardParser.registerTransactionSetParser(new BbbTransactionSetParser());
        
        standardParser.registerUnhandledTransactionSet(new UnhandledTransactionSet() {
            
            @Override
            public void unhandledTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group) {
                assertNotNull(transactionSegments);
                assertFalse(transactionSegments.isEmpty());
                String txSetId = transactionSegments.get(0).getSegmentElement(1);
                assertEquals("YYZ", txSetId);
            }
        });
        return standardParser;
    }
    
    private StandardX12Parser createParserWithRegistrationViaCollection() {
        List<TransactionSetParser> parsers = new ArrayList<>();
        parsers.add(new AaaTransactionSetParser());
        parsers.add(new BbbTransactionSetParser());
        
        StandardX12Parser standardParser = new StandardX12Parser();
        standardParser.registerTransactionSetParser(parsers);
        return standardParser;
    }
    
    private StandardX12Parser createParserWithRegistrationMixed() {
        List<TransactionSetParser> parsers = new ArrayList<>();
        parsers.add(new BbbTransactionSetParser());
        
        StandardX12Parser standardParser = new StandardX12Parser();
        standardParser.registerTransactionSetParser(new AaaTransactionSetParser());
        standardParser.registerTransactionSetParser(parsers);
        return standardParser;
    }
}
