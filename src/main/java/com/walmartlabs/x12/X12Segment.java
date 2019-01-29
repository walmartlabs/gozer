package com.walmartlabs.x12;

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

public class X12Segment {
    private String segmentValue;
    private List<String> segmentElements;

    public X12Segment(String segment) {
        segmentValue = segment;
        segmentElements = this.splitSegmentIntoDataElements(segment);
    }

    /**
     * returns the original segment value
     */
    @Override
    public String toString() {
        return segmentValue;
    }

    /**
     * extracts the first data element in a segment which is the segment identifier
     * otherwise return an empty String
     */
    public String getSegmentIdentifier() {
        if (segmentElements.size() > 0) {
            return segmentElements.get(0);
        } else {
            return "";
        }
    }

    /**
     * retrieve the element at a particular index in the segment
     */
    public String getSegmentElement(int index) {
        if (segmentElements.size() > index) {
            String value = segmentElements.get(index);
            return StringUtils.isEmpty(value) ? null : value;
        } else {
            return null;
        }
    }

    public int segmentSize() {
        return segmentElements.size();
    }

    /**
     * parses the segment into a list of data elements
     * each date element is separated by an asterisk (*)
     */
    private List<String> splitSegmentIntoDataElements(String segment) {
        if (StringUtils.isEmpty(segment)) {
            return Arrays.asList();
        } else {
            return Arrays.asList(segment.split("\\*"));
        }
    }
}
