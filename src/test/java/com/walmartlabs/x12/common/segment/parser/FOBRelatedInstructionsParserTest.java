package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.FOBRelatedInstructions;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FOBRelatedInstructionsParserTest {


    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        FOBRelatedInstructions fob = FOBRelatedInstructionsParser.parse(segment);
        assertNull(fob);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        FOBRelatedInstructions fob = FOBRelatedInstructionsParser.parse(segment);
        assertNull(fob);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("FOB*PP");
        FOBRelatedInstructions fob = FOBRelatedInstructionsParser.parse(segment);
        assertNotNull(fob);
        assertEquals("PP", fob.getPaymentCode());
    }

    @Test
    public void test_parse_segment_bad_identifier() {
        X12Segment segment = new X12Segment("XX*067*20201117*000000");
        FOBRelatedInstructions fob = FOBRelatedInstructionsParser.parse(segment);
        assertNull(fob);
    }
}
