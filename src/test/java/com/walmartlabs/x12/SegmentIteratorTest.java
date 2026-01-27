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

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SegmentIteratorTest {

    @Test
    public void test_with_null_list() {
        List<X12Segment> segmentLines = null;
        assertThrows(IllegalArgumentException.class, () -> new SegmentIterator(segmentLines));
    }

    @Test
    public void test_with_empty_list() {
        List<X12Segment> segmentLines = Collections.emptyList();
        SegmentIterator iter = new SegmentIterator(segmentLines);

        assertFalse(iter.hasNext());
        assertEquals(0, iter.nextIndex());
        assertNextWithNoSuchElementException(iter);

        assertFalse(iter.hasPrevious());
        assertEquals(-1, iter.previousIndex());
        assertPreviousWithNoSuchElementException(iter);

        assertEquals(0, iter.currentIndex());
        assertEquals(-1, iter.lastIndex());
    }

    @Test
    public void test_advancing_with_next() {
        List<X12Segment> segmentLines = this.generateTestData();
        SegmentIterator iter = new SegmentIterator(segmentLines);

        // starting point
        assertTrue(iter.hasNext());
        assertFalse(iter.hasPrevious());
        assertEquals(0, iter.nextIndex());
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.currentIndex());
        assertEquals(1, iter.lastIndex());

        // advance one
        X12Segment segment = iter.next();
        assertTrue(iter.hasNext());
        assertTrue(iter.hasPrevious());
        assertEquals(1, iter.nextIndex());
        assertEquals(0, iter.previousIndex());
        assertEquals(1, iter.currentIndex());
        assertEquals(1, iter.lastIndex());

        assertNotNull(segment);
        assertEquals("LINE1", segment.getElement(0));

        // advance again
        segment = iter.next();
        assertFalse(iter.hasNext());
        assertTrue(iter.hasPrevious());
        assertEquals(2, iter.nextIndex());
        assertEquals(1, iter.previousIndex());
        assertEquals(2, iter.currentIndex());
        assertEquals(1, iter.lastIndex());

        assertNotNull(segment);
        assertEquals("LINE2", segment.getElement(0));

        // advance again
        assertNextWithNoSuchElementException(iter);
    }

    @Test
    public void test_previous_on_list() {
        List<X12Segment> segmentLines = this.generateTestData();
        SegmentIterator iter = new SegmentIterator(segmentLines);

        // starting point
        assertTrue(iter.hasNext());
        assertFalse(iter.hasPrevious());
        assertEquals(0, iter.nextIndex());
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.currentIndex());
        assertEquals(1, iter.lastIndex());

        // try getting previous from starting point
        assertPreviousWithNoSuchElementException(iter);

        // advance one
        X12Segment segment = iter.next();
        assertNotNull(segment);
        assertEquals("LINE1", segment.getElement(0));

        // advance again
        segment = iter.next();
        assertNotNull(segment);
        assertEquals("LINE2", segment.getElement(0));

        // at the end of iterator
        assertFalse(iter.hasNext());
        assertTrue(iter.hasPrevious());
        assertEquals(2, iter.nextIndex());
        assertEquals(1, iter.previousIndex());
        assertEquals(2, iter.currentIndex());
        assertEquals(1, iter.lastIndex());

        // move back
        segment = iter.previous();
        assertTrue(iter.hasNext());
        assertTrue(iter.hasPrevious());
        assertEquals(1, iter.nextIndex());
        assertEquals(0, iter.previousIndex());
        assertEquals(1, iter.currentIndex());
        assertEquals(1, iter.lastIndex());

        assertNotNull(segment);
        assertEquals("LINE2", segment.getElement(0));

        // move back again
        segment = iter.previous();
        assertTrue(iter.hasNext());
        assertFalse(iter.hasPrevious());
        assertEquals(0, iter.nextIndex());
        assertEquals(-1, iter.previousIndex());
        assertEquals(0, iter.currentIndex());
        assertEquals(1, iter.lastIndex());

        assertNotNull(segment);
        assertEquals("LINE1", segment.getElement(0));

    }

    private List<X12Segment> generateTestData() {
        List<X12Segment> segmentLines = new ArrayList<>();
        segmentLines.add(new X12Segment("LINE1"));
        segmentLines.add(new X12Segment("LINE2"));
        return segmentLines;
    }

    private void assertNextWithNoSuchElementException(SegmentIterator iter) {
        try {
            iter.next();
            fail("expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // ignore
        }
    }

    private void assertPreviousWithNoSuchElementException(SegmentIterator iter) {
        try {
            iter.previous();
            fail("expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // ignore
        }
    }

}
