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

public interface X12Parser<T extends X12Document> {
    
    /**
     * parse the X12 transmission into a representative Java object
     *
     * @return the representative Java object
     * @throws X12ParserException
     */
    T parse(String sourceData);

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
