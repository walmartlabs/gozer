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

package com.walmartlabs.x12.rule;

import com.walmartlabs.x12.SegmentIterator;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * turns out that different documents
 * have the document number and date in 
 * different spots on the segment line
 * 
 * for example the document number
 * on the ASN can be found at BSN02 
 * but the document number 
 * on the PO can be found at BEG03
 * 
 * therefore this rule can be configured
 * to look for a specific identifier
 * and will take two locations
 * one for the document number and
 * the other for the document date
 */
public class UniqueDocumentX12Rule implements X12Rule {

    private final String segmentId;
    private final int documentNumberIndex;
    private final int documentDateIndex;
    
    public UniqueDocumentX12Rule(String segmentId, int documentNumberIndex, int documentDateIndex) {
        this.segmentId = segmentId;
        this.documentNumberIndex = documentNumberIndex;
        this.documentDateIndex = documentDateIndex;
    }
    
    @Override
    public void verify(List<X12Segment> segmentList) {
        Set<String> documentNumbers = new HashSet<>();

        SegmentIterator segments = new SegmentIterator(segmentList);
        while (segments.hasNext()) {
            X12Segment currentSegment = segments.next();
            if (segmentId.equals(currentSegment.getIdentifier())) {
                String docNumber = currentSegment.getElement(documentNumberIndex);
                String docDate = currentSegment.getElement(documentDateIndex);
                if (!documentNumbers.add(docNumber + "_" + docDate)) {
                    throw new X12ParserException(
                        new X12ErrorDetail(segmentId, segmentId + documentNumberIndex, "duplicate document numbers"));
                }
            }
        }
        
    }

}
