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

import com.walmartlabs.x12.SegmentIterator;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.DTMDateTimeReference;
import com.walmartlabs.x12.common.segment.FOBRelatedInstructions;
import com.walmartlabs.x12.common.segment.REFReferenceInformation;
import com.walmartlabs.x12.common.segment.TD3CarrierDetail;

public final class TD3CarrierDetailParser {

    /**
     * parse the segment
     * @param segment
     * @return
     */
    public static TD3CarrierDetail parse(X12Segment segment) {
        TD3CarrierDetail td3 = null;

        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (TD3CarrierDetail.IDENTIFIER.equals(segmentIdentifier)) {
                td3 = new TD3CarrierDetail();
                td3.setEquipmentDescriptionCode(segment.getElement(1));
                td3.setEquipmentInitial(segment.getElement(2));
                td3.setEquipmentNumber(segment.getElement(3));
                td3.setSealNumber(segment.getElement(9));
            }
        }
        return td3;
    }
    
    /**
     * parse the TD3 segment and "attach" all related segment lines
     * this is the preferred method to use
     * @param segment
     * @param segmentIterator
     * @return
     */
    public static TD3CarrierDetail handleTD3Loop(X12Segment segment, SegmentIterator segmentIterator) {
        TD3CarrierDetail td3 = TD3CarrierDetailParser.parse(segment);
        boolean keepLooping = true;
        while (td3 != null && keepLooping && segmentIterator.hasNext()) {
            X12Segment nextSegment = segmentIterator.next();
            switch (nextSegment.getIdentifier()) {
                case REFReferenceInformation.IDENTIFIER:
                    td3.addReferenceInformation(REFReferenceInformationParser.parse(nextSegment));
                    break;                    
                case DTMDateTimeReference.IDENTIFIER:
                    td3.addDTMDateTimeReference(DTMDateTimeReferenceParser.parse(nextSegment));
                    break;
                case FOBRelatedInstructions.IDENTIFIER:
                    td3.setFob(FOBRelatedInstructionsParser.parse(nextSegment));
                    break;                    
                default:
                    // assume any other identifier is a break out of the TD3 loop
                    // and let the other parser deal with it
                    segmentIterator.previous();
                    keepLooping = false;
                    break;
            }
        }
        return td3;
    }

    private TD3CarrierDetailParser() {
        // you can't make me
    }
}
