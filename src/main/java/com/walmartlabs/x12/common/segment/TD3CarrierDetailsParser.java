package com.walmartlabs.x12.common.segment;

import com.walmartlabs.x12.X12Segment;

public final class TD3CarrierDetailsParser {
    
    public static TD3CarrierDetails parse(X12Segment segment) {
        TD3CarrierDetails td3 = null;
        
        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (TD3CarrierDetails.CARRIER_DETAILS_IDENTIFIER.equals(segmentIdentifier)) {
                td3 = new TD3CarrierDetails();
                td3.setEquipmentDescriptionCode(segment.getElement(1));
                td3.setEquipmentInitial(segment.getElement(2));
                td3.setEquipmentNumber(segment.getElement(3));
                td3.setSealNumber(segment.getElement(9));
             }
        }
        return td3;
    }
    
    private TD3CarrierDetailsParser() {
        // you can't make me
    }
}
