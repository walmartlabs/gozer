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
import com.walmartlabs.x12.common.segment.LINItemIdentification;

import java.util.ArrayList;
import java.util.List;

public final class LINItemIdentificationParser {

    /**
     * parse the segment
     * 
     * @param segment
     * @return
     */
    public static List<LINItemIdentification> parse(X12Segment segment) {
        List<LINItemIdentification> itemIdList = new ArrayList<>();

        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (LINItemIdentification.IDENTIFIER.equals(segmentIdentifier)) {
                int elements = segment.segmentSize();
                for (int i = 2; i < elements; i += 2) {
                    // skipping LIN00 and LIN01
                    // LIN lines can store more than one
                    // item identifier on the same segment line
                    LINItemIdentification lin = new LINItemIdentification();
                    lin.setProductIdQualifier(segment.getElement(i));
                    lin.setProductId(segment.getElement(i + 1));
                    itemIdList.add(lin);
                }

            }
        }
        return itemIdList;
    }

    private LINItemIdentificationParser() {
        // you can't make me
    }
}
