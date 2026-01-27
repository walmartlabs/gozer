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
import com.walmartlabs.x12.common.segment.DTMDateTimeReference;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DTMDateTimeReferenceParserTest {


    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        DTMDateTimeReference dtm = DTMDateTimeReferenceParser.parse(segment);
        assertNull(dtm);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        DTMDateTimeReference dtm = DTMDateTimeReferenceParser.parse(segment);
        assertNull(dtm);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("DTM*067*20201117*000000");
        DTMDateTimeReference dtm = DTMDateTimeReferenceParser.parse(segment);
        assertNotNull(dtm);
        assertEquals("067", dtm.getDateTimeQualifier());
        assertEquals("20201117", dtm.getDate());
        assertEquals("000000", dtm.getTime());
    }

    @Test
    public void test_parse_segment_bad_identifier() {
        X12Segment segment = new X12Segment("XX*067*20201117*000000");
        DTMDateTimeReference dtm = DTMDateTimeReferenceParser.parse(segment);
        assertNull(dtm);
    }

}
