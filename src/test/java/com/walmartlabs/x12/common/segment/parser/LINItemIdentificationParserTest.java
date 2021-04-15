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
import com.walmartlabs.x12.common.segment.LINItemIdentification;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LINItemIdentificationParserTest {


    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        List<LINItemIdentification> linList = LINItemIdentificationParser.parse(segment);
        assertNotNull(linList);
        assertEquals(0, linList.size());
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        List<LINItemIdentification> linList = LINItemIdentificationParser.parse(segment);
        assertNotNull(linList);
        assertEquals(0, linList.size());
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("LIN*01*UP*061414000010*IN*236016748*UK*01353784182399*CH*US-FL");
        List<LINItemIdentification> linList = LINItemIdentificationParser.parse(segment);
        assertNotNull(linList);
        assertEquals(4, linList.size());
        
        assertEquals("UP", linList.get(0).getProductIdQualifier());
        assertEquals("061414000010", linList.get(0).getProductId());
        
        assertEquals("IN", linList.get(1).getProductIdQualifier());
        assertEquals("236016748", linList.get(1).getProductId());
        
        assertEquals("UK", linList.get(2).getProductIdQualifier());
        assertEquals("01353784182399", linList.get(2).getProductId());
        
        assertEquals("CH", linList.get(3).getProductIdQualifier());
        assertEquals("US-FL", linList.get(3).getProductId());
    }
    
    @Test
    public void test_parse_segment_not_balanced() {
        X12Segment segment = new X12Segment("LIN*01*UP*061414000010*IN*236016748*UK");
        List<LINItemIdentification> linList = LINItemIdentificationParser.parse(segment);
        assertNotNull(linList);
        assertEquals(3, linList.size());
        
        assertEquals("UP", linList.get(0).getProductIdQualifier());
        assertEquals("061414000010", linList.get(0).getProductId());
        
        assertEquals("IN", linList.get(1).getProductIdQualifier());
        assertEquals("236016748", linList.get(1).getProductId());
        
        assertEquals("UK", linList.get(2).getProductIdQualifier());
        assertEquals(null, linList.get(2).getProductId());
    }

}
