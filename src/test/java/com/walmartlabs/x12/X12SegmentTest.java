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
package com.walmartlabs.x12;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class X12SegmentTest {

    @Test
    public void test_SegmentId_ThreeCharacter() {
        X12Segment segment = new X12Segment("DXS*9251230013*DX*004010UCS*1*9254850000");
        assertNotNull(segment);
        assertEquals(6, segment.segmentSize());
        assertEquals("DXS", segment.getSegmentIdentifier());
        assertEquals("DXS", segment.getSegmentElement(0));
        assertEquals("9251230013", segment.getSegmentElement(1));
        assertEquals("DX", segment.getSegmentElement(2));
        assertEquals("004010UCS", segment.getSegmentElement(3));
        assertEquals("1", segment.getSegmentElement(4));
        assertEquals("9254850000", segment.getSegmentElement(5));
        assertEquals(null, segment.getSegmentElement(6));
    }


    @Test
    public void test_SegmentId_TwoCharacter() {
        X12Segment segment = new X12Segment("ST*9251230013*DX*004010UCS*1*9254850000");
        assertNotNull(segment);
        assertEquals(6, segment.segmentSize());
        assertEquals("ST", segment.getSegmentIdentifier());
        assertEquals("ST", segment.getSegmentElement(0));
        assertEquals("9251230013", segment.getSegmentElement(1));
        assertEquals("DX", segment.getSegmentElement(2));
        assertEquals("004010UCS", segment.getSegmentElement(3));
        assertEquals("1", segment.getSegmentElement(4));
        assertEquals("9254850000", segment.getSegmentElement(5));
        assertEquals(null, segment.getSegmentElement(6));
    }

    @Test
    public void test_SegmentId_None() {
        X12Segment segment = new X12Segment("*ST*9251230013*DX*004010UCS*1*9254850000");
        assertNotNull(segment);
        assertEquals(7, segment.segmentSize());
        assertEquals("", segment.getSegmentIdentifier());
        assertEquals(null, segment.getSegmentElement(0));
        assertEquals("ST", segment.getSegmentElement(1));
        assertEquals("9251230013", segment.getSegmentElement(2));
        assertEquals("DX", segment.getSegmentElement(3));
        assertEquals("004010UCS", segment.getSegmentElement(4));
        assertEquals("1", segment.getSegmentElement(5));
        assertEquals("9254850000", segment.getSegmentElement(6));
        assertEquals(null, segment.getSegmentElement(7));
    }

    @Test
    public void test_SegmentId_NoAsterisk() {
        X12Segment segment = new X12Segment("TESTING 123");
        assertNotNull(segment);
        assertEquals(1, segment.segmentSize());
        assertEquals("TESTING 123", segment.getSegmentIdentifier());
    }

    @Test
    public void test_SegmentId_Empty() {
        X12Segment segment = new X12Segment("");
        assertNotNull(segment);
        assertEquals(0, segment.segmentSize());
        assertEquals("", segment.getSegmentIdentifier());
    }

    @Test
    public void test_SegmentId_Null() {
        X12Segment segment = new X12Segment(null);
        assertNotNull(segment);
        assertEquals(0, segment.segmentSize());
        assertEquals("", segment.getSegmentIdentifier());
    }

}
