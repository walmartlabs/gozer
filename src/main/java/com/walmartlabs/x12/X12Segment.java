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

import com.walmartlabs.x12.util.SourceToSegmentUtil;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *  Each line in an X12 document is called a segment
 *  Each segment contains one or more elements
 *  The first element identifies the type of segment
 *  
 *  This class will parse a segment into the individual elements
 */
public class X12Segment {
    
    private String segmentValue;
    private List<String> segmentElements;

    /**
     * create the {@link X12Segment} using the default delimiter
     * @param segment
     * @return {@link X12Segment}
     */
    public X12Segment(String segment) {
        this(segment, SourceToSegmentUtil.DEFAULT_DATA_ELEMENT_SEPARATOR);
    }
    
    /**
     * create the {@link X12Segment} using the delimiter provided
     * @param segment
     * @return {@link X12Segment}
     * @throws PatternSyntaxException if the delimiter results in invalid regular expression
     */
    public X12Segment(String segment, Character dataElementDelimiter) {
        segmentValue = segment;
        segmentElements = this.splitSegmentIntoDataElements(segment, dataElementDelimiter);
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
    public String getIdentifier() {
        if (segmentElements != null && !segmentElements.isEmpty()) {
            return segmentElements.get(0);
        } else {
            return "";
        }
    }

    /**
     * retrieve the element at a particular index in the segment
     */
    public String getElement(int index) {
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
    private List<String> splitSegmentIntoDataElements(String segment, Character dataElementDelimiter) {
        if (StringUtils.isEmpty(segment)) {
            return Collections.emptyList();
        } else {
            String splitRegEx = this.convertDataElementDelimiterToRegEx(dataElementDelimiter);
            return Arrays.asList(segment.split(splitRegEx));
        }
    }
    
    private String convertDataElementDelimiterToRegEx(Character dataElementDelimiter) {
        if (dataElementDelimiter != null) {
            if (Character.isLetterOrDigit(dataElementDelimiter.charValue())) {
                return dataElementDelimiter.toString();
            } else {
                return "\\" + dataElementDelimiter;
            }
        } else {
            return String.valueOf(Character.MIN_VALUE);
        }
    }
}
