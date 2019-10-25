package com.walmartlabs.x12;

import com.walmartlabs.x12.SegmentIterator;
import com.walmartlabs.x12.X12Segment;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        assertNextWithNoSuchElementException(iter);

        assertFalse(iter.hasPrevious());
        assertEquals(-1, iter.previousIndex());
        assertPreviousWithNoSuchElementException(iter);
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
        assertEquals("LINE1", segment.getElement(0));

        // advance again
        assertTrue(iter.hasNext());
        assertEquals(1, iter.nextIndex());
        segment = iter.next();
        assertNotNull(segment);
        assertEquals("LINE2", segment.getElement(0));

        // advance again
        assertFalse(iter.hasNext());
        assertEquals(2, iter.nextIndex());
        assertNextWithNoSuchElementException(iter);
    }

    @Test
    public void test_previous_on_list() {
        List<X12Segment> segmentLines = this.generateTestData();
        SegmentIterator iter = new SegmentIterator(segmentLines);

        // previous at the start
        assertFalse(iter.hasPrevious());
        assertEquals(-1, iter.previousIndex());
        assertPreviousWithNoSuchElementException(iter);

        // advance one
        assertTrue(iter.hasNext());
        assertEquals(0, iter.nextIndex());
        X12Segment segment = iter.next();
        assertNotNull(segment);
        assertEquals("LINE1", segment.getElement(0));

        // advance again
        assertTrue(iter.hasNext());
        assertEquals(1, iter.nextIndex());
        segment = iter.next();
        assertNotNull(segment);
        assertEquals("LINE2", segment.getElement(0));

        // previous
        assertTrue(iter.hasPrevious());
        assertEquals(1, iter.previousIndex());
        segment = iter.previous();
        assertNotNull(segment);
        assertEquals("LINE2", segment.getElement(0));

        // previous again
        assertTrue(iter.hasPrevious());
        assertEquals(0, iter.previousIndex());
        segment = iter.previous();
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
