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
package com.walmartlabs.x12.common;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.exceptions.X12ParserException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class AbstractStandardX12ParserTest {

    AbstractStandardX12Parser standardParser;

    @Before
    public void init() {
        standardParser = new MockStandardParser();
    }

    @Test
    public void test_parseInterchangeControlHeader() {
        MockStandardDocument x12Doc = new MockStandardDocument();
        X12Segment segment = new X12Segment("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*XX*123456789012345*101127*1719*U*00400*000003438*0*P*");
        standardParser.parseInterchangeControlHeader(segment, x12Doc);

        InterchangeControlHeader isa = x12Doc.getInterchangeControlHeader();
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
        MockStandardDocument x12Doc = new MockStandardDocument();
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
        MockStandardDocument x12Doc = new MockStandardDocument();
        X12Segment segment = new X12Segment("GS*SH*4090032Z*925485US00*20181212*0901*113733*X*005010");
        standardParser.parseGroupHeader(segment, x12Doc);

        assertNotNull(x12Doc.getGroups());
        assertEquals(1, x12Doc.getGroups().size());

        X12Group groupHeader = x12Doc.getGroups().get(0);
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
        MockStandardDocument x12Doc = new MockStandardDocument();
        X12Segment segment = new X12Segment("ISA*SH*4090032Z*925485US00*20181212*0901*113733*X*005010");
        try {
            standardParser.parseGroupHeader(segment, x12Doc);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            assertEquals("expected GS segment but found ISA", e.getMessage());
        }
    }

    public class MockStandardParser extends AbstractStandardX12Parser {

        @Override
        protected AbstractStandardX12Document createX12Document() {
            return new MockStandardDocument();
        }

        @Override
        protected void parseCustom(List segmentLines, AbstractStandardX12Document x12Doc) {
        }

    }

    public class MockStandardDocument extends AbstractStandardX12Document {

    }

}
