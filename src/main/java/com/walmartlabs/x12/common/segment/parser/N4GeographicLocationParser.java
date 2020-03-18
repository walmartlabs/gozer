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
