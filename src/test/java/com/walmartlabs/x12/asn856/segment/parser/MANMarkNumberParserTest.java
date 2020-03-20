package com.walmartlabs.x12.asn856.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.asn856.segment.MANMarkNumber;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class MANMarkNumberParserTest {


    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        MANMarkNumber man = MANMarkNumberParser.parse(segment);
        assertNull(man);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        MANMarkNumber man = MANMarkNumberParser.parse(segment);
        assertNull(man);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("MAN*UC*10081131916931");
        MANMarkNumber man = MANMarkNumberParser.parse(segment);
        assertNotNull(man);
        assertEquals("UC", man.getQualifier());
        assertEquals("10081131916931", man.getNumber());
    }
}
