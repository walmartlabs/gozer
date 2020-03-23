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
import com.walmartlabs.x12.common.segment.TD5CarrierDetail;

public final class TD5CarrierDetailParser {

    /**
     * parse the segment
     * @param segment
     * @return
     */
    public static TD5CarrierDetail parse(X12Segment segment) {
        TD5CarrierDetail td5 = null;

        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (TD5CarrierDetail.IDENTIFIER.equals(segmentIdentifier)) {
                td5 = new TD5CarrierDetail();
                td5.setRoutingSequenceCode(segment.getElement(1));
                td5.setIdentificationCodeQualifier(segment.getElement(2));
                td5.setIdentificationCode(segment.getElement(3));
                td5.setTransportationMethodTypeCode(segment.getElement(4));
                td5.setRoutingDescription(segment.getElement(5));
            }
        }
        return td5;
    }

    private TD5CarrierDetailParser() {
        // you can't make me
    }
}
