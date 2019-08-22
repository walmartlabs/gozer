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

import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class X12Segment {
    
    public static final String DEFAULT_ELEMENT_SEPARATOR = "*";
    
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
        if (segmentElements != null && !segmentElements.isEmpty()) {
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
            return Collections.emptyList();
        } else {
            String splitRegEx = "\\" + DEFAULT_ELEMENT_SEPARATOR;
            return Arrays.asList(segment.split(splitRegEx));
        }
    }
}
