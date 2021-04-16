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

import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class SegmentIterator implements ListIterator<X12Segment> {

    private List<X12Segment> segmentLines;
    private int currentSegmentIdx;

    public SegmentIterator(List<X12Segment> segmentLines) {
        if (segmentLines != null) {
            currentSegmentIdx = 0;
            this.segmentLines = segmentLines;
        } else {
            throw new IllegalArgumentException("segment list must not be null");
        }
    }

    /**
     * Returns true if this list iterator has more elements when traversing the list in the forward direction.
     */
    @Override
    public boolean hasNext() {
        return (currentSegmentIdx < segmentLines.size());
    }

    /**
     * Returns the next element in the list and advances the cursor position
     *
     * @throws NoSuchElementException
     *             - if the iteration has no next element
     */
    @Override
    public X12Segment next() {
        if (this.hasNext()) {
            return segmentLines.get(currentSegmentIdx++);
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Returns true if this list iterator has more elements when traversing the list in the reverse direction.
     */
    @Override
    public boolean hasPrevious() {
        return (currentSegmentIdx != 0);
    }

    /**
     * Returns the previous element in the list and moves the cursor position backwards.
     *
     * @throws NoSuchElementException
     *             - if the iteration has no next element
     */
    @Override
    public X12Segment previous() {
        if (this.hasPrevious()) {
            return segmentLines.get(--currentSegmentIdx);
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Returns the index of the element that would be returned by a subsequent call to next(). (Returns list size if the list iterator is at the end
     * of the list.)
     */
    @Override
    public int nextIndex() {
        if (this.hasNext()) {
            return currentSegmentIdx;
        } else {
            return segmentLines.size();
        }

    }

    /**
     * Returns the index of the element that would be returned by a subsequent call to previous(). (Returns -1 if the list iterator is at the
     * beginning of the list.)
     */
    @Override
    public int previousIndex() {
        if (this.hasPrevious()) {
            return (currentSegmentIdx - 1);
        } else {
            return -1;
        }
    }

    /**
     * Inserts the specified element into the list (optional operation).
     */
    @Override
    public void add(X12Segment segment) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes from the list the last element that was returned by next() or previous() (optional operation).
     */
    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     * Replaces the last element returned by next() or previous() with the specified element (optional operation).
     */
    @Override
    public void set(X12Segment segment) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * non-standard iterator method to return the current index
     */
    public int currentIndex() {
        return currentSegmentIdx;
    }

}
