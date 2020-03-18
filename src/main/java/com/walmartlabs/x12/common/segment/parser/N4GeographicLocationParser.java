package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.N4GeographicLocation;


public final class N4GeographicLocationParser {
    
    public static N4GeographicLocation parse(X12Segment segment) {
        N4GeographicLocation n4 = null;

        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (N4GeographicLocation.PARTY_GEOGRAPHIC_IDENTIFIER.equals(segmentIdentifier)) {
                n4 = new N4GeographicLocation();
                n4.setCityName(segment.getElement(1));
                n4.setStateOrProvinceCode(segment.getElement(2));
                n4.setPostalCode(segment.getElement(3));
                n4.setCountryCode(segment.getElement(4));
             }
        }
        return n4;
    }
    
    private N4GeographicLocationParser() {
        // you can't make me
    }
}
