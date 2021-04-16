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

package com.walmartlabs.x12.asn856.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.asn856.segment.PO4ItemPhysicalDetail;

public final class PO4ItemPhysicalDetailParser {

    /**
     * parse the segment
     * @param segment
     * @return
     */
    public static PO4ItemPhysicalDetail parse(X12Segment segment) {
        PO4ItemPhysicalDetail po4 = null;

        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (PO4ItemPhysicalDetail.IDENTIFIER.equals(segmentIdentifier)) {
                po4 = new PO4ItemPhysicalDetail();
                po4.setLength(segment.getElement(10));
                po4.setWidth(segment.getElement(11));
                po4.setHeight(segment.getElement(12));
                po4.setUnitOfMeasurement(segment.getElement(13));
                po4.setAssignedIdentification(segment.getElement(16));
            }
        }
        return po4;
    }

    private PO4ItemPhysicalDetailParser() {
        // you can't make me
    }
}
