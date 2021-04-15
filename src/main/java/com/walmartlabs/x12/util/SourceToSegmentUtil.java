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

package com.walmartlabs.x12.util;

import com.walmartlabs.x12.X12Segment;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This utility is used to help parse an EDI transmission
 * 
 *  It used to be part of the {@link X12Parser} but was
 *  pulled into a utility to make it more reusable
 */
public final class SourceToSegmentUtil {

    public static final Character DEFAULT_DATA_ELEMENT_SEPARATOR = '*';
    public static final Character DEFAULT_REPETITION_ELEMENT_SEPARATOR = '^';
    public static final Character DEFAULT_COMPOSITE_ELEMENT_SEPARATOR = ':';
    public static final Character DEFAULT_SEGMENT_SEPARATOR = '~';

    // note: Java Strings use a zero based index
    // so these are one less than the value 
    // provided in various EDI documentation
    public static final int DATA_ELEMENT_SEPARATOR_INDEX = 3;
    public static final int REPETITION_ELEMENT_SEPARATOR_INDEX = 82;
    public static final int COMPOSITE_ELEMENT_SEPARATOR_INDEX = 104;
    public static final int SEGMENT_SEPARATOR_INDEX = 105;
    
    /**
     * parses the source data into a list of segments 
     * 1) assume each segment is on separate line
     * 2) otherwise try 106th character in source data
     * 
     * @param sourceData
     * @return a {@link List} of {@link X12Segment} or empty if there are issues w/ source data
     */
    public static List<X12Segment> splitSourceDataIntoSegments(String sourceData) {
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
     * parses the source data into a list of segments 
     * using the the segment delimiter that was passed in
     * @param sourceData
     * @param segmentSeparatorRegEx a regex to split segments
     * @return a {@link List} of {@link X12Segment} or empty is either parameter is missing
     * @throws @{link PatternSyntaxException} if the regular expression is invalid
     */
    private static List<X12Segment> splitSourceDataIntoSegments(String sourceData, String segmentSeparatorRegEx) {
        if (StringUtils.isEmpty(sourceData) || StringUtils.isEmpty(segmentSeparatorRegEx)) {
            return Collections.emptyList();
        } else {
            Character segmentDataElementDelimiter = findElementDelimiterCharacter(sourceData);
            String[] segments = sourceData.split(segmentSeparatorRegEx);
            return Arrays.stream(segments)
                .map(segment -> new X12Segment(segment, segmentDataElementDelimiter))
                .collect(Collectors.toList());
        }
    }
    
    /**
     * get the segment delimiter/separator character
     * @param sourceData
     * @return the character at the 106th position or null if there are not enough characters
     */
    private static Character findSegmentDelimiterCharacter(String sourceData) {
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
    private static Character findElementDelimiterCharacter(String sourceData) {
        if (sourceData != null && sourceData.length() > DATA_ELEMENT_SEPARATOR_INDEX) {
            return Character.valueOf(sourceData.charAt(DATA_ELEMENT_SEPARATOR_INDEX));
        } else {
            return null;
        }
    }
    
    private SourceToSegmentUtil() {
        // you can't make me
    }
    
}
