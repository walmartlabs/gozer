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

package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.DTMDateTimeReference;

public final class DTMDateTimeReferenceParser {

    /**
     * parse the segment
     * 
     * @param segment
     * @return
     */
    public static DTMDateTimeReference parse(X12Segment segment) {
        DTMDateTimeReference dtm = null;
        
        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (DTMDateTimeReference.IDENTIFIER.equals(segmentIdentifier)) {
                dtm = new DTMDateTimeReference();
                dtm.setDateTimeQualifier(segment.getElement(1));
                dtm.setDate(segment.getElement(2));
                dtm.setTime(segment.getElement(3));
            }
        }
        
        return dtm;
    }
    

    private DTMDateTimeReferenceParser() {
        // you can't make me
    }
}
