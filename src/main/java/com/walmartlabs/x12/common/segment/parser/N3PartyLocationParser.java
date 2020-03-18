package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.N3PartyLocation;


public final class N3PartyLocationParser {
    
    public static N3PartyLocation parse(X12Segment segment) {
        N3PartyLocation n3 = null;

        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (N3PartyLocation.PARTY_LOCATION_IDENTIFIER.equals(segmentIdentifier)) {
                n3 = new N3PartyLocation();
                n3.setAddressInfoOne(segment.getElement(1));
                n3.setAddressInfoTwo(segment.getElement(2));
             }
        }
        return n3;
    }
    
    private N3PartyLocationParser() {
        // you can't make me
    }
}
