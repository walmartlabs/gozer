package com.walmartlabs.x12.common.segment;

import com.walmartlabs.x12.X12Segment;

public final class TD5CarrierDetailsParser {
    
    public static TD5CarrierDetails parse(X12Segment segment) {
        TD5CarrierDetails td15 = null;
        
        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (TD5CarrierDetails.CARRIER_DETAILS_IDENTIFIER.equals(segmentIdentifier)) {
                td15 = new TD5CarrierDetails();
                td15.setRoutingSequenceCode(segment.getElement(1));
                td15.setIdentificationCodeQualifier(segment.getElement(2));
                td15.setIdentificationCode(segment.getElement(3));
                td15.setTransportationMethodTypeCode(segment.getElement(4));
                td15.setRoutingDescription(segment.getElement(5));
             }
        }
        return td15;
    }
    
    private TD5CarrierDetailsParser() {
        // you can't make me
    }
}
