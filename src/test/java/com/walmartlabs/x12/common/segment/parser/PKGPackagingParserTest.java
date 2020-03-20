package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.PKGPackaging;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PKGPackagingParserTest {

    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        PKGPackaging pkg = PKGPackagingParser.parse(segment);
        assertNull(pkg);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        PKGPackaging pkg = PKGPackagingParser.parse(segment);
        assertNull(pkg);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("PKG*S*68*FD*52");
        PKGPackaging pkg = PKGPackagingParser.parse(segment);
        assertNotNull(pkg);
        assertEquals("S", pkg.getItemDescriptionType());
        assertEquals("68", pkg.getPackagingCharacteristicCode());
        assertEquals("FD", pkg.getAgencyQualifierCode());
        assertEquals("52", pkg.getPackagingDescriptionCode());
    }

}
