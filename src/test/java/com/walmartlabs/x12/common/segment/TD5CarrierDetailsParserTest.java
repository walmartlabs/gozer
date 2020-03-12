package com.walmartlabs.x12.common.segment;

import com.walmartlabs.x12.X12Segment;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TD5CarrierDetailsParserTest {


    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        TD5CarrierDetails td5 = TD5CarrierDetailsParser.parse(segment);
        assertNull(td5);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        TD5CarrierDetails td5 = TD5CarrierDetailsParser.parse(segment);
        assertNull(td5);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("TD5*B*2*UPSG*U*UPS GROUND");
        TD5CarrierDetails td5 = TD5CarrierDetailsParser.parse(segment);
        assertNotNull(td5);
        assertEquals("B", td5.getRoutingSequenceCode());
        assertEquals("2", td5.getIdentificationCodeQualifier());
        assertEquals("UPSG", td5.getIdentificationCode());
        assertEquals("U", td5.getTransportationMethodTypeCode());
        assertEquals("UPS GROUND", td5.getRoutingDescription());
    }

}
