package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.TD5CarrierDetails;

public final class TD5CarrierDetailsParser {
    
    public static TD5CarrierDetails parse(X12Segment segment) {
        TD5CarrierDetails td5 = null;
        
        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (TD5CarrierDetails.CARRIER_DETAILS_IDENTIFIER.equals(segmentIdentifier)) {
                td5 = new TD5CarrierDetails();
                td5.setRoutingSequenceCode(segment.getElement(1));
                td5.setIdentificationCodeQualifier(segment.getElement(2));
                td5.setIdentificationCode(segment.getElement(3));
                td5.setTransportationMethodTypeCode(segment.getElement(4));
                td5.setRoutingDescription(segment.getElement(5));
             }
        }
        return td5;
    }
    
    private TD5CarrierDetailsParser() {
        // you can't make me
    }
}
