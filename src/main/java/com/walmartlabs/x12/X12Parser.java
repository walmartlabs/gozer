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

import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.util.X12ParsingUtil;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface X12Parser<T extends X12Document> {
    
    public static final String DEFAULT_DATA_ELEMENT_SEPARATOR = "*";
    public static final String DEFAULT_REPETITION_ELEMENT_SEPARATOR = "^";
    public static final String DEFAULT_COMPOSITE_ELEMENT_SEPARATOR = ":";
    public static final String DEFAULT_SEGMENT_SEPARATOR = "~";

    // note: Java Strings use a zero based index
    // so these are one less than the value 
    // provided in various EDI documentation
    public static final int DATA_ELEMENT_SEPARATOR_INDEX = 4;
    public static final int REPETITION_ELEMENT_SEPARATOR_INDEX = 82;
    public static final int COMPOSITE_ELEMENT_SEPARATOR_INDEX = 104;
    public static final int SEGMENT_SEPARATOR_INDEX = 105;
    

    /**
     * parse the X12 transmission into a representative Java object
     *
     * @return the representative Java object
     * @throws X12ParserException
     */
    T parse(String sourceData);

    /**
     * parses the source data into a list of segments 
     * 1) assume each segment is on separate line
     * 2) otherwise try 106th character in source data
     * 
     * @param sourceData
     * @return a {@link List} of {@link X12Segment} or empty if there are issues w/ source data
     */
    default List<X12Segment> splitSourceDataIntoSegments(String sourceData) {
        // assume that the source data has 
        // each segment on a separate line
        // and that ALL valid EDI / X12 documents
        // are > 1 segment 
        List<X12Segment> segments = splitSourceDataIntoSegments(sourceData, "\\r?\\n");
        if (segments != null && segments.size() > 1) {
            return segments;
        } else {
            // if there is only one line in the source data
            // now try to get the specified delimiter
            String segmentDelimiterRegex = "\\" + findSegmentDelimiterCharacter(sourceData);
            return splitSourceDataIntoSegments(sourceData, segmentDelimiterRegex);
        }
    }
    
    /**
     * get the segment delimiter/separator character
     * @param sourceData
     * @return the character at the 106th position or null if there are not enough characters
     */
    default Character findSegmentDelimiterCharacter(String sourceData) {
        if (sourceData != null && sourceData.length() > SEGMENT_SEPARATOR_INDEX) {
            return Character.valueOf(sourceData.charAt(SEGMENT_SEPARATOR_INDEX));
        } else {
            return null;
        }
    }
    
    /**
     * get the element delimiter/separator character
     * @param sourceData
     * @return the character at the 4th position or null if there are not enough characters
     */
    default Character findElementDelimiterCharacter(String sourceData) {
        if (sourceData != null && sourceData.length() > COMPOSITE_ELEMENT_SEPARATOR_INDEX) {
            return Character.valueOf(sourceData.charAt(COMPOSITE_ELEMENT_SEPARATOR_INDEX));
        } else {
            return null;
        }
    }
    
    /**
     * parses the source data into a list of segments 
     * using the the segment delimiter that was passed in
     * @param sourceData
     * @param segmentSeparatorRegEx a regex to split segments
     * @return a {@link List} of {@link X12Segment} or empty is either parameter is missing
     * @throws @{link PatternSyntaxException} if the regular expression is invalid
     */
    default List<X12Segment> splitSourceDataIntoSegments(String sourceData, String segmentSeparatorRegEx) {
        if (StringUtils.isEmpty(sourceData) || StringUtils.isEmpty(segmentSeparatorRegEx)) {
            return Collections.emptyList();
        } else {
            String[] segments = sourceData.split(segmentSeparatorRegEx);
            return Arrays.stream(segments)
                .map(segment -> new X12Segment(segment))
                .collect(Collectors.toList());
        }
    }

    /**
     * convenience method that will throw X12ParserException 
     * with a message indicating that the segment that was found
     * was not the one that was expected
     * @param expectedSegmentId
     * @param actualSegmentId
     * @throws {@link X12ParserException}
     */
    default void handleUnexpectedSegment(String expectedSegmentId, String actualSegmentId) {
        throw X12ParsingUtil.handleUnexpectedSegment(expectedSegmentId, actualSegmentId);
    }

}
