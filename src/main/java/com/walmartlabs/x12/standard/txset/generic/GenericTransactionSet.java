package com.walmartlabs.x12.standard.txset.generic;

import com.walmartlabs.x12.AbstractX12TransactionSet;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.standard.X12Loop;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * this transaction set implementation is a very generic
 * class that will allow parsing of the basic X12 structures
 * such as X12Segment and X12Loop. These structures will not
 * be parsed beyond this.
 *
 * It is the intention of this framework to have a specific
 * transaction set class and parser to support more specific
 * parsing of the transaction set
 *
 * This class can serve as a basic reference.
 *
 * Sample:
 *
 * ST*YYZ*0001 <---- transaction header
 *
 * YYZ*00*1234 <---- beginningSegment
 * DTM*000*20210408 <---- segments after beginning
 * REF*ZZ*2112
 *
 * HL*1**A     <----- first HL loop
 * REF*XX*1234
 *
 * HL*2*1*B    <----- child to first loop
 * REF*XX*4321
 *
 * CTT*10      <---- transaction totals
 * SE*10*0001  <---- transaction trailer
 *
 */
public class GenericTransactionSet extends AbstractX12TransactionSet {

    private X12Segment beginningSegment;

    // all segments that appear after the beginning segment
    // and before the first HL loop (if any)
    private List<X12Segment> segmentsBeforeLoops;

    // the loops
    private List<X12Loop> loops;

    // looping issues are captured
    private List<X12ErrorDetail> loopingErrors;

    /**
     * helper method to add segment to list
     * @param segment
     */
    public void addX12Segment(X12Segment segment) {
        if (CollectionUtils.isEmpty(segmentsBeforeLoops)) {
            segmentsBeforeLoops = new ArrayList<>();
        }
        segmentsBeforeLoops.add(segment);
    }

    /**
     * helper method to add loop to list
     * @param loop
     */
    public void addX12Loop(X12Loop loop) {
        if (CollectionUtils.isEmpty(loops)) {
            loops = new ArrayList<>();
        }
        loops.add(loop);
    }
    
    /**
     * helper method to add X12ErrorDetail for looping errors
     * @param errorDetail
     */
    public void addX12ErrorDetailForLoop(X12ErrorDetail errorDetail) {
        this.addX12ErrorDetailForLoop(Collections.singletonList(errorDetail));
    }

    /**
     * helper method to add multiple X12ErrorDetail for looping errors
     * @param errorDetail
     */
    public void addX12ErrorDetailForLoop(List<X12ErrorDetail> errorDetails) {
        if (!CollectionUtils.isEmpty(errorDetails)) {
            if (CollectionUtils.isEmpty(loopingErrors)) {
                loopingErrors = new ArrayList<>();
            }
            loopingErrors.addAll(errorDetails);
        }
    }
    
    @Override
    public List<X12ErrorDetail> getLoopingErrors() {
        return loopingErrors;
    }

    public X12Segment getBeginningSegment() {
        return beginningSegment;
    }

    public void setBeginningSegment(X12Segment mainSegment) {
        this.beginningSegment = mainSegment;
    }

    public List<X12Segment> getSegmentsBeforeLoops() {
        return segmentsBeforeLoops;
    }

    public void setSegmentsBeforeLoops(List<X12Segment> segmentsBeforeLoops) {
        this.segmentsBeforeLoops = segmentsBeforeLoops;
    }

    public List<X12Loop> getLoops() {
        return loops;
    }

    public void setLoops(List<X12Loop> loops) {
        this.loops = loops;
    }

}
