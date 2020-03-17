package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.TD3CarrierDetails;
import com.walmartlabs.x12.common.segment.parser.TD3CarrierDetailsParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TD3CarrierDetailsParserTest {


    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        TD3CarrierDetails td3 = TD3CarrierDetailsParser.parse(segment);
        assertNull(td3);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        TD3CarrierDetails td3 = TD3CarrierDetailsParser.parse(segment);
        assertNull(td3);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("TD3*TL");
        TD3CarrierDetails td3 = TD3CarrierDetailsParser.parse(segment);
        assertNotNull(td3);
        assertEquals("TL", td3.getEquipmentDescriptionCode());
        assertEquals(null, td3.getEquipmentInitial());
        assertEquals(null, td3.getEquipmentNumber());
        assertEquals(null, td3.getSealNumber());
    }
    
    @Test
    public void test_parse_segment_more_data_elements() {
        X12Segment segment = new X12Segment("TD3*TL*SCAC*7771******SEAL");
        TD3CarrierDetails td3 = TD3CarrierDetailsParser.parse(segment);
        assertNotNull(td3);
        assertEquals("TL", td3.getEquipmentDescriptionCode());
        assertEquals("SCAC", td3.getEquipmentInitial());
        assertEquals("7771", td3.getEquipmentNumber());
        assertEquals("SEAL", td3.getSealNumber());
    }

}
