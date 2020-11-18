package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.DTMDateTimeReference;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DTMDateTimeReferenceParserTest {


    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        DTMDateTimeReference dtm = DTMDateTimeReferenceParser.parse(segment);
        assertNull(dtm);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        DTMDateTimeReference dtm = DTMDateTimeReferenceParser.parse(segment);
        assertNull(dtm);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("DTM*067*20201117*000000");
        DTMDateTimeReference dtm = DTMDateTimeReferenceParser.parse(segment);
        assertNotNull(dtm);
        assertEquals("067", dtm.getDateTimeQualifier());
        assertEquals("20201117", dtm.getDate());
        assertEquals("000000", dtm.getTime());
    }

    @Test
    public void test_parse_segment_bad_identifier() {
        X12Segment segment = new X12Segment("XX*067*20201117*000000");
        DTMDateTimeReference dtm = DTMDateTimeReferenceParser.parse(segment);
        assertNull(dtm);
    }

}
