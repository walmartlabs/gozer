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
import org.junit.Test;
import sample.aaa.AaaTransactionSetParser;
import sample.aaa.TypeAaaTransactionSet;
import sample.bbb.BbbTransactionSetParser;
import sample.bbb.TypeBbbTransactionSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StandardX12ParserTest {

    StandardX12Parser<StandardX12Document> standardParser;

    @Before
    public void init() {
        standardParser = new StandardX12Parser<>();
        standardParser.registerTransactionSetParser(new AaaTransactionSetParser());
        standardParser.registerTransactionSetParser(new BbbTransactionSetParser());
    }

    @Test
    public void test_parseInterchangeControlHeader() {
        StandardX12Document x12Doc = new StandardX12Document();
        X12Segment segment = new X12Segment("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*XX*123456789012345*101127*1719*U*00400*000003438*0*P*");
        standardParser.parseInterchangeControlHeader(segment, x12Doc);

        InterchangeControlEnvelope isa = x12Doc.getInterchangeControlEnvelope();
        assertNotNull(isa);
        assertEquals("01", isa.getAuthorizationInformationQualifier());
        assertEquals("0000000000", isa.getAuthorizationInformation());
        assertEquals("01", isa.getSecurityInformationQualifier());
        assertEquals("0000000000", isa.getSecurityInformation());
        assertEquals("ZZ", isa.getInterchangeIdQualifier());
        assertEquals("ABCDEFGHIJKLMNO", isa.getInterchangeSenderId());
        assertEquals("XX", isa.getInterchangeIdQualifier_2());
        assertEquals("123456789012345", isa.getInterchangeReceiverId());
        assertEquals("101127", isa.getInterchangeDate());
        assertEquals("1719", isa.getInterchangeTime());
        assertEquals("U", isa.getInterchangeControlStandardId());
        assertEquals("00400", isa.getInterchangeControlVersion());
        assertEquals("000003438", isa.getInterchangeControlNumber());
        assertEquals("0", isa.getAcknowledgementRequested());
        assertEquals("P", isa.getUsageIndicator());
        assertEquals(null, isa.getElementSeparator());
    }

    @Test
    public void test_parseInterchangeControlHeader_incorrectSegment() {
        StandardX12Document x12Doc = new StandardX12Document();
        X12Segment segment = new X12Segment("GS*SH*4090032Z*925485US00*20181212*0901*113733*X*005010");
        try {
            standardParser.parseInterchangeControlHeader(segment, x12Doc);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            assertEquals("expected ISA segment but found GS", e.getMessage());
        }
    }

    @Test
    public void test_parseGroupHeader() {
        StandardX12Document x12Doc = new StandardX12Document();
        X12Segment segment = new X12Segment("GS*SH*4090032Z*925485US00*20181212*0901*113733*X*005010");
        X12Group groupHeader = standardParser.parseGroupHeader(segment, x12Doc);

        assertNotNull(groupHeader);
        assertEquals("SH", groupHeader.getFunctionalCodeId());
        assertEquals("4090032Z", groupHeader.getApplicationSenderCode());
        assertEquals("925485US00", groupHeader.getApplicationReceiverCode());
        assertEquals("20181212", groupHeader.getDate());
        assertEquals("0901", groupHeader.getTime());
        assertEquals("113733", groupHeader.getHeaderGroupControlNumber());
        assertEquals("X", groupHeader.getResponsibleAgencyCode());
        assertEquals("005010", groupHeader.getVersion());
    }

    @Test
    public void test_parseGroupHeader_incorrectSegment() {
        StandardX12Document x12Doc = new StandardX12Document();
        X12Segment segment = new X12Segment("ISA*SH*4090032Z*925485US00*20181212*0901*113733*X*005010");
        try {
            standardParser.parseGroupHeader(segment, x12Doc);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            assertEquals("expected GS segment but found ISA", e.getMessage());
        }
    }

    @Test
    public void test_Parsing_SourceIsNull() throws IOException {
        String sourceData = null;
        StandardX12Document x12 = standardParser.parse(sourceData);
        assertNull(x12);
    }

    @Test
    public void test_Parsing_SourceIsEmpty() throws IOException {
        String sourceData = "";
        StandardX12Document x12 = standardParser.parse(sourceData);
        assertNull(x12);
    }


    @Test
    public void test_Parsing_BaseDocument() throws IOException {
        byte[] x12Bytes = Files.readAllBytes(Paths.get("src/test/resources/x12.base.txt"));
        StandardX12Document x12 = standardParser.parse(new String(x12Bytes));
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
        X12Group group1 = x12.getGroups().get(0);
        assertEquals("00", group1.getHeaderGroupControlNumber());
        List<X12TransactionSet> group1TxList = group1.getTransactions();
        assertNotNull(group1TxList);
        assertEquals(2, group1TxList.size());
        X12TransactionSet tx1 = group1TxList.get(0);
        assertTrue(tx1 instanceof TypeAaaTransactionSet);
        assertEquals("1", ((TypeAaaTransactionSet)tx1).getValue());
        X12TransactionSet tx2 = group1TxList.get(1);
        assertTrue(tx2 instanceof TypeBbbTransactionSet);
        assertEquals("2", ((TypeBbbTransactionSet)tx2).getValue());
        
        // group 2
        X12Group group2 = x12.getGroups().get(1);
        assertEquals("99", group2.getHeaderGroupControlNumber());
        List<X12TransactionSet> group2TxList = group2.getTransactions();
        assertNotNull(group2TxList);
        assertEquals(1, group2TxList.size());
        X12TransactionSet tx3 = group2TxList.get(0);
        assertTrue(tx3 instanceof TypeAaaTransactionSet);
        assertEquals("3", ((TypeAaaTransactionSet)tx3).getValue());
    }
}
