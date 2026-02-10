/**
Copyright (c) 2018-present, Walmart, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.TD3CarrierDetail;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TD3CarrierDetailParserTest {

    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        TD3CarrierDetail td3 = TD3CarrierDetailParser.parse(segment);
        assertNull(td3);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        TD3CarrierDetail td3 = TD3CarrierDetailParser.parse(segment);
        assertNull(td3);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("TD3*TL");
        TD3CarrierDetail td3 = TD3CarrierDetailParser.parse(segment);
        assertNotNull(td3);
        assertEquals("TL", td3.getEquipmentDescriptionCode());
        assertEquals(null, td3.getEquipmentInitial());
        assertEquals(null, td3.getEquipmentNumber());
        assertEquals(null, td3.getSealNumber());
    }

    @Test
    public void test_parse_segment_more_data_elements() {
        X12Segment segment = new X12Segment("TD3*TL*SCAC*7771******SEAL");
        TD3CarrierDetail td3 = TD3CarrierDetailParser.parse(segment);
        assertNotNull(td3);
        assertEquals("TL", td3.getEquipmentDescriptionCode());
        assertEquals("SCAC", td3.getEquipmentInitial());
        assertEquals("7771", td3.getEquipmentNumber());
        assertEquals("SEAL", td3.getSealNumber());
    }

}
