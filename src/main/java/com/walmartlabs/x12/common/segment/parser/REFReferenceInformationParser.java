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
import com.walmartlabs.x12.common.segment.REFReferenceInformation;

public final class REFReferenceInformationParser {

    /**
     * parse the segment
     * @param segment
     * @return
     */
    public static REFReferenceInformation parse(X12Segment segment) {
        REFReferenceInformation ref = null;

        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (REFReferenceInformation.IDENTIFIER.equals(segmentIdentifier)) {
                ref = new REFReferenceInformation();
                ref.setReferenceIdentificationQualifier(segment.getElement(1));
                ref.setReferenceIdentification(segment.getElement(2));
                ref.setDescription(segment.getElement(3));
                ref.setAdditionalReferenceIdentification(segment.getElement(4));
            }
        }
        return ref;
    }

    private REFReferenceInformationParser() {
        // you can't make me
    }
}
