package com.walmartlabs.x12.util;

import com.walmartlabs.x12.X12Segment;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SegmentIteratorTest {

    @Test(expected = IllegalArgumentException.class)
    public void test_with_null_list() {
        List<X12Segment> segmentLines = null;
        new SegmentIterator(segmentLines);
    }

    @Test
    public void test_with_empty_list() {
        List<X12Segment> segmentLines = Collections.emptyList();
        SegmentIterator iter = new SegmentIterator(segmentLines);

        assertFalse(iter.hasNext());
        assertEquals(0, iter.nextIndex());
        assertNull(iter.next());

        assertFalse(iter.hasPrevious());
        assertEquals(-1, iter.previousIndex());
        assertNull(iter.previous());
    }

    @Test
    public void test_advancing_with_next() {
        List<X12Segment> segmentLines = this.generateTestData();
        SegmentIterator iter = new SegmentIterator(segmentLines);

        // advance one
        assertTrue(iter.hasNext());
        assertEquals(0, iter.nextIndex());
        X12Segment segment = iter.next();
        assertNotNull(segment);
        assertEquals("LINE1", segment.getSegmentElement(0));

        // advance again
        assertTrue(iter.hasNext());
        assertEquals(1, iter.nextIndex());
        segment = iter.next();
        assertNotNull(segment);
        assertEquals("LINE2", segment.getSegmentElement(0));

        // advance again
        assertFalse(iter.hasNext());
        assertEquals(2, iter.nextIndex());
        segment = iter.next();
        assertNull(segment);
    }

    @Test
    public void test_previous_on_list() {
        List<X12Segment> segmentLines = this.generateTestData();
        SegmentIterator iter = new SegmentIterator(segmentLines);

        // previous at the start
        assertFalse(iter.hasPrevious());
        assertEquals(-1, iter.previousIndex());
        assertNull(iter.previous());

        // advance one
        assertTrue(iter.hasNext());
        assertEquals(0, iter.nextIndex());
        X12Segment segment = iter.next();
        assertNotNull(segment);
        assertEquals("LINE1", segment.getSegmentElement(0));

        // advance again
        assertTrue(iter.hasNext());
        assertEquals(1, iter.nextIndex());
        segment = iter.next();
        assertNotNull(segment);
        assertEquals("LINE2", segment.getSegmentElement(0));

        // previous
        assertTrue(iter.hasPrevious());
        assertEquals(1, iter.previousIndex());
        segment = iter.previous();
        assertNotNull(segment);
        assertEquals("LINE2", segment.getSegmentElement(0));

        // previous again
        assertTrue(iter.hasPrevious());
        assertEquals(0, iter.previousIndex());
        segment = iter.previous();
        assertNotNull(segment);
        assertEquals("LINE1", segment.getSegmentElement(0));

    }

    private List<X12Segment> generateTestData() {
        List<X12Segment> segmentLines = new ArrayList<>();
        segmentLines.add(new X12Segment("LINE1"));
        segmentLines.add(new X12Segment("LINE2"));
        return segmentLines;
    }

}
