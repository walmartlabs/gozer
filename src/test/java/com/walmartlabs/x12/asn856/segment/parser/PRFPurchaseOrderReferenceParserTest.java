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
