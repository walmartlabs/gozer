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

import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface X12Parser<T extends X12Document> {
    
    public static final String DEFAULT_SEGMENT_SEPARATOR = "~";
    
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
     * 2) otherwise try default segment delimiter (~)
     * 3) otherwise try last character (TODO)
     */
    default List<X12Segment> splitSourceDataIntoSegments(String sourceData) {
        // assume that the source data has 
        // each segment on a separate line
        // and that ALL valid EDI / X12 documents
        // are > 1 segment 
        List<X12Segment> segments = splitSourceDataIntoSegments(sourceData, "\\r?\\n");
        if (segments.size() > 1) {
            return segments;
        } else {
            // if there is only one line in the source data
            // we should attempt to use the segment separator passed in
            // and see if we can split up this source data
            segments = splitSourceDataIntoSegments(sourceData, "\\" + DEFAULT_SEGMENT_SEPARATOR);
            return segments;
        }
    }
    
    /**
     * parses the source data into a list of segments 
     * using the the segment delimiter that was passed in
     * @param sourceData
     * @param segmentSeparator a regex to split segments
     */
    default List<X12Segment> splitSourceDataIntoSegments(String sourceData, String segmentSeparator) {
        if (StringUtils.isEmpty(sourceData)) {
            return Arrays.asList();
        } else {
            String segmentSeparatorRegEx = segmentSeparator;
            return Arrays.asList(sourceData.split(segmentSeparatorRegEx)).stream()
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
        StringBuilder sb = new StringBuilder("expected ");
        sb.append(expectedSegmentId);
        sb.append(" segment but found ");
        sb.append(actualSegmentId);
        throw new X12ParserException(new X12ErrorDetail(actualSegmentId, null, sb.toString()));
    }

}
