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
package com.walmartlabs.x12.asn856;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.standard.AbstractTransactionSetParserChainable;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.util.ConversionUtil;
import com.walmartlabs.x12.util.X12ParsingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ASN 856 is the Advance Shipping Notice Used to communicate the contents of a
 * shipment prior to arriving at the facility where the contents will be
 * delivered.
 *
 */
public class DefaultAsn856TransactionSetParser extends AbstractTransactionSetParserChainable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAsn856TransactionSetParser.class);

    public static final String ASN_TRANSACTION_TYPE = "856";
    public static final String ASN_TRANSACTION_HEADER = "BSN";
    public static final String ASN_TRANSACTION_TOTALS = "CTT";

    @Override
    protected boolean handlesTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group) {
        // the first segment should be an ST with the 856 transaction type code
        return X12ParsingUtil.verifyTransactionSetType(transactionSegments, ASN_TRANSACTION_TYPE);
    }

    /**
     * it is assumed that this method is only called after getting true as a
     * response from {@link handlesTransactionSet}
     */
    @Override
    protected X12TransactionSet doParse(List<X12Segment> transactionSegments, X12Group x12Group) {
        AsnTransactionSet asnTx = null;

        if (transactionSegments != null) {
            asnTx = new AsnTransactionSet();
            
            this.doParsing(transactionSegments, asnTx);
        }

        return asnTx;
    }

    protected void doParsing(List<X12Segment> transactionSegments, AsnTransactionSet asnTx) {
        int segmentCount = transactionSegments.size();

        int segementAfterHierarchicalLoops = this.findSegmentAfterHierarchicalLoops(transactionSegments);
        List<X12Segment> loopSegments = transactionSegments.subList(2, segementAfterHierarchicalLoops);
        List<X12Loop> loops = X12ParsingUtil.findHierarchicalLoops(loopSegments);
        
        //
        // ST
        //
        X12Segment currentSegment = transactionSegments.get(0);
        this.parseTransactionSetHeader(currentSegment, asnTx);

        //
        // BSN
        //
        currentSegment = transactionSegments.get(1);
        this.parseBeginningSegment(currentSegment, asnTx);
        
        //
        // Hierarchical Loops
        // 
        // TODO: add loop parsing
        
        //
        // CTT (optional)
        //
        currentSegment = transactionSegments.get(segementAfterHierarchicalLoops);
        if (ASN_TRANSACTION_TOTALS.equals(currentSegment.getSegmentIdentifier())) {
            this.parseTransactionTotals(currentSegment, asnTx);
            currentSegment = transactionSegments.get(segmentCount - 1);
        }
        
        //
        // SE
        //
        this.parseTransactionSetTrailer(currentSegment, asnTx);
    }

    private int findSegmentAfterHierarchicalLoops(List<X12Segment> transactionSegments) {
        int segmentCount = transactionSegments.size();
        int secondToLastSegmentIndex = segmentCount - 2;
        X12Segment segment = transactionSegments.get(secondToLastSegmentIndex);
        // 2nd to last line is CTT (optional) otherwise it is part of HL
        if (ASN_TRANSACTION_TOTALS.equals(segment.getSegmentIdentifier())) {
            // CTT segment
            return secondToLastSegmentIndex;
        } else {
            // SE segment
            return segmentCount - 1;
        }
    }
    
    /**
     * parse the ST segment
     * @param segment
     * @param asnTx
     */
    private void parseTransactionSetHeader(X12Segment segment, AsnTransactionSet asnTx) {
        LOGGER.debug(segment.getSegmentIdentifier());

        String segmentIdentifier = segment.getSegmentIdentifier();
        if (X12TransactionSet.TRANSACTION_SET_HEADER.equals(segmentIdentifier)) {
            asnTx.setTransactionSetIdentifierCode(segment.getSegmentElement(1));
            asnTx.setHeaderControlNumber(segment.getSegmentElement(2));
        } else {
            throw X12ParsingUtil.handleUnexpectedSegment(X12TransactionSet.TRANSACTION_SET_HEADER, segmentIdentifier);
        }
    }
    
    /**
     * parse the SE segment
     * @param segment
     * @param asnTx
     */
    private void parseTransactionSetTrailer(X12Segment segment, AsnTransactionSet asnTx) {
        LOGGER.debug(segment.getSegmentIdentifier());

        String segmentIdentifier = segment.getSegmentIdentifier();
        if (X12TransactionSet.TRANSACTION_SET_TRAILER.equals(segmentIdentifier)) {
            asnTx.setExpectedNumberOfSegments(ConversionUtil.convertStringToInteger(segment.getSegmentElement(1)));
            asnTx.setTrailerControlNumber(segment.getSegmentElement(2));
        } else {
            throw X12ParsingUtil.handleUnexpectedSegment(X12TransactionSet.TRANSACTION_SET_TRAILER, segmentIdentifier);
        }
    }

    /**
     * parse the BSN segment
     * @param segment
     * @param asnTx
     */
    private void parseBeginningSegment(X12Segment segment, AsnTransactionSet asnTx) {
        LOGGER.debug(segment.getSegmentIdentifier());
        
        String segmentIdentifier = segment.getSegmentIdentifier();
        if (ASN_TRANSACTION_HEADER.equals(segmentIdentifier)) {
            asnTx.setPurposeCode(segment.getSegmentElement(1));
            asnTx.setShipmentIdentification(segment.getSegmentElement(2));
            asnTx.setShipmentDate(segment.getSegmentElement(3));
            asnTx.setShipmentTime(segment.getSegmentElement(4));
            asnTx.setHierarchicalStructureCode(segment.getSegmentElement(5));
        } else {
            throw X12ParsingUtil.handleUnexpectedSegment(ASN_TRANSACTION_HEADER, segmentIdentifier);
        }
    }

    /**
     * parse the CTT segment
     * @param segment
     * @param asnTx
     */
    private void parseTransactionTotals(X12Segment segment, AsnTransactionSet asnTx) {
        LOGGER.debug(segment.getSegmentIdentifier());

        String segmentIdentifier = segment.getSegmentIdentifier();
        if (ASN_TRANSACTION_TOTALS.equals(segmentIdentifier)) {
            asnTx.setTransactionLineItems(ConversionUtil.convertStringToInteger(segment.getSegmentElement(1)));
        } else {
            throw X12ParsingUtil.handleUnexpectedSegment(ASN_TRANSACTION_TOTALS, segmentIdentifier);
        }
    }
}
