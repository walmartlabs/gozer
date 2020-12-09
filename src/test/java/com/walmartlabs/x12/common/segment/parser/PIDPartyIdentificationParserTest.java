package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.PIDProductIdentification;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PIDPartyIdentificationParserTest {


    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        PIDProductIdentification pid = PIDPartyIdentificationParser.parse(segment);
        assertNull(pid);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        PIDProductIdentification pid = PIDPartyIdentificationParser.parse(segment);
        assertNull(pid);
    }

    @Test
    public void test_parse_segment_wrong_id() {
        X12Segment segment = new X12Segment("XX*F*MSG***PRODUCT OF USA-CALIFORNIA AND CHILE");
        PIDProductIdentification pid = PIDPartyIdentificationParser.parse(segment);
        assertNull(pid);
    }
    
    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("PID*F*MSG***PRODUCT OF USA-CALIFORNIA AND CHILE");
        PIDProductIdentification pid = PIDPartyIdentificationParser.parse(segment);
        assertNotNull(pid);
        assertEquals("F", pid.getItemDescriptionType());
        assertEquals("MSG", pid.getCharacteristicCode());
        assertEquals("PRODUCT OF USA-CALIFORNIA AND CHILE", pid.getDescription());
    }
}
