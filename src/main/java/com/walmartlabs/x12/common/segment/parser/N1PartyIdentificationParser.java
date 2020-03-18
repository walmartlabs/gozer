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
import com.walmartlabs.x12.common.segment.N1PartyIdentification;
import com.walmartlabs.x12.common.segment.N3PartyLocation;
import com.walmartlabs.x12.common.segment.N4GeographicLocation;

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

    public static N1PartyIdentification handleN1Loop(X12Segment segment, SegmentIterator segmentIterator) {
        N1PartyIdentification n1 = N1PartyIdentificationParser.parse(segment);
        boolean keepLooping = true;
        while (n1 != null && keepLooping && segmentIterator.hasNext()) {
            X12Segment nextSegment = segmentIterator.next();
            switch (nextSegment.getIdentifier()) {
            case N3PartyLocation.PARTY_LOCATION_IDENTIFIER:
                n1.setN3(N3PartyLocationParser.parse(nextSegment));
                break;
            case N4GeographicLocation.PARTY_GEOGRAPHIC_IDENTIFIER:
                n1.setN4(N4GeographicLocationParser.parse(nextSegment));
                break;
            default:
                // assume any other identifier is a break out of the N1 loop
                // and let the other parser deal with it
                segmentIterator.previous();
                keepLooping = false;
                break;
            }
        }
        return n1;
    }

    private N1PartyIdentificationParser() {
        // you can't make me
    }
}
