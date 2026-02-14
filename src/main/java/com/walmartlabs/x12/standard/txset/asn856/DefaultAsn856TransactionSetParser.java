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

package com.walmartlabs.x12.standard.txset.asn856;

import com.walmartlabs.x12.SegmentIterator;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.common.segment.DTMDateTimeReference;
import com.walmartlabs.x12.common.segment.FOBRelatedInstructions;
import com.walmartlabs.x12.common.segment.LINItemIdentification;
import com.walmartlabs.x12.common.segment.N1PartyIdentification;
import com.walmartlabs.x12.common.segment.PIDProductIdentification;
import com.walmartlabs.x12.common.segment.PKGPackaging;
import com.walmartlabs.x12.common.segment.REFReferenceInformation;
import com.walmartlabs.x12.common.segment.TD1CarrierDetail;
import com.walmartlabs.x12.common.segment.TD3CarrierDetail;
import com.walmartlabs.x12.common.segment.TD5CarrierDetail;
import com.walmartlabs.x12.common.segment.parser.DTMDateTimeReferenceParser;
import com.walmartlabs.x12.common.segment.parser.FOBRelatedInstructionsParser;
import com.walmartlabs.x12.common.segment.parser.LINItemIdentificationParser;
import com.walmartlabs.x12.common.segment.parser.N1PartyIdentificationParser;
import com.walmartlabs.x12.common.segment.parser.PIDPartyIdentificationParser;
import com.walmartlabs.x12.common.segment.parser.PKGPackagingParser;
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
import com.walmartlabs.x12.standard.txset.asn856.loop.Batch;
import com.walmartlabs.x12.standard.txset.asn856.loop.Item;
import com.walmartlabs.x12.standard.txset.asn856.loop.Order;
import com.walmartlabs.x12.standard.txset.asn856.loop.Pack;
import com.walmartlabs.x12.standard.txset.asn856.loop.Shipment;
import com.walmartlabs.x12.standard.txset.asn856.loop.Tare;
import com.walmartlabs.x12.standard.txset.asn856.segment.MANMarkNumber;
import com.walmartlabs.x12.standard.txset.asn856.segment.PALPalletType;
import com.walmartlabs.x12.standard.txset.asn856.segment.PO4ItemPhysicalDetail;
import com.walmartlabs.x12.standard.txset.asn856.segment.PRFPurchaseOrderReference;
import com.walmartlabs.x12.standard.txset.asn856.segment.SN1ItemDetail;
import com.walmartlabs.x12.standard.txset.asn856.segment.parser.MANMarkNumberParser;
import com.walmartlabs.x12.standard.txset.asn856.segment.parser.PALPalletTypeParser;
import com.walmartlabs.x12.standard.txset.asn856.segment.parser.PO4ItemPhysicalDetailParser;
import com.walmartlabs.x12.standard.txset.asn856.segment.parser.PRFPurchaseOrderReferenceParser;
import com.walmartlabs.x12.standard.txset.asn856.segment.parser.SN1ItemDetailParser;
import com.walmartlabs.x12.util.TriConsumer;
import com.walmartlabs.x12.util.X12ParsingUtil;
import com.walmartlabs.x12.util.loop.X12LoopHolder;
import com.walmartlabs.x12.util.loop.X12LoopUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

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
        // Gozer is NOT enforcing the rule that the transaction set should be in a group
        // with an SH functional id code
        return X12ParsingUtil.verifyTransactionSetType(transactionSegments, ASN_TRANSACTION_TYPE);
    }

    /**
     * it is assumed that this method is only called after getting true as a
     * response from {@link handlesTransactionSet}
     */
    @Override
    protected X12TransactionSet doParse(List<X12Segment> transactionSegments, X12Group x12Group) {
        AsnTransactionSet asnTx = null;

        if (CollectionUtils.isNotEmpty(transactionSegments)) {
            asnTx = new AsnTransactionSet();
            this.doParsing(transactionSegments, asnTx);
        }

        return asnTx;
    }

    /**
     * the first segment in the list of {@link X12Segment} should be an ST
     * the last segment in the list of {@link X12Segment} should be an SE
     */
    protected void doParsing(List<X12Segment> transactionSegments, AsnTransactionSet asnTx) {

        SegmentIterator segments = new SegmentIterator(transactionSegments);
        X12Segment currentSegment = null;

        //
        // ST
        //
        if (segments.hasNext()) {
            currentSegment = segments.next();
            this.parseTransactionSetHeader(currentSegment, asnTx);
        }

        //
        // BSN
        //
        if (segments.hasNext()) {
            currentSegment = segments.next();
            this.parseBeginningSegmentForShipNotice(currentSegment, asnTx);
        }

        //
        // DTM segment (optional) can be added between the BSN and HL hierarchy
        //
        this.parseSegmentsBeforeFirstLoop(segments, asnTx);

        //
        // Hierarchical Loops
        //
        this.handleLooping(segments, asnTx);

        //
        // CTT (optional)
        //
        this.handleOptionalSegments(segments, asnTx);

        //
        // SE
        //
        if (segments.hasNext()) {
            currentSegment = segments.next();
            this.parseTransactionSetTrailer(currentSegment, asnTx);
        } else {
            throw X12ParsingUtil.handleUnexpectedSegment("SE", "nothing");
        }
    }

    /**
     * parse the BSN segment
     *
     * @param segment
     * @param asnTx
     * @throws X12ParserException if segment is not BSN
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
            // expecting all Shipment children 
            // to be an Order loop
            //
            List<X12Loop> shipmentChildLoops = unparsedLoop.getChildLoops();
            if (CollectionUtils.isNotEmpty(shipmentChildLoops)) {
                shipmentChildLoops.forEach(childLoop -> {
                    this.parseOrderLoop(childLoop, shipment, asnTx);
                });
            }
        } else {
            asnTx.addX12ErrorDetailForLoop(
                new X12ErrorDetail("HL", "03", "first HL is not a shipment it was " + unparsedLoop.getCode()));
        }
    }

    /**
     * we always expect the children of a shipment to be an order
     *
     * @param unparsedLoop
     * @param shipment
     */
    private void parseOrderLoop(X12Loop unparsedLoop, Shipment shipment, AsnTransactionSet asnTx) {
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
            asnTx.addX12ErrorDetailForLoop(
                new X12ErrorDetail("HL", "03", "Unexpected child loop", "expected Order HL but got " + unparsedLoop.getCode()));
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
        if (CollectionUtils.isNotEmpty(unparsedLoopChildren)) {
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
            case Item.ITEM_LOOP_CODE:
                this.parseItemLoop(unparsedLoop, parentLoop);
                break;
            case Batch.BATCH_LOOP_CODE:
                this.parseBatchLoop(unparsedLoop, parentLoop);
                break;
            default:
                // making the unknown child loop 
                // available to users of Gozer
                parentLoop.addLoop(unparsedLoop);
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
        if (CollectionUtils.isNotEmpty(shipmentSegments)) {
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

        switch (segment.getIdentifier()) {
            case TD1CarrierDetail.IDENTIFIER:
                shipment.addTD1CarrierDetail(TD1CarrierDetailParser.parse(segment));
                break;
            case TD3CarrierDetail.IDENTIFIER:
                shipment.addTD3CarrierDetail(TD3CarrierDetailParser.parse(segment));
                break;
            case TD5CarrierDetail.IDENTIFIER:
                shipment.addTD5CarrierDetail(TD5CarrierDetailParser.parse(segment));
                break;
            case N1PartyIdentification.IDENTIFIER:
                N1PartyIdentification n1 = N1PartyIdentificationParser.handleN1Loop(segment, segmentIterator);
                shipment.addN1PartyIdentification(n1);
                break;
            case REFReferenceInformation.IDENTIFIER:
                shipment.addReferenceInformation(REFReferenceInformationParser.parse(segment));
                break;
            case DTMDateTimeReference.IDENTIFIER:
                shipment.addDTMDateTimeReference(DTMDateTimeReferenceParser.parse(segment));
                break;
            case FOBRelatedInstructions.IDENTIFIER:
                shipment.setFob(FOBRelatedInstructionsParser.parse(segment));
                break;
            default:
                shipment.addUnparsedSegment(segment);
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
            case TD1CarrierDetail.IDENTIFIER:
                order.addTD1CarrierDetail(TD1CarrierDetailParser.parse(segment));
                break;
            case N1PartyIdentification.IDENTIFIER:
                N1PartyIdentification n1 = N1PartyIdentificationParser.handleN1Loop(segment, segmentIterator);
                order.addN1PartyIdentification(n1);
                break;                
            default:
                order.addUnparsedSegment(segment);
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
                tare.addPKGPackaging(PKGPackagingParser.parse(segment));
                break;
            case PALPalletType.IDENTIFIER:
                tare.setPal(PALPalletTypeParser.parse(segment));
                break;
            case MANMarkNumber.IDENTIFIER:
                tare.addMANMarkNumber(MANMarkNumberParser.parse(segment));
                break;
            default:
                tare.addUnparsedSegment(segment);
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
                pack.addMANMarkNumber(MANMarkNumberParser.parse(segment));
                break;
            case N1PartyIdentification.IDENTIFIER:
                N1PartyIdentification n1 = N1PartyIdentificationParser.handleN1Loop(segment, segmentIterator);
                pack.addN1PartyIdentification(n1);
                break; 
            case TD1CarrierDetail.IDENTIFIER:
                pack.addTD1CarrierDetail(TD1CarrierDetailParser.parse(segment));
                break;
            case PO4ItemPhysicalDetail.IDENTIFIER:
                pack.setPo4(PO4ItemPhysicalDetailParser.parse(segment));
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
            case DTMDateTimeReference.IDENTIFIER:
                DTMDateTimeReference dtm = DTMDateTimeReferenceParser.parse(segment);
                pack.addDTMDateTimeReference(dtm);
                break;                
            default:
                pack.addUnparsedSegment(segment);
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
            case REFReferenceInformation.IDENTIFIER:
                item.addReferenceInformation(REFReferenceInformationParser.parse(segment));
                break;
            case DTMDateTimeReference.IDENTIFIER:
                DTMDateTimeReference dtm = DTMDateTimeReferenceParser.parse(segment);
                item.addDTMDateTimeReference(dtm);
                break;
            default:
                item.addUnparsedSegment(segment);
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
                batch.addUnparsedSegment(segment);
                break;
        }
    }

    /**
     * parse the optional DTM segments
     * that can appear between the beginning segment
     * and the first loop
     *
     * it will also move the segment iterator to first loop
     *
     * @param segments
     * @param txSet
     */
    protected void parseSegmentsBeforeFirstLoop(SegmentIterator segments, AsnTransactionSet txSet) {

        while (segments.hasNext()) {
            X12Segment currentSegment = segments.next();
            String segmentId = currentSegment.getIdentifier();
            LOGGER.debug(segmentId);
            if (X12LoopUtil.isHierarchicalLoopStart(currentSegment)
                || X12TransactionSet.TRANSACTION_SET_TRAILER.equals(segmentId)) {
                // we found one of two things
                // (1) start of loops (HL)
                // (2) the end of the transaction (SE)
                // in either case
                // we should back up so
                // the parser template method
                // starts w/ this segment
                segments.previous();
                break;
            } else {
                // add DTM segments to the transaction set
                // ignore other segments until we find the first loop
                if (DTMDateTimeReference.IDENTIFIER.equals(segmentId)) {
                    txSet.addDTMDateTimeReference(DTMDateTimeReferenceParser.parse(currentSegment));
                } else {
                    // add segment to unexpected segment list
                    txSet.addUnexpectedSegmentBeforeLoop(currentSegment);
                }
            }
        }
    }

    /**
     * parse the HL loops
     * it will also move the segment iterator to first segment after loops
     *
     * @param segments
     * @param txSet
     */
    protected void handleLooping(SegmentIterator segments, AsnTransactionSet asnTx) {

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

                // add loop errors to transaction set (if any)
                List<X12ErrorDetail> loopErrors = loopHolder.getLoopErrors();
                asnTx.addX12ErrorDetailForLoop(loopErrors);

                // handle loops
                List<X12Loop> loops = loopHolder.getLoops();
                this.doLoopParsing(loops, asnTx);

                // we processed all of the loops
                // so now set the iterator up
                // so that the next segment after
                // the last loop is next
                segments.reset(indexToSegmentAfterHierarchicalLoops);
            } else {
                // doesn't start w/ HL
                asnTx.addX12ErrorDetailForLoop(
                    new X12ErrorDetail(currentSegment.getIdentifier(), null, "missing shipment loop"));
                // we should back it up
                // and let the parser keep going
                // with that segment
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

    /**
     * currently allowing only 1 top level Shipment HL in the transaction set 
     *
     * @param loops
     * @param asnTx
     */
    protected void doLoopParsing(List<X12Loop> loops, AsnTransactionSet asnTx) {
        if (CollectionUtils.isNotEmpty(loops) && loops.size() == 1) {
            X12Loop firstLoop = loops.get(0);
            this.parseShipmentLoop(firstLoop, asnTx);
        } else {
            asnTx.addX12ErrorDetailForLoop(this.evaluateX12ErrorDetail(loops));
        }
    }

    /**
     * checks for CTT or AMT
     */
    private void handleOptionalSegments(SegmentIterator segments, AsnTransactionSet genericTx) {
        while (segments.hasNext()) {
            X12Segment currentSegment = segments.next();
            String segmentId = currentSegment.getIdentifier();
            if (X12TransactionSet.TRANSACTION_ITEM_TOTAL.equals(segmentId)) {
                this.parseTransactionTotals(currentSegment, genericTx);
            } else {
                // was not CTT or AMT
                // hopefully it is SE
                segments.previous();
                break;
            }

        }
    }

    private X12ErrorDetail evaluateX12ErrorDetail(List<X12Loop> loops) {
        long shipmentCount = CollectionUtils.emptyIfNull(loops)
            .stream()
            .filter(Shipment::isShipmentLoop)
            .count();

        if (shipmentCount <= 0) {
            // this scenario has already handled as first HL is not a shipment it was xx
        } else if (shipmentCount == 1) {
            Optional<X12Loop> nonShipmentLoop = loops.stream()
                .filter(loop -> !Shipment.isShipmentLoop(loop))
                .findFirst();

            if (nonShipmentLoop.isPresent()) {
                X12Loop x12Loop = nonShipmentLoop.get();
                if (StringUtils.isBlank(x12Loop.getParentHierarchicalId())) {
                    String invalidValue = "Missing parent loop on " + x12Loop.getCode() + " loop";
                    return new X12ErrorDetail(X12Loop.HIERARCHY_LOOP_ID, null, "Missing parent loop", invalidValue);
                }
            }
        } else {
            return new X12ErrorDetail(X12Loop.HIERARCHY_LOOP_ID, null, "Multiple Shipment Loops");
        }

        // default error message
        return new X12ErrorDetail(X12Loop.HIERARCHY_LOOP_ID, null, "expected one top level Shipment HL");
    }
}
