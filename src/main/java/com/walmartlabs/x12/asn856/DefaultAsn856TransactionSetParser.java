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

import com.walmartlabs.x12.X12ParsingUtil;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.common.segment.TD1CarrierDetails;
import com.walmartlabs.x12.common.segment.TD1CarrierDetailsParser;
import com.walmartlabs.x12.common.segment.TD5CarrierDetails;
import com.walmartlabs.x12.common.segment.TD5CarrierDetailsParser;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.standard.txset.AbstractTransactionSetParserChainable;
import com.walmartlabs.x12.util.ConversionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

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

        if (!CollectionUtils.isEmpty(transactionSegments)) {
            asnTx = new AsnTransactionSet();
            this.doParsing(transactionSegments, asnTx);
        }

        return asnTx;
    }

    protected void doParsing(List<X12Segment> transactionSegments, AsnTransactionSet asnTx) {
        int segmentCount = transactionSegments.size();
        
        //
        // ST
        //
        X12Segment currentSegment = transactionSegments.get(0);
        this.parseTransactionSetHeader(currentSegment, asnTx);

        //
        // BSN
        //
        currentSegment = transactionSegments.get(1);
        this.parseBeginningSegmentForShipNotice(currentSegment, asnTx);
        
        //
        // Hierarchical Loops
        // 
        int segementAfterHierarchicalLoops = this.findSegmentAfterHierarchicalLoops(transactionSegments);
        List<X12Segment> loopSegments = transactionSegments.subList(2, segementAfterHierarchicalLoops);
        List<X12Loop> loops = X12ParsingUtil.findHierarchicalLoops(loopSegments);
        this.doLoopParsing(loops, asnTx);
        
        //
        // CTT (optional)
        //
        currentSegment = transactionSegments.get(segementAfterHierarchicalLoops);
        if (ASN_TRANSACTION_TOTALS.equals(currentSegment.getIdentifier())) {
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
        if (ASN_TRANSACTION_TOTALS.equals(segment.getIdentifier())) {
            // CTT segment
            return secondToLastSegmentIndex;
        } else {
            // SE segment
            return segmentCount - 1;
        }
    }
    
    /**
     * parse the BSN segment
     * @param segment
     * @param asnTx
     */
    private void parseBeginningSegmentForShipNotice(X12Segment segment, AsnTransactionSet asnTx) {
        LOGGER.debug(segment.getIdentifier());
        
        String segmentIdentifier = segment.getIdentifier();
        if (ASN_TRANSACTION_HEADER.equals(segmentIdentifier)) {
            asnTx.setPurposeCode(segment.getElement(1));
            asnTx.setShipmentIdentification(segment.getElement(2));
            asnTx.setShipmentDate(segment.getElement(3));
            asnTx.setShipmentTime(segment.getElement(4));
            asnTx.setHierarchicalStructureCode(segment.getElement(5));
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
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (ASN_TRANSACTION_TOTALS.equals(segmentIdentifier)) {
            asnTx.setTransactionLineItems(ConversionUtil.convertStringToInteger(segment.getElement(1)));
        } else {
            throw X12ParsingUtil.handleUnexpectedSegment(ASN_TRANSACTION_TOTALS, segmentIdentifier);
        }
    }
    

    /**
     * currently enforcing only 1 top level HL in the transaction set
     * (ie) only one Shipment HL
     * 
     * @param loops
     * @param asnTx
     * @return
     */
    protected void doLoopParsing(List<X12Loop> loops, AsnTransactionSet asnTx) {
        if (!CollectionUtils.isEmpty(loops) && loops.size() == 1) {
            X12Loop firstLoop = loops.get(0);
            if (Shipment.isShipmentLoop(firstLoop)) {
                this.parseShipmentLoop(firstLoop, asnTx);
            } else {
                throw new X12ParserException(new X12ErrorDetail("HL", "HL03", "first HL is not a shipment"));
            }
        } else {
            throw new X12ParserException(new X12ErrorDetail("HL", "HL00", "expected one top level HL"));
        }
    }
    
    private void parseShipmentLoop(X12Loop shipmentLoop, AsnTransactionSet asnTx) {
        Shipment shipment = new Shipment();
        
        List<X12Segment> shipmentSegments = shipmentLoop.getSegments();
        if (!CollectionUtils.isEmpty(shipmentSegments)) {
            shipmentSegments.forEach(segment -> {
                switch (segment.getIdentifier()) {
                    case TD1CarrierDetails.CARRIER_DETAILS_IDENTIFIER:
                        shipment.setTd1(TD1CarrierDetailsParser.parse(segment));
                        break;
                    case TD5CarrierDetails.CARRIER_DETAILS_IDENTIFIER:
                        shipment.setTd5(TD5CarrierDetailsParser.parse(segment));
                        break;
                        // TODO: need to keep working on this and add tests
                    default:
                        // TODO: what do we do w/ an unidentified segment
                        break;
                }
                
            });
        }
    }
}
