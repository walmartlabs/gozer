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

package com.walmartlabs.x12.standard.txset.asn856.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.standard.txset.asn856.segment.PRFPurchaseOrderReference;

public final class PRFPurchaseOrderReferenceParser {

    /**
     * parse the segment
     * @param segment
     * @return
     */
    public static PRFPurchaseOrderReference parse(X12Segment segment) {
        PRFPurchaseOrderReference prf = null;

        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (PRFPurchaseOrderReference.IDENTIFIER.equals(segmentIdentifier)) {
                prf = new PRFPurchaseOrderReference();
                prf.setPurchaseOrderNumber(segment.getElement(1));
                prf.setDate(segment.getElement(4));
            }
        }
        return prf;
    }

    private PRFPurchaseOrderReferenceParser() {
        // you can't make me
    }
}
