package com.walmartlabs.x12.util.loop;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.util.X12ParsingUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class X12LoopUtil {

    /**
     * check the segment for the start of HL 
     * @param segment
     * @return true if HL otherwise false
     */
    public static boolean isHierarchalLoopStart(X12Segment segment) {
        return segment != null && X12Loop.HIERARCHY_LOOP_ID.equals(segment.getIdentifier());
    }
    
    /**
     * generic method that takes a given set of segment lines and breaks
     * them up into separate hierarchical loops using the HL as the break since
     * there is no terminating segment for the loop - only the start of the next
     * loop
     * 
     * this method will only work when the first segment is an HL and when this set
     * of segments has already been extracted from the ST/SE and the parts of the
     * header and trailer of the transaction set
     * 
     * the results of processing the HL loops will be returned in {@link X12LoopHolder}
     * this holder will have a list of {@link X12Loop} as well as a 
     * list of any errors {@link X12ErrorDetail} that are found 
     * while trying to create the loop hierarchy
     */
    public static X12LoopHolder organizeHierarchicalLoops(List<X12Segment> segmentList) {
        X12LoopHolder loopHolder = new X12LoopHolder();
        
        if (segmentList != null && !segmentList.isEmpty()) {
            X12Segment firstSegment = segmentList.get(0);
            // the segment list starts with HL so we can 
            // attempt to handle the looping that was provided
            if (X12LoopUtil.isHierarchalLoopStart(firstSegment)) {
                loopHolder = X12LoopUtil.processLoops(segmentList);
            } else {
                // unexpected segment
                // should have been the first HL loop
                String actualSegment = (firstSegment != null ? firstSegment.getIdentifier() : "");
                loopHolder.addX12ErrorDetail(X12ParsingUtil.generateUnexpectedSegmentDetail("HL", actualSegment));
            }
        }
        
        return loopHolder;
    }
    
    /**
     * handle the loops and build nested structure as defined by the segment lines
     * 
     * @param segmentList
     * @return list of loops
     * 
     * @throws an {@link X12ParserException} if id is reused an HL segment
     */
    private static X12LoopHolder processLoops(List<X12Segment> segmentList) {
        X12LoopHolder loopHolder = new X12LoopHolder();
        
        List<X12Loop> loops = new ArrayList<>();
        loopHolder.setLoops(loops);
        
        String currLoopId = null;
        Map<String, X12Loop> loopMap = new HashMap<>();

        for (X12Segment x12Segment : segmentList) {
            if (X12LoopUtil.isHierarchalLoopStart(x12Segment)) {
                X12Loop loop = X12LoopUtil.buildHierarchalLoop(x12Segment);

                // when the HL has no parent
                // we will add it to the top level
                if (loop.getParentHierarchicalId() == null
                    || loop.getParentHierarchicalId().trim().isEmpty()) {

                    loops.add(loop);
                }

                // add the loop to the map
                // to allow parent/child associations
                // to be found quickly
                String loopId = loop.getHierarchicalId();
                if (loopMap.containsKey(loopId)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("HL segment with id (")
                        .append(loopId)
                        .append(") already exists");
                    loopHolder.addX12ErrorDetail(
                        new X12ErrorDetail(X12Loop.HIERARCHY_LOOP_ID, loopId, sb.toString()));
                } else {
                    loopMap.put(loop.getHierarchicalId(), loop);
                }

                // update the current loop
                currLoopId = loopId;

                X12LoopUtil.handleParentLoop(loop, loopMap, loopHolder);
            } else {
                // still in existing loop
                // so this segment belongs to
                // the current loop
                X12Loop currentLoop = loopMap.get(currLoopId);
                currentLoop.addSegment(x12Segment);
            }
        }
        
        return loopHolder;
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
    private static void handleParentLoop(X12Loop loop, Map<String, X12Loop> loopMap, X12LoopHolder loopHolder) {
        String parentLoopId = loop.getParentHierarchicalId();
        if (parentLoopId != null && !parentLoopId.isEmpty()) {
            X12Loop parentLoop = loopMap.get(parentLoopId);
            if (parentLoop != null) {
                parentLoop.addLoop(loop);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("HL segment (").append(loop.getHierarchicalId()).append(")");
                sb.append(" is missing parent (").append(loop.getParentHierarchicalId()).append(")");
                loopHolder.addX12ErrorDetail(
                    new X12ErrorDetail(X12Loop.HIERARCHY_LOOP_ID, loop.getHierarchicalId(), sb.toString()));
            }
        }
    }
    
    private X12LoopUtil() {
        // you can't make me
    }
}
