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

package com.walmartlabs.x12.asn856.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.asn856.segment.PO4ItemPhysicalDetail;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PO4ItemPhysicalDetailParserTest {

    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        PO4ItemPhysicalDetail po4 = PO4ItemPhysicalDetailParser.parse(segment);
        assertNull(po4);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        PO4ItemPhysicalDetail po4 = PO4ItemPhysicalDetailParser.parse(segment);
        assertNull(po4);
    }

    @Test
    public void test_parse_segment_dimensions() {
        X12Segment segment = new X12Segment("PO4**********60*40*17.2*CM");
        PO4ItemPhysicalDetail po4 = PO4ItemPhysicalDetailParser.parse(segment);
        assertNotNull(po4);
        assertEquals("60", po4.getLength());
        assertEquals("40", po4.getWidth());
        assertEquals("17.2", po4.getHeight());
        assertEquals("CM", po4.getUnitOfMeasurement());
        assertNull(po4.getAssignedIdentification());
    }
    
    @Test
    public void test_parse_segment_standard() {
        X12Segment segment = new X12Segment("PO4****************RPC6413");
        PO4ItemPhysicalDetail po4 = PO4ItemPhysicalDetailParser.parse(segment);
        assertNotNull(po4);
        assertNull(po4.getLength());
        assertNull(po4.getWidth());
        assertNull(po4.getHeight());
        assertNull(po4.getUnitOfMeasurement());
        assertEquals("RPC6413", po4.getAssignedIdentification());
    }
}
