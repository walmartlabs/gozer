package com.walmartlabs.x12.rule;

import com.walmartlabs.x12.SegmentIterator;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.StandardX12Parser;
import com.walmartlabs.x12.util.ConversionUtil;

import java.util.List;
import java.util.Optional;

public class TrailerSegmentCountX12Rule implements X12Rule {

    /**
     * check each trailer record and verify 
     * that the total segments match what 
     * is reported in the trailer count
     * 
     * @param segmentList
     */
    @Override
    public void verify(List<X12Segment> segmentList) {
        
        SegmentIterator segments = new SegmentIterator(segmentList);

        int groupHeaders = 0;
        int groupTrailers = 0;
        int transactionCount = 0;
        String currentGroupControlNumber = null;
        int groupCountOnIeaTrailer = this.findGroupCountOnIeaTrailer(segmentList);
        
        while (segments.hasNext()) {
            X12Segment currentSegment = segments.next();
            
            if (StandardX12Parser.GROUP_HEADER_ID.equals(currentSegment.getIdentifier())) {
                groupHeaders++;
                currentGroupControlNumber = currentSegment.getElement(6);
            }
            
            if (X12TransactionSet.TRANSACTION_SET_HEADER.equals(currentSegment.getIdentifier())) {
                transactionCount++;
            }
            
            if (StandardX12Parser.GROUP_TRAILER_ID.equals(currentSegment.getIdentifier())) {
                groupTrailers++;
                this.verifyTransactionsOnGroupTrailer(currentGroupControlNumber, transactionCount, currentSegment);
                
                // reset transaction numbers
                transactionCount = 0;
                currentGroupControlNumber = null;
            }
        }
        
        this.verifyInterchangeControlTrailer(groupHeaders, groupTrailers, groupCountOnIeaTrailer);
    }
    
    /**
     * find IEA segment and get number of groups
     */
    private int findGroupCountOnIeaTrailer(List<X12Segment> segmentList) {
        int groupCountOnIeaTrailer = 0;
        int segmentCount = segmentList.size();
        
        X12Segment ieaTrailer = segmentList.get(segmentCount - 1);
        if (ieaTrailer != null && StandardX12Parser.ENVELOPE_TRAILER_ID.equals(ieaTrailer.getIdentifier())) {
            groupCountOnIeaTrailer = Optional
                .ofNullable(ConversionUtil.convertStringToInteger(ieaTrailer.getElement(1)))
                .orElse(-1);
        } else {
            // error
            throw new X12ParserException(
                new X12ErrorDetail(StandardX12Parser.ENVELOPE_TRAILER_ID, "", "missing IEA segment"));
        }
        
        return groupCountOnIeaTrailer;
    }
    
    /**
     * IEA02 should match the total number of groups
     * that are in the EDI file
     */
    private void verifyInterchangeControlTrailer(int groupHeaders, int groupTrailers, int groupCountOnIeaTrailer) {
        if (groupHeaders == groupTrailers && groupHeaders == groupCountOnIeaTrailer) {
            // ok
        } else {
            // error
            throw new X12ParserException(
                new X12ErrorDetail(StandardX12Parser.ENVELOPE_TRAILER_ID, "IEA02", "incorrect number of groups on IEA trailer"));
        }
    }
    
    private void verifyTransactionsOnGroupTrailer(String currentGroupControlNumber, int transactionCount, X12Segment currentSegment) {
        if (currentGroupControlNumber != null && currentGroupControlNumber.equals(currentSegment.getElement(2))) {
            int transactionCountOnGroupTrailer = Optional
                .ofNullable(ConversionUtil.convertStringToInteger(currentSegment.getElement(1)))
                .orElse(-1);

            if (transactionCountOnGroupTrailer == transactionCount) {
                // ok
            } else {
                // error
                throw new X12ParserException(
                    new X12ErrorDetail(StandardX12Parser.GROUP_TRAILER_ID, "GE02", "incorrect number of transactions on group"));
            }
        } else {
            // error
            throw new X12ParserException(
                new X12ErrorDetail(StandardX12Parser.GROUP_TRAILER_ID, "GE02", "groups seem to be misaligned"));
        }
    }
}
