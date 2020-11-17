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

package com.walmartlabs.x12.util.split;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.rule.X12Rule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class X12TransactionSplitterTest {
    
    @Rule 
    public ExpectedException exception = ExpectedException.none();
    
    private X12TransactionSplitter splitter;
    
    @Before
    public void init() {
        splitter = new X12TransactionSplitter();
    }

    @Test
    public void test_split_sourceData_null() {
        String sourceData = null;
        List<String> ediDocuments = splitter.split(sourceData);
        assertNotNull(ediDocuments);
        assertEquals(0, ediDocuments.size());
    }
    
    @Test
    public void test_split_sourceData_empty() {
        String sourceData = "";
        List<String> ediDocuments = splitter.split(sourceData);
        assertNotNull(ediDocuments);
        assertEquals(0, ediDocuments.size());
    }
    
    @Test
    public void test_split_segements_null() {
        List<X12Segment> segmentList = null;
        List<String> ediDocuments = splitter.split(segmentList);
        assertNotNull(ediDocuments);
        assertEquals(0, ediDocuments.size());
    }
    
    @Test
    public void test_split_segments_empty() {
        List<X12Segment> segmentList = new ArrayList<>();
        List<String> ediDocuments = splitter.split(segmentList);
        assertNotNull(ediDocuments);
        assertEquals(0, ediDocuments.size());
    }
    
    @Test
    public void test_split_sourceData_missing_ISA() throws IOException {
        String sourceData = new StringBuilder()
            // Note - the lack of an ISA header means
            // that the data element delimiter is not able 
            // to be determined causing odd behavior
            .append("GS*SH*4405197800*999999999*20111206*1045*00*X*004060")
            .append("\r\n")
            .append("ST*AAA*0001")
            .append("\r\n")
            .append("TEST*1")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("GE*1*00")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();
        
        exception.expect(X12ParserException.class);
        exception.expectMessage("expected ISA segment but got G");
        
        splitter.split(sourceData);
    }
    
    @Test
    public void test_split_sourceData_missing_IEA() throws IOException {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*00*X*004060")
            .append("\r\n")
            .append("ST*AAA*0001")
            .append("\r\n")
            .append("TEST*1")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("GE*1*00")
            // missing IEA
            .toString();
        
        exception.expect(X12ParserException.class);
        exception.expectMessage("expected IEA segment but got ISA");
        
        splitter.split(sourceData);
    }
    
    @Test
    public void test_split_sourceData_missing_GS() throws IOException {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ")
            .append("\r\n")
            // missing GS
            .append("ST*AAA*0001")
            .append("\r\n")
            .append("TEST*1")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("GE*1*00")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();
        
        exception.expect(X12ParserException.class);
        exception.expectMessage("expected GS segment but got ST");
        
        splitter.split(sourceData);
    }
    
    @Test
    public void test_split_sourceData_missing_GE() throws IOException {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*00*X*004060")
            .append("\r\n")
            .append("ST*AAA*0001")
            .append("\r\n")
            .append("TEST*1")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            //missing GE
            .append("IEA*2*000000049")
            .toString();
        
        exception.expect(X12ParserException.class);
        exception.expectMessage("expected ST segment but got IEA");
        
        splitter.split(sourceData);
    }
    
    @Test
    public void test_split_sourceData_missing_ST() throws IOException {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*00*X*004060")
            .append("\r\n")
            // missing ST
            .append("TEST*1")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("GE*1*00")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();
        
        exception.expect(X12ParserException.class);
        exception.expectMessage("expected ST segment but got TEST");
        
        splitter.split(sourceData);
    }
    
    @Test
    public void test_split_sourceData_missing_SE() throws IOException {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*00*X*004060")
            .append("\r\n")
            .append("ST*AAA*0001")
            .append("\r\n")
            .append("TEST*1")
            .append("\r\n")
            // missing SE
            .append("GE*1*00")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();

        exception.expect(X12ParserException.class);
        exception.expectMessage("expected SE segment but got IEA");

        splitter.split(sourceData);
    }    
    
    @Test
    public void test_split_sourceData_two_documents_missing_SE_blend() throws IOException {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*00*X*004060")
            .append("\r\n")
            .append("ST*AAA*0001")
            .append("\r\n")
            .append("TEST*1")
            .append("\r\n")
            // missing SE
            .append("ST*AAA*0002")
            .append("\r\n")
            .append("TEST*2")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("GE*1*00")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();
        
        List<String> ediDocuments = splitter.split(sourceData);
        assertNotNull(ediDocuments);
        assertEquals(1, ediDocuments.size());
        
        // verify first document
        String docOne = ediDocuments.get(0);
        assertEquals(sourceData, docOne);
    }
    
    @Test
    public void test_split_sourceData_one_group_one_document() throws IOException {
        byte[] x12Bytes = Files.readAllBytes(Paths.get("src/test/resources/x12.base.one.txt"));
        String sourceData = new String(x12Bytes);
        List<String> ediDocuments = splitter.split(sourceData);
        assertNotNull(ediDocuments);
        assertEquals(1, ediDocuments.size());
        
        // verify first document
        String expectedDocOne = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*00*X*004060")
            .append("\r\n")
            .append("ST*AAA*0001")
            .append("\r\n")
            .append("TEST*1")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("GE*1*00")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();
        String docOne = ediDocuments.get(0);
        assertEquals(expectedDocOne, docOne);
    }
    
    @Test
    public void test_split_sourceData_two_groups_four_documents() throws IOException {
        byte[] x12Bytes = Files.readAllBytes(Paths.get("src/test/resources/x12.base.txt"));
        String sourceData = new String(x12Bytes);
        List<String> ediDocuments = splitter.split(sourceData);
        assertNotNull(ediDocuments);
        assertEquals(4, ediDocuments.size());
        
        // verify first document
        String expectedDocOne = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*00*X*004060")
            .append("\r\n")
            .append("ST*AAA*0001")
            .append("\r\n")
            .append("TEST*1")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("GE*1*00")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();
        String docOne = ediDocuments.get(0);
        assertEquals(expectedDocOne, docOne);
        
        // verify 2nd document
        String expectedDocTwo = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*00*X*004060")
            .append("\r\n")
            .append("ST*BBB*0002")
            .append("\r\n")
            .append("TEST*2")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("GE*1*00")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();
        String docTwo = ediDocuments.get(1);
        assertEquals(expectedDocTwo, docTwo);
        
        // verify 3rd document
        String expectedDocThree = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*00*X*004060")
            .append("\r\n")
            .append("ST*YYZ*0099")
            .append("\r\n")
            .append("TEST*99")
            .append("\r\n")
            .append("SE*1*0099")
            .append("\r\n")
            .append("GE*1*00")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();
        String docThree = ediDocuments.get(2);
        assertEquals(expectedDocThree, docThree);
        
        // verify 4th document
        String expectedDocFour = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            .append("ST*AAA*0003")
            .append("\r\n")
            .append("TEST*3")
            .append("\r\n")
            .append("SE*1*0003")
            .append("\r\n")
            .append("GE*1*99")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();
        String docFour = ediDocuments.get(3);
        assertEquals(expectedDocFour, docFour);
    }
    
    @Test(expected = X12ParserException.class)
    public void test_split_sourceData_one_group_one_document_rules_fail() throws IOException {
        byte[] x12Bytes = Files.readAllBytes(Paths.get("src/test/resources/x12.base.one.txt"));
        String sourceData = new String(x12Bytes);
        
        X12Rule mockRule = Mockito.mock(X12Rule.class);
        Mockito.doThrow(X12ParserException.class)
            .when(mockRule)
            .verify(Mockito.anyList());
        
        splitter.registerX12Rule(mockRule);
        
        splitter.split(sourceData);
    }
    
    @Test
    public void test_split_sourceData_one_group_two_documents_rules_multi_ok() throws IOException {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*00*X*004060")
            .append("\r\n")
            .append("ST*AAA*0001")
            .append("\r\n")
            .append("TEST*1")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("ST*BBB*0002")
            .append("\r\n")
            .append("TEST*2")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("GE*1*00")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();
        
        X12Rule mockRuleOne = Mockito.mock(X12Rule.class);
        X12Rule mockRuleTwo = Mockito.mock(X12Rule.class);
        X12Rule mockRuleThree = Mockito.mock(X12Rule.class);
        
        splitter.registerX12Rule(mockRuleOne);
        splitter.registerX12Rule(mockRuleTwo);
        splitter.registerX12Rule(mockRuleThree);
        
        List<String> ediDocuments = splitter.split(sourceData);
        assertNotNull(ediDocuments);
        assertEquals(2, ediDocuments.size());
        
        // verify first document
        String expectedDocOne = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*00*X*004060")
            .append("\r\n")
            .append("ST*AAA*0001")
            .append("\r\n")
            .append("TEST*1")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("GE*1*00")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();
        String docOne = ediDocuments.get(0);
        assertEquals(expectedDocOne, docOne);
        
        // verify 2nd document
        String expectedDocTwo = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*00*X*004060")
            .append("\r\n")
            .append("ST*BBB*0002")
            .append("\r\n")
            .append("TEST*2")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("GE*1*00")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();
        String docTwo = ediDocuments.get(1);
        assertEquals(expectedDocTwo, docTwo);
        

        Mockito.verify(mockRuleOne, Mockito.times(1)).verify(Mockito.anyList());
        Mockito.verify(mockRuleTwo, Mockito.times(1)).verify(Mockito.anyList());
        Mockito.verify(mockRuleThree, Mockito.times(1)).verify(Mockito.anyList());
    }
    
    @Test
    public void test_split_sourceData_one_group_two_documents_rules_multi_fail() throws IOException {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*00*X*004060")
            .append("\r\n")
            .append("ST*AAA*0001")
            .append("\r\n")
            .append("TEST*1")
            .append("\r\n")
            .append("SE*1*0001")
            .append("ST*AAA*0002")
            .append("\r\n")
            .append("TEST*2")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("GE*1*00")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();
        
        X12Rule mockRuleOne = Mockito.mock(X12Rule.class);
        X12Rule mockRuleTwo = Mockito.mock(X12Rule.class);
        X12Rule mockRuleThree = Mockito.mock(X12Rule.class);
        X12Rule mockRuleBoom = Mockito.mock(X12Rule.class);
        Mockito.doThrow(X12ParserException.class)
            .when(mockRuleBoom)
            .verify(Mockito.anyList());
        
        splitter.registerX12Rule(mockRuleOne);
        splitter.registerX12Rule(mockRuleTwo);
        splitter.registerX12Rule(mockRuleBoom);
        splitter.registerX12Rule(mockRuleThree);
        
        try {
            splitter.split(sourceData);
            fail("expected X12ParserException");
        } catch (X12ParserException e) {
            // expected
        }
        
        Mockito.verify(mockRuleOne, Mockito.times(1)).verify(Mockito.anyList());
        Mockito.verify(mockRuleTwo, Mockito.times(1)).verify(Mockito.anyList());
        Mockito.verify(mockRuleBoom, Mockito.times(1)).verify(Mockito.anyList());
        Mockito.verify(mockRuleThree, Mockito.times(0)).verify(Mockito.anyList());
    }
}
