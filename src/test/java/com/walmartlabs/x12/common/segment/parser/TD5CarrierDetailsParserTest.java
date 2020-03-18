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
import com.walmartlabs.x12.common.segment.TD5CarrierDetails;
import com.walmartlabs.x12.common.segment.parser.TD5CarrierDetailsParser;
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
