package com.walmartlabs.x12.standard.txset.generic;

import com.walmartlabs.x12.SegmentIterator;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.standard.txset.AbstractTransactionSetParserChainable;
import com.walmartlabs.x12.util.X12ParsingUtil;
import com.walmartlabs.x12.util.loop.X12LoopHolder;
import com.walmartlabs.x12.util.loop.X12LoopUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * this transaction set parser implementation will parse 
 * any transaction set into a {@link GenericTransactionSet} 
 * which will hold the basic X12 structures
 * like the {@link X12Segment} and the {@link X12Loop}
 * 
 * it will NOT be able to parse these basic X12 parts
 * into more specific object types. for that a specific
 * parser for a particular transaction set is required
 *
 * This class can serve as a basic reference.
 */
public class GenericTransactionSetParser extends AbstractTransactionSetParserChainable {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericTransactionSetParser.class);

    /**
     * generic parser will handle any transaction set
     */
    @Override
    protected boolean handlesTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group) {
        return true;
    }

    /**
     * it is assumed that this method is only called after getting true as a
     * response from {@link handlesTransactionSet}
     */
    @Override
    protected X12TransactionSet doParse(List<X12Segment> transactionSegments, X12Group x12Group) {
        GenericTransactionSet genericTxSet = null;

        if (!CollectionUtils.isEmpty(transactionSegments)) {
            genericTxSet = new GenericTransactionSet();
            this.doParsing(transactionSegments, genericTxSet);
        }

        return genericTxSet;
    }
    
    /**
     * the first segment in the list of {@link X12Segment} should be an ST
     * the last segment in the list of {@link X12Segment} should be an SE
     */
    protected void doParsing(List<X12Segment> transactionSegments, GenericTransactionSet genericTx) {
        SegmentIterator segments = new SegmentIterator(transactionSegments);
        X12Segment currentSegment = null;

        //
        // ST
        //
        if (segments.hasNext()) {
            currentSegment = segments.next();
            this.parseTransactionSetHeader(currentSegment, genericTx);
        }

        //
        // Beginning Segment line for the Transaction Set
        //
        if (segments.hasNext()) {
            currentSegment = segments.next();
            if (this.isLoopSegmentOrOptionalSegmentOrEndingSegment(currentSegment)) {
                // unexpected segment HL, CTT, AMT or SE
                throw X12ParsingUtil.handleUnexpectedSegment("Beginning", currentSegment.getIdentifier());
            } else {
                // assuming that this
                // segment is the beginning segment
                genericTx.setBeginningSegment(currentSegment);
            }
        }
        
        //
        // all segments that appear before the first HL loop
        // 
        this.parseSegmentsBeforeHierarchyLoops(segments, genericTx);
        
        //
        // loops
        //
        this.handleLooping(segments, genericTx);
        
        //
        // CTT or AMT
        //
        this.handleOptionalSegments(segments, genericTx);
        
        //
        // SE
        //
        if (segments.hasNext()) {
            currentSegment = segments.next();
            this.parseTransactionSetTrailer(currentSegment, genericTx);
        } else {
            throw X12ParsingUtil.handleUnexpectedSegment("SE", "nothing");
        }
    }
    
    /**
     * parse the segments
     * that appear between the beginning segment
     * and the first loop or the CTT or SE
     * 
     * it will also move the segment iterator to segment 
     * that caused the looping to stop
     * 
     * @param segments
     * @param txSet
     * @throws X12ParserException if no HL loop is found
     */
    protected void parseSegmentsBeforeHierarchyLoops(SegmentIterator segments, GenericTransactionSet genericTx) {
        
        while (segments.hasNext()) {
            X12Segment currentSegment = segments.next();
            if (this.isLoopSegmentOrOptionalSegmentOrEndingSegment(currentSegment)) {
                // we should back up so
                // the parser starts w/ this segment
                segments.previous();
                break;
            } else {
                // add segment to the transaction set
                LOGGER.debug(currentSegment.getIdentifier());
                genericTx.addX12Segment(currentSegment);
            }
        }
    }
    
    /**
     * checks for an HL loop, CTT or AMT or SE
     * @return
     */
    private boolean isLoopSegmentOrOptionalSegmentOrEndingSegment(X12Segment segment) {
        return X12LoopUtil.isHierarchicalLoopStart(segment) 
            || X12TransactionSet.TRANSACTION_ITEM_TOTAL.equals(segment.getIdentifier())
            || X12TransactionSet.TRANSACTION_AMOUNT_TOTAL.equals(segment.getIdentifier())
            || X12TransactionSet.TRANSACTION_SET_TRAILER.equals(segment.getIdentifier());
    }
    
    /**
     * checks for CTT or AMT
     * @return
     */
    private void handleOptionalSegments(SegmentIterator segments, GenericTransactionSet genericTx) {
        while (segments.hasNext()) {
            X12Segment currentSegment = segments.next();
            String segmentId = currentSegment.getIdentifier();
            if (X12TransactionSet.TRANSACTION_ITEM_TOTAL.equals(segmentId)) {
                this.parseTransactionTotals(currentSegment, genericTx);
            } else if (X12TransactionSet.TRANSACTION_AMOUNT_TOTAL.equals(segmentId)) {
                // TODO: AMT not supported yet
            } else {
                // was not CTT or AMT
                // hopefully it is SE
                segments.previous();
                break;
            }
                
        }
    }
    
    protected void handleLooping(SegmentIterator segments, GenericTransactionSet genericTx) {
        
        if (segments.hasNext()) {
            X12Segment currentSegment = segments.next();
            if (X12LoopUtil.isHierarchicalLoopStart(currentSegment)) {
                segments.previous();
                int firstLoopSegmentIndex = segments.currentIndex();
                int indexToSegmentAfterHierarchicalLoops = this.findIndexForSegmentAfterHierarchicalLoops(segments);
                List<X12Segment> loopSegments = segments.subList(firstLoopSegmentIndex, indexToSegmentAfterHierarchicalLoops);
                
                // manage the loops
                // assigning the parents and children accordingly
                X12LoopHolder loopHolder = X12LoopUtil.organizeHierarchicalLoops(loopSegments);

                // add loops
                List<X12Loop> loops = loopHolder.getLoops();
                genericTx.setLoops(loops);
                
                // add loop errors to tx (if any)
                List<X12ErrorDetail> loopErrors = loopHolder.getLoopErrors();
                genericTx.setLoopingValid(CollectionUtils.isEmpty(loopErrors));
                genericTx.setLoopingErrors(loopHolder.getLoopErrors());
                
                // we processed all of the loops 
                // so now set the iteraror up
                // so that the next segment after 
                // the last loop is next
                segments.reset(indexToSegmentAfterHierarchicalLoops);
            } else {
                // doesn't start w/ HL
                // we should back it up 
                // and let the parser deal w/ this
                // segment
                segments.previous();
            }
        }
    }
    
    /**
     * expects the current segment to be the first HL loop occurring 
     * in the transaction set
     */
    private int findIndexForSegmentAfterHierarchicalLoops(SegmentIterator segments) {
        int firstLoopSegmentIndex = segments.currentIndex();
        int indexToSegmentAfterHierarchicalLoops = -1;
        while (segments.hasNext()) {
            X12Segment segment = segments.next();
            if (X12TransactionSet.TRANSACTION_ITEM_TOTAL.equals(segment.getIdentifier())
                || X12TransactionSet.TRANSACTION_AMOUNT_TOTAL.equals(segment.getIdentifier())
                || X12TransactionSet.TRANSACTION_SET_TRAILER.equals(segment.getIdentifier())) {
                // CTT segment or AMT segment or SE segment
                indexToSegmentAfterHierarchicalLoops = segments.currentIndex() - 1;
                break;
            }
        }
        if (indexToSegmentAfterHierarchicalLoops == -1) {
            indexToSegmentAfterHierarchicalLoops = segments.currentIndex() - 1;
        }
        
        segments.reset(firstLoopSegmentIndex);
        return indexToSegmentAfterHierarchicalLoops;
    }

}
