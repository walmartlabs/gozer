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
import com.walmartlabs.x12.common.segment.PIDProductIdentification;

public final class PIDPartyIdentificationParser {

    /**
     * parse the segment
     * @param segment
     * @return
     */
    public static PIDProductIdentification parse(X12Segment segment) {
        PIDProductIdentification pid = null;

        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (PIDProductIdentification.PRODUCT_ID_IDENTIFIER.equals(segmentIdentifier)) {
                pid = new PIDProductIdentification();
                pid.setItemDescriptionType(segment.getElement(1));
                pid.setDescription(segment.getElement(5));
            }
        }
        return pid;
    }

    private PIDPartyIdentificationParser() {
        // you can't make me
    }
}
