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
