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
import com.walmartlabs.x12.common.segment.REFReferenceInformation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class REFReferenceInformationParserTest {

    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        REFReferenceInformation ref = REFReferenceInformationParser.parse(segment);
        assertNull(ref);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        REFReferenceInformation ref = REFReferenceInformationParser.parse(segment);
        assertNull(ref);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("REF*UCB*711170010491361*TEST*BM>1234");
        REFReferenceInformation ref = REFReferenceInformationParser.parse(segment);
        assertNotNull(ref);
        assertEquals("UCB", ref.getReferenceIdentificationQualifier());
        assertEquals("711170010491361", ref.getReferenceIdentification());
        assertEquals("TEST", ref.getDescription());
        assertEquals("BM>1234", ref.getAdditionalReferenceIdentification());
    }
}
