package com.walmartlabs.x12.asn856.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.asn856.segment.PRFPurchaseOrderReference;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PRFPurchaseOrderReferenceParserTest {

    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        PRFPurchaseOrderReference prf = PRFPurchaseOrderReferenceParser.parse(segment);
        assertNull(prf);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        PRFPurchaseOrderReference prf = PRFPurchaseOrderReferenceParser.parse(segment);
        assertNull(prf);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("PRF*0391494868");
        PRFPurchaseOrderReference prf = PRFPurchaseOrderReferenceParser.parse(segment);
        assertNotNull(prf);
        assertEquals("0391494868", prf.getPurchaseOrderNumber());
    }
}
