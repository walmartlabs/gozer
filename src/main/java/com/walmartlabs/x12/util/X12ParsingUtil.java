package com.walmartlabs.x12.util;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.dex.dx894.DefaultDex894Parser;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.X12Loop;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class X12ParsingUtil {

    
    /**
     * given a set of segment lines it will examine the first 
     * and last segments and evaluate whether they match 
     * the header and trailer 
     * 
     * @return true if envelope matches otherwise false
     */
    public static boolean isValidEnvelope(List<X12Segment> segmentList, String headerIdentifier, String trailerIdentifier) {
        boolean isValidEnvelope = false;
        
        if (segmentList != null && headerIdentifier!= null && trailerIdentifier != null) {
            int segmentCount = segmentList.size();
            int lastSegmentIndex = segmentCount - 1;
            if (segmentCount > 1) {
                // need at least 2 lines to have valid envelope
                X12Segment headerSegment = segmentList.get(0);
                X12Segment trailerSegment = segmentList.get(lastSegmentIndex);
                if (headerIdentifier.equals(headerSegment.getSegmentIdentifier())
                    && trailerIdentifier.equals(trailerSegment.getSegmentIdentifier())) {
                    isValidEnvelope = true;
                }
            }
        }
        
        return isValidEnvelope;
    }
    
    /**
     * The first segment in the list should be an ST segment 
     * with the transaction type (ST01) matching the provided type
     * @param segmentList
     * @param transactionType
     * @return true if type matches otherwise false
     */
    public static boolean verifyTransactionSetType(List<X12Segment> segmentList, String transactionType) {
        boolean isTransactionType = false;
        
        if (segmentList != null && !segmentList.isEmpty() && transactionType != null) {
            X12Segment firstSegment = segmentList.get(0);
            if (firstSegment != null) {
                String segmentId = firstSegment.getSegmentIdentifier();
                String segmentType = firstSegment.getSegmentElement(1);
                if (X12TransactionSet.TRANSACTION_SET_HEADER.equals(segmentId)
                    && transactionType.equals(segmentType)) {
                    isTransactionType = true;
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
     * given a set of segment lines it will break them up into
     * separate hierarchical loops using the HL as the break since 
     * there is no terminating segment for the loop - only the 
     * start of the next loop
     * 
     * this method will only work when the first segment is an HL
     * and when this set of segments has already been extracted from 
     * the ST/SE and the parts of the header and trailer of the transaction set 
     * 
     * @return list of {@link X12Loop} or empty list if there is a problem
     * 
     * @throws X12ParserException if the first segment is not an HL
     */
    public static List<X12Loop> findHierarchicalLoops(List<X12Segment> segmentList) {
        List<X12Loop> loops = Collections.emptyList();
        
        if (segmentList != null && !segmentList.isEmpty()) {
            X12Segment firstSegment = segmentList.get(0);
            // the segment list starts with HL so we can 
            // attempt to handle the looping that was provided
            if (isHierarchalLoopStart(firstSegment)) {
                String currLoopId = null;
                Map<String, X12Loop> loopMap = new HashMap<>();
                
                for (X12Segment x12Segment : segmentList) {
                    if (isHierarchalLoopStart(x12Segment)) {
                        // starting new loop
                        String loopId = x12Segment.getSegmentElement(1);
                        
                        X12Loop loop = new X12Loop();
                        loop.setHierarchicalId(loopId);
                        loop.setParentHierarchicalId(x12Segment.getSegmentElement(2));
                        loop.setCode(x12Segment.getSegmentElement(3));
                        
                        loopMap.putIfAbsent(loopId, loop);
                        currLoopId = loopId;
                        
                        handleParentLoop(loop, loopMap);
                    } else {
                        // still in existing loop
                        // so this segment belongs to 
                        // the current loop 
                        X12Loop currentLoop = loopMap.get(currLoopId);
                        currentLoop.addSegment(x12Segment);
                    }
                }
            } else {
                // TODO: the first line is not HL
            }
        }
        
        return loops;
    }
    
    /**
     * check the segment for the start of HL 
     * @param segment
     * @return true if HL otherwise false
     */
    private static boolean isHierarchalLoopStart(X12Segment segment) {
        return segment != null && "HL".equals(segment.getSegmentIdentifier());
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
                // TODO: parent is missing
            }
        }
    }
    
    private static String remove0LeftPadding(String value) {
        return value.replaceFirst("^0+(?!$)", "");
    }
    
    private X12ParsingUtil() {
    }
}
