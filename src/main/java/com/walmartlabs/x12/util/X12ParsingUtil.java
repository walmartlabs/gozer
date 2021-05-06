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

package com.walmartlabs.x12.util;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;

import java.util.List;

public final class X12ParsingUtil {
    
    /**
     * return the numeric part of a version number
     *
     * @param versionValue
     * @return
     */
    public static Integer parseVersion(String versionValue) {
        if (versionValue != null && !versionValue.isEmpty()) {
            try {
                String versionNum = X12ParsingUtil.remove0LeftPadding(versionValue).replace("UCS", "");
                return Integer.valueOf(versionNum);
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            return null;
        }
    }
    
    /**
     * builds an {@link X12ParserException} w/ consistent message when 
     * an unexpected segment is encountered
     * 
     * the caller of the method must throw this exception 
     * if that is what is desired
     * 
     * @param expectedSegmentId
     * @param actualSegmentId
     * @return the X12ParserException
     */
    public static X12ParserException handleUnexpectedSegment(String expectedSegmentId, String actualSegmentId) {
        return new X12ParserException(X12ParsingUtil.generateUnexpectedSegmentDetail(expectedSegmentId, actualSegmentId));
    }
    
    /**
     * builds an {@link X12ParserException} w/ consistent message when 
     * an unexpected segment is encountered
     * 
     * the caller of the method must throw this exception 
     * if that is what is desired
     * 
     * @param expectedSegmentId
     * @param actualSegmentId
     * @return the X12ErrorDetail
     */
    public static X12ErrorDetail generateUnexpectedSegmentDetail(String expectedSegmentId, String actualSegmentId) {
        StringBuilder sb = new StringBuilder("expected ");
        sb.append(expectedSegmentId);
        sb.append(" segment but found ");
        sb.append(actualSegmentId);
        return new X12ErrorDetail(actualSegmentId, null, sb.toString());
    }

    /**
     * given a set of segment lines it will examine the first 
     * and last segments and evaluate whether they match 
     * the header and trailer values passed into the method
     * 
     * @return true if envelope matches otherwise false
     */
    public static boolean isValidEnvelope(List<X12Segment> segmentList, String headerIdentifier, String trailerIdentifier) {
        boolean isValidEnvelope = false;
        
        if (segmentList != null && headerIdentifier != null && trailerIdentifier != null) {
            int segmentCount = segmentList.size();
            int lastSegmentIndex = segmentCount - 1;
            if (segmentCount > 1) {
                // need at least 2 lines to have valid envelope
                X12Segment headerSegment = segmentList.get(0);
                X12Segment trailerSegment = segmentList.get(lastSegmentIndex);
                if (headerIdentifier.equals(headerSegment.getIdentifier())
                    && trailerIdentifier.equals(trailerSegment.getIdentifier())) {
                    isValidEnvelope = true;
                }
            }
        }
        
        return isValidEnvelope;
    }
    
    /**
     * The segment list should wrapped in valid transaction envelope (ST/SE) 
     * with the transaction type (ST01) matching the provided type
     * @param segmentList
     * @param transactionType
     * @return true if type matches otherwise false
     */
    public static boolean verifyTransactionSetType(List<X12Segment> segmentList, String transactionType) {
        boolean isTransactionType = false;
        
        if (segmentList != null && !segmentList.isEmpty() && transactionType != null) {
            
            if (X12ParsingUtil.isValidEnvelope(segmentList, 
                X12TransactionSet.TRANSACTION_SET_HEADER,
                X12TransactionSet.TRANSACTION_SET_TRAILER)) {
                
                X12Segment firstSegment = segmentList.get(0);
                if (firstSegment != null) {
                    String segmentType = firstSegment.getElement(1);
                    if (transactionType.equals(segmentType)) {
                        isTransactionType = true;
                    }
                }
            }
        }
        
        return isTransactionType;
    }
    
    private static String remove0LeftPadding(String value) {
        return value.replaceFirst("^0+(?!$)", "");
    }
    
    private X12ParsingUtil() {
        // you can't make me
    }
}
