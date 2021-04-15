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
package com.walmartlabs.x12;

import com.walmartlabs.x12.dex.dx894.DefaultDex894Parser;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.X12Loop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class X12ParsingUtil {

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
        StringBuilder sb = new StringBuilder("expected ");
        sb.append(expectedSegmentId);
        sb.append(" segment but found ");
        sb.append(actualSegmentId);
        return new X12ParserException(new X12ErrorDetail(actualSegmentId, null, sb.toString()));
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
                // TODO: need to remove coupling to DefaultDex894Parser
                throw new X12ParserException(new X12ErrorDetail(DefaultDex894Parser.DEX_HEADER_ID, "DXS03", "Invalid version format"));
            }
        } else {
            return null;
        }
    }
    
    /**
     * generic method that takes a given a set of segment lines and it will break
     * them up into separate hierarchical loops using the HL as the break since
     * there is no terminating segment for the loop - only the start of the next
     * loop
     * 
     * this method will only work when the first segment is an HL and when this set
     * of segments has already been extracted from the ST/SE and the parts of the
     * header and trailer of the transaction set
     * 
     * @return list of {@link X12Loop} or empty list if there is a problem
     * 
     * @throws X12ParserException if the first segment is not an HL or if the parent
     *                            that an HL loop has is not found
     */
    public static List<X12Loop> findHierarchicalLoops(List<X12Segment> segmentList) {
        List<X12Loop> loops = Collections.emptyList();
        
        if (segmentList != null && !segmentList.isEmpty()) {
            X12Segment firstSegment = segmentList.get(0);
            // the segment list starts with HL so we can 
            // attempt to handle the looping that was provided
            if (isHierarchalLoopStart(firstSegment)) {
                loops = processLoops(segmentList);
            } else {
                String actualSegment = (firstSegment != null ? firstSegment.getIdentifier() : "");
                throw handleUnexpectedSegment("HL", actualSegment);
            }
        }
        
        return loops;
    }
    
    /**
     * handle the loops and build nested structure as defined by the segment lines
     * 
     * @param segmentList
     * @return list of loops
     * 
     * @throws an {@link X12ParserException} if id is reused an HL segment
     */
    private static List<X12Loop> processLoops(List<X12Segment> segmentList) {
        List<X12Loop> loops = new ArrayList<>();
        
        String currLoopId = null;
        Map<String, X12Loop> loopMap = new HashMap<>();

        for (X12Segment x12Segment : segmentList) {
            if (isHierarchalLoopStart(x12Segment)) {
                X12Loop loop = buildHierarchalLoop(x12Segment);

                // when the HL has no parent
                // we will add it to the top level
                if (loop.getParentHierarchicalId() == null
                    || loop.getParentHierarchicalId().trim().isEmpty()) {

                    loops.add(loop);
                }

                // add the loop to the map
                // to allow parent/child associations
                // to be found quickly
                if (loopMap.containsKey(loop.getHierarchicalId())) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("HL segment with id (").append(loop.getHierarchicalId())
                        .append(") already exists");
                    throw new X12ParserException(sb.toString());
                } else {
                    loopMap.put(loop.getHierarchicalId(), loop);
                }

                // update the current loop
                currLoopId = loop.getHierarchicalId();

                handleParentLoop(loop, loopMap);
            } else {
                // still in existing loop
                // so this segment belongs to
                // the current loop
                X12Loop currentLoop = loopMap.get(currLoopId);
                currentLoop.addSegment(x12Segment);
            }
        }
        
        return loops;
    }
    
    /**
     * check the segment for the start of HL 
     * @param segment
     * @return true if HL otherwise false
     */
    public static boolean isHierarchalLoopStart(X12Segment segment) {
        return segment != null && X12Loop.HIERARCHY_LOOP_ID.equals(segment.getIdentifier());
    }
    
    private static X12Loop buildHierarchalLoop(X12Segment x12Segment) {
        // starting new loop
        String loopId = x12Segment.getElement(1);
        String parentLoopId = x12Segment.getElement(2);

        X12Loop loop = new X12Loop();
        loop.setHierarchicalId(loopId);
        loop.setParentHierarchicalId(parentLoopId);
        loop.setCode(x12Segment.getElement(3));
        
        return loop;
    }
    
    /**
     * given a loop, look for the parent loop
     * @param loop
     * @param loopMap
     * 
     * @throws X12ParserException if the parent loop is missing
     */
    private static void handleParentLoop(X12Loop loop, Map<String, X12Loop> loopMap) {
        String parentLoopId = loop.getParentHierarchicalId();
        if (parentLoopId != null && !parentLoopId.isEmpty()) {
            X12Loop parentLoop = loopMap.get(parentLoopId);
            if (parentLoop != null) {
                parentLoop.addLoop(loop);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("HL segment (").append(loop.getHierarchicalId()).append(")");
                sb.append(" is missing parent (").append(loop.getParentHierarchicalId()).append(")");
                throw new X12ParserException(sb.toString());
            }
        }
    }
    
    private static String remove0LeftPadding(String value) {
        return value.replaceFirst("^0+(?!$)", "");
    }
    
    private X12ParsingUtil() {
    }
}
