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
import com.walmartlabs.x12.asn856.segment.SN1ItemDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;
import org.junit.Test;

import static org.junit.Assert.*;

public class SN1ItemDetailParserTest {

    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        SN1ItemDetail sn1 = SN1ItemDetailParser.parse(segment);
        assertNull(sn1);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        SN1ItemDetail sn1 = SN1ItemDetailParser.parse(segment);
        assertNull(sn1);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("SN1**18*EA");
        SN1ItemDetail sn1 = SN1ItemDetailParser.parse(segment);
        assertNotNull(sn1);
        assertEquals("18.000000", sn1.getNumberOfUnits().toString());
        assertEquals("EA", sn1.getUnitOfMeasurement());
    }
    
    @Test
    public void test_parse_segment_pounds() {
        X12Segment segment = new X12Segment("SN1**21.12*LB");
        SN1ItemDetail sn1 = SN1ItemDetailParser.parse(segment);
        assertNotNull(sn1);
        assertEquals("21.120000", sn1.getNumberOfUnits().toString());
        assertEquals("LB", sn1.getUnitOfMeasurement());
    }
    
    @Test(expected = X12ParserException.class)
    public void test_parse_segment_bad_value() {
        X12Segment segment = new X12Segment("SN1**X*EA");
        SN1ItemDetailParser.parse(segment);
    }
}
