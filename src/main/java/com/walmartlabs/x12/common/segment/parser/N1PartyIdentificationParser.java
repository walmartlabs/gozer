package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.N1PartyIdentification;

public final class N1PartyIdentificationParser {
    
    public static N1PartyIdentification parse(X12Segment segment) {
        N1PartyIdentification n1 = null;

        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (N1PartyIdentification.PARTY_IDENTIFICATION_IDENTIFIER.equals(segmentIdentifier)) {
                n1 = new N1PartyIdentification();
                n1.setEntityIdentifierCode(segment.getElement(1));
                n1.setName(segment.getElement(2));
                n1.setIdentificationCodeQualifier(segment.getElement(3));
                n1.setIdentificationCode(segment.getElement(4));
             }
        }
        return n1;
    }
    
    private N1PartyIdentificationParser() {
        // you can't make me
    }
}
