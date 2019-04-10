package com.walmartlabs.x12.util;

import com.walmartlabs.x12.X12Segment;

import java.util.List;

public class SegmentUtil {

    private List<X12Segment> segmentLines;
    private int currentSegmentIdx;

    public SegmentUtil(List<X12Segment> segmentLines) {
        currentSegmentIdx = 0;
        this.segmentLines = segmentLines;
    }

    /**
     * compare the current segment index and see if
     * there are any more segment lines after it
     */
    public boolean hasMoreSegmentLines() {
        return (currentSegmentIdx < segmentLines.size()) ;
    }

    /**
     * retrieve the Segment that the current index is pointing to
     * and advance the current index by one
     * if there is not another segment return null
     */
    public X12Segment nextSegment() {
        if (this.hasMoreSegmentLines()) {
            return segmentLines.get(currentSegmentIdx++);
        } else {
            return null;
        }
    }

    /**
     * retrieve the Segment that is AFTER the current index without
     * advancing the current index
     * if there is not another segment return null
     */
    public X12Segment peekSegment() {
        if (this.hasMoreSegmentLines()) {
            return segmentLines.get(currentSegmentIdx);
        } else {
            return null;
        }
    }

}
