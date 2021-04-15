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
import com.walmartlabs.x12.common.segment.FOBRelatedInstructions;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class FOBRelatedInstructionsParserTest {


    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        FOBRelatedInstructions fob = FOBRelatedInstructionsParser.parse(segment);
        assertNull(fob);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        FOBRelatedInstructions fob = FOBRelatedInstructionsParser.parse(segment);
        assertNull(fob);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("FOB*PP");
        FOBRelatedInstructions fob = FOBRelatedInstructionsParser.parse(segment);
        assertNotNull(fob);
        assertEquals("PP", fob.getPaymentCode());
    }

    @Test
    public void test_parse_segment_bad_identifier() {
        X12Segment segment = new X12Segment("XX*067*20201117*000000");
        FOBRelatedInstructions fob = FOBRelatedInstructionsParser.parse(segment);
        assertNull(fob);
    }
}
