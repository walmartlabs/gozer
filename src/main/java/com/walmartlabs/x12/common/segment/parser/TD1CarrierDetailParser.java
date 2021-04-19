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
import com.walmartlabs.x12.common.segment.TD1CarrierDetail;
import org.springframework.util.StringUtils;

public final class TD1CarrierDetailParser {

    /**
     * parse the segment
     * @param segment
     * @return
     */
    public static TD1CarrierDetail parse(X12Segment segment) {
        TD1CarrierDetail td1 = null;

        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (TD1CarrierDetail.IDENTIFIER.equals(segmentIdentifier)) {
                td1 = new TD1CarrierDetail();
                String packagingCode = segment.getElement(1);
                td1.setRawPackagingCode(packagingCode);
                if (!StringUtils.isEmpty(packagingCode)) {
                    if (packagingCode.length() == 3 || packagingCode.length() == 5) {
                        td1.setPackagingCodePartOne(packagingCode.substring(0, 3));
                        td1.setPackagingCodePartTwo(packagingCode.substring(3));
                    }
                }
                td1.setLadingQuantity(segment.getElement(2));
                td1.setWeightQualifier(segment.getElement(6));
                td1.setWeight(segment.getElement(7));
                td1.setUnitOfMeasure(segment.getElement(8));
            }
        }
        return td1;
    }

    private TD1CarrierDetailParser() {
        // you can't make me
    }
}
