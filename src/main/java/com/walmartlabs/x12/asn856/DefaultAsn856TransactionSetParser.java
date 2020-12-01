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

import com.walmartlabs.x12.SegmentIterator;
import com.walmartlabs.x12.X12ParsingUtil;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.asn856.segment.MANMarkNumber;
import com.walmartlabs.x12.asn856.segment.PALPalletType;
import com.walmartlabs.x12.asn856.segment.PO4ItemPhysicalDetail;
import com.walmartlabs.x12.asn856.segment.PRFPurchaseOrderReference;
import com.walmartlabs.x12.asn856.segment.SN1ItemDetail;
import com.walmartlabs.x12.asn856.segment.parser.MANMarkNumberParser;
import com.walmartlabs.x12.asn856.segment.parser.PRFPurchaseOrderReferenceParser;
import com.walmartlabs.x12.asn856.segment.parser.SN1ItemDetailParser;
import com.walmartlabs.x12.common.segment.DTMDateTimeReference;
import com.walmartlabs.x12.common.segment.LINItemIdentification;
import com.walmartlabs.x12.common.segment.N1PartyIdentification;
import com.walmartlabs.x12.common.segment.PIDProductIdentification;
import com.walmartlabs.x12.common.segment.PKGPackaging;
import com.walmartlabs.x12.common.segment.REFReferenceInformation;
import com.walmartlabs.x12.common.segment.TD1CarrierDetail;
import com.walmartlabs.x12.common.segment.TD3CarrierDetail;
import com.walmartlabs.x12.common.segment.TD5CarrierDetail;
import com.walmartlabs.x12.common.segment.parser.DTMDateTimeReferenceParser;
import com.walmartlabs.x12.common.segment.parser.LINItemIdentificationParser;
import com.walmartlabs.x12.common.segment.parser.N1PartyIdentificationParser;
import com.walmartlabs.x12.common.segment.parser.PIDPartyIdentificationParser;
import com.walmartlabs.x12.common.segment.parser.REFReferenceInformationParser;
import com.walmartlabs.x12.common.segment.parser.TD1CarrierDetailParser;
import com.walmartlabs.x12.common.segment.parser.TD3CarrierDetailParser;
import com.walmartlabs.x12.common.segment.parser.TD5CarrierDetailParser;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.standard.X12ParsedLoop;
import com.walmartlabs.x12.standard.txset.AbstractTransactionSetParserChainable;
import com.walmartlabs.x12.util.ConversionUtil;
import com.walmartlabs.x12.util.TriConsumer;
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
     * 
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
     * 
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
     * currently enforcing only 1 top level HL in the transaction set (ie) only one
     * Shipment HL
     * 
     * @param loops
     * @param asnTx
     * @return
     */
    protected void doLoopParsing(List<X12Loop> loops, AsnTransactionSet asnTx) {
        if (!CollectionUtils.isEmpty(loops) && loops.size() == 1) {
            X12Loop firstLoop = loops.get(0);
            this.parseShipmentLoop(firstLoop, asnTx);
        } else {
            throw new X12ParserException(new X12ErrorDetail("HL", "HL00", "expected one top level HL"));
        }
    }

    /**
     * parse a Shipment Loop
     * 
     */
    private void parseShipmentLoop(X12Loop unparsedLoop, AsnTransactionSet asnTx) {
        //
        // should be a Shipment
        //
        LOGGER.debug(unparsedLoop.getCode());
        if (Shipment.isShipmentLoop(unparsedLoop)) {
            Shipment shipment = new Shipment();
            shipment.copyAttributes(unparsedLoop);
            asnTx.setShipment(shipment);

            //
            // handle the segments that are associated w/ the Shipment Loop
            //
            this.handleLoopSegments(unparsedLoop, shipment, this::doShipmentSegments);

            //
            // handle the children loops
            //
            List<X12Loop> shipmentChildLoops = unparsedLoop.getChildLoops();
            if (!CollectionUtils.isEmpty(shipmentChildLoops)) {
                shipmentChildLoops.forEach(childLoop -> {
                    this.parseOrderLoop(childLoop, shipment);
                });
            }
        } else {
            throw new X12ParserException(new X12ErrorDetail("HL", "HL03", "first HL is not a shipment"));
        }
    }

    /**
     * we always expect the children of a shipment to be an order
     * 
     * @param unparsedLoop
     * @param shipment
     */
    private void parseOrderLoop(X12Loop unparsedLoop, Shipment shipment) {
        //
        // should be an Order
        //
        LOGGER.debug(unparsedLoop.getCode());
        if (Order.isOrderLoop(unparsedLoop)) {
            Order order = new Order();
            order.copyAttributes(unparsedLoop);
            shipment.addParsedChildLoop(order);

            //
            // handle the segments that are associated w/ the Order Loop
            //
            this.handleLoopSegments(unparsedLoop, order, this::doOrderSegments);

            //
            // handle the children loops
            // these loops can appear in a variety of sequences
            //
            this.parseEachChildrenLoop(unparsedLoop, order);

        } else {
            throw new X12ParserException(
                new X12ErrorDetail("HL", "HL03", "expected Order HL but got " + unparsedLoop.getCode()));
        }
    }
    
    private void parseTareLoop(X12Loop unparsedLoop,  X12ParsedLoop parentLoop) {
        //
        // should be a Tare
        //
        LOGGER.debug(unparsedLoop.getCode());
        if (Tare.isTareLoop(unparsedLoop)) {
            Tare tare = new Tare();
            tare.copyAttributes(unparsedLoop);
            parentLoop.addParsedChildLoop(tare);
            
            //
            // handle the segments that are associated w/ the Tare Loop
            //
            this.handleLoopSegments(unparsedLoop, tare, this::doTareSegments);

            //
            // handle the children loops
            // these loops can appear in a variety of sequences
            //
            this.parseEachChildrenLoop(unparsedLoop, tare);
        }        
    }
    
    private void parsePackLoop(X12Loop unparsedLoop,  X12ParsedLoop parentLoop) {
        //
        // should be a Pack
        //
        LOGGER.debug(unparsedLoop.getCode());
        if (Pack.isPackLoop(unparsedLoop)) {
            Pack pack = new Pack();
            pack.copyAttributes(unparsedLoop);
            parentLoop.addParsedChildLoop(pack);
            
            //
            // handle the segments that are associated w/ the Pack Loop
            //
            this.handleLoopSegments(unparsedLoop, pack, this::doPackSegments);

            //
            // handle the children loops
            // these loops can appear in a variety of sequences
            //
            this.parseEachChildrenLoop(unparsedLoop, pack);
        }        
    }
    
    private void parseItemLoop(X12Loop unparsedLoop,  X12ParsedLoop parentLoop) {
        //
        // should be an Item
        //
        LOGGER.debug(unparsedLoop.getCode());
        if (Item.isItemLoop(unparsedLoop)) {
            Item item = new Item();
            item.copyAttributes(unparsedLoop);
            parentLoop.addParsedChildLoop(item);
            
            //
            // handle the segments that are associated w/ the Item Loop
            //
            this.handleLoopSegments(unparsedLoop, item, this::doItemSegments);

            //
            // handle the children loops
            // these loops can appear in a variety of sequences
            //
            this.parseEachChildrenLoop(unparsedLoop, item);
        }        
    }
    
    
    private void parseBatchLoop(X12Loop unparsedLoop,  X12ParsedLoop parentLoop) {
        //
        // should be a Batch
        //
        LOGGER.debug(unparsedLoop.getCode());
        if (Batch.isBatchLoop(unparsedLoop)) {
            Batch batch = new Batch();
            batch.copyAttributes(unparsedLoop);
            parentLoop.addParsedChildLoop(batch);
            
            //
            // handle the segments that are associated w/ the Batch Loop
            //
            this.handleLoopSegments(unparsedLoop, batch, this::doBatchSegments);

            //
            // handle the children loops
            // these loops can appear in a variety of sequences
            //
            this.parseEachChildrenLoop(unparsedLoop, batch);
        }        
    }
    
    
    
    private void parseEachChildrenLoop(X12Loop unparsedLoop,  X12ParsedLoop parentLoop) {
        List<X12Loop> unparsedLoopChildren = unparsedLoop.getChildLoops();
        if (!CollectionUtils.isEmpty(unparsedLoopChildren)) {
            unparsedLoopChildren.forEach(childLoop -> {
                this.parseChildrenLoop(childLoop, parentLoop);
            });
        }
    }
    
    private void parseChildrenLoop(X12Loop unparsedLoop, X12ParsedLoop parentLoop) {
        // loops in an order can be in different sequencing 
        switch (unparsedLoop.getCode()) {
            case Tare.TARE_LOOP_CODE:
                this.parseTareLoop(unparsedLoop, parentLoop);
                break;
            case Pack.PACK_LOOP_CODE:
                this.parsePackLoop(unparsedLoop, parentLoop);
                break;
            case Item.ITEM__LOOP_CODE:
                this.parseItemLoop(unparsedLoop, parentLoop);
                break;
            case Batch.BATCH_LOOP_CODE:
                this.parseBatchLoop(unparsedLoop, parentLoop);
                break;                
            default:
                // TODO: what to do w/ unknown loop
                break;
        }
    }
    
    /**
     * template for processing segments associated with a loop 
     * 
     * @param loop
     * @param loopObject
     * @param function
     */
    private <T> void handleLoopSegments(X12Loop loop, T loopObject, TriConsumer<X12Segment, SegmentIterator, T> function) {
        List<X12Segment> shipmentSegments = loop.getSegments();
        if (!CollectionUtils.isEmpty(shipmentSegments)) {
            SegmentIterator segmentIterator = new SegmentIterator(shipmentSegments);
            while (segmentIterator.hasNext()) {
                X12Segment segment = segmentIterator.next();
                LOGGER.debug(segment.getIdentifier());

                function.accept(segment, segmentIterator, loopObject);
            }
        }  
    }


    /**
     * handle the segment lines that are part of the Order (appearing before the next
     * HL loop)
     * 
     * @param segment
     * @param segmentIterator
     * @param shipment
     */
    private void doShipmentSegments(X12Segment segment, SegmentIterator segmentIterator, Shipment shipment) {
        // TODO: need to keep working on this and add tests
        switch (segment.getIdentifier()) {
            case TD1CarrierDetail.IDENTIFIER:
                shipment.setTd1(TD1CarrierDetailParser.parse(segment));
                break;
            case TD3CarrierDetail.IDENTIFIER:
                shipment.setTd3(TD3CarrierDetailParser.parse(segment));
                break;
            case TD5CarrierDetail.IDENTIFIER:
                shipment.setTd5(TD5CarrierDetailParser.parse(segment));
                break;
            case N1PartyIdentification.IDENTIFIER:
                N1PartyIdentification n1 = N1PartyIdentificationParser.handleN1Loop(segment, segmentIterator);
                shipment.addN1PartyIdentification(n1);
                break;
            case REFReferenceInformation.IDENTIFIER:
                shipment.addReferenceInformation(REFReferenceInformationParser.parse(segment));
                break;                 
            case DTMDateTimeReference.IDENTIFIER:
                DTMDateTimeReference dtm = DTMDateTimeReferenceParser.parse(segment);
                shipment.addDTMDateTimeReference(dtm);
                break;                
            default:
                // TODO: what do we do w/ an unidentified segment
                break;
        }
    }
    
    /**
     * handle the segment lines that are part of the Order (appearing before the next
     * HL loop)
     * 
     * @param segment
     * @param segmentIterator
     * @param order
     */
    private void doOrderSegments(X12Segment segment, SegmentIterator segmentIterator, Order order) {
        switch (segment.getIdentifier()) {
            case PRFPurchaseOrderReference.IDENTIFIER:
                order.setPrf(PRFPurchaseOrderReferenceParser.parse(segment));
                break;
            case REFReferenceInformation.IDENTIFIER:
                order.addReferenceInformation(REFReferenceInformationParser.parse(segment));
                break;            
            default:
                // TODO: what do we do w/ an unidentified segment
                break;
        }
    }
    
    /**
     * handle the segment lines that are part of the Tare (appearing before the next
     * HL loop)
     * 
     * @param segment
     * @param segmentIterator
     * @param tare
     */
    private void doTareSegments(X12Segment segment, SegmentIterator segmentIterator, Tare tare) {
        switch (segment.getIdentifier()) {
            case PKGPackaging.IDENTIFIER:
                // TODO: add this
                break;        
            case PALPalletType.IDENTIFIER:
                // TODO: add this
                break;
            case MANMarkNumber.IDENTIFIER:
                tare.setMan(MANMarkNumberParser.parse(segment));
                break;                
            default:
                // TODO: what do we do w/ an unidentified segment
                break;
        }
    }


    /**
     * handle the segment lines that are part of the Pack (appearing before the next
     * HL loop)
     * 
     * @param segment
     * @param segmentIterator
     * @param pack
     */
    private void doPackSegments(X12Segment segment, SegmentIterator segmentIterator, Pack pack) {
        switch (segment.getIdentifier()) {
            case MANMarkNumber.IDENTIFIER:
                pack.setMan(MANMarkNumberParser.parse(segment));
                break;
            case TD1CarrierDetail.IDENTIFIER:
                pack.setTd1(TD1CarrierDetailParser.parse(segment));
                break;                
            case PO4ItemPhysicalDetail.IDENTIFIER:
                // TODO: add this
                break;
            case PIDProductIdentification.IDENTIFIER:
                pack.addPIDProductIdentification(PIDPartyIdentificationParser.parse(segment));
                break;
            case LINItemIdentification.IDENTIFIER:
                pack.setItemIdentifications(LINItemIdentificationParser.parse(segment));
                break;
            case SN1ItemDetail.IDENTIFIER:
                pack.setSn1(SN1ItemDetailParser.parse(segment));
                break;                 
            default:
                // TODO: what do we do w/ an unidentified segment
                break;
        }    
    }

    /**
     * handle the segment lines that are part of the Item (appearing before the next
     * HL loop)
     * 
     * @param segment
     * @param segmentIterator
     * @param items
     */
    private void doItemSegments(X12Segment segment, SegmentIterator segmentIterator, Item item) {
        switch (segment.getIdentifier()) {
            case PIDProductIdentification.IDENTIFIER:
                item.addPIDProductIdentification(PIDPartyIdentificationParser.parse(segment));
                break;
            case LINItemIdentification.IDENTIFIER:
                item.setItemIdentifications(LINItemIdentificationParser.parse(segment));
                break;
            case SN1ItemDetail.IDENTIFIER:
                item.setSn1(SN1ItemDetailParser.parse(segment));
                break;                 
            default:
                // TODO: what do we do w/ an unidentified segment
                break;
        }    
    }
    
    /**
     * handle the segment lines that are part of the Batch (appearing before the next
     * HL loop)
     * 
     * @param segment
     * @param segmentIterator
     * @param items
     */
    private void doBatchSegments(X12Segment segment, SegmentIterator segmentIterator, Batch batch) {
        switch (segment.getIdentifier()) {
            case PIDProductIdentification.IDENTIFIER:
                batch.addPIDProductIdentification(PIDPartyIdentificationParser.parse(segment));
                break;
            case LINItemIdentification.IDENTIFIER:
                batch.setItemIdentifications(LINItemIdentificationParser.parse(segment));
                break;
            case SN1ItemDetail.IDENTIFIER:
                batch.setSn1(SN1ItemDetailParser.parse(segment));
                break;
            case N1PartyIdentification.IDENTIFIER:
                N1PartyIdentification n1 = N1PartyIdentificationParser.handleN1Loop(segment, segmentIterator);
                batch.addN1PartyIdentification(n1);
                break;
            case REFReferenceInformation.IDENTIFIER:
                batch.addReferenceInformation(REFReferenceInformationParser.parse(segment));
                break;                
            case DTMDateTimeReference.IDENTIFIER:
                DTMDateTimeReference dtm = DTMDateTimeReferenceParser.parse(segment);
                batch.addDTMDateTimeReference(dtm);
                break;                  
            default:
                // TODO: what do we do w/ an unidentified segment
                break;
        }    
    }

}
