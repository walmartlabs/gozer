package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.REFReferenceInformation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class REFReferenceInformationParserTest {

    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        REFReferenceInformation ref = REFReferenceInformationParser.parse(segment);
        assertNull(ref);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        REFReferenceInformation ref = REFReferenceInformationParser.parse(segment);
        assertNull(ref);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("REF*UCB*711170010491361");
        REFReferenceInformation ref = REFReferenceInformationParser.parse(segment);
        assertNotNull(ref);
        assertEquals("UCB", ref.getReferenceIdentificationQualifier());
        assertEquals("711170010491361", ref.getReferenceIdentification());
    }
}
