package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.N1PartyIdentification;
import com.walmartlabs.x12.common.segment.TD1CarrierDetails;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.types.UnitMeasure;
import org.junit.Test;

import static org.junit.Assert.*;

public class N1PartyIdentificationParserTest {


    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        N1PartyIdentification n1 = N1PartyIdentificationParser.parse(segment);
        assertNull(n1);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        N1PartyIdentification n1 = N1PartyIdentificationParser.parse(segment);
        assertNull(n1);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("N1*ST*REGIONAL DISTRIBUTION CENTER 6285*UL*0078742090955");
        N1PartyIdentification n1 = N1PartyIdentificationParser.parse(segment);
        assertNotNull(n1);
        assertEquals("ST", n1.getEntityIdentifierCode());
        assertEquals("REGIONAL DISTRIBUTION CENTER 6285", n1.getName());
        assertEquals("UL", n1.getIdentificationCodeQualifier());
        assertEquals("0078742090955", n1.getIdentificationCode().toString());
    }

}
