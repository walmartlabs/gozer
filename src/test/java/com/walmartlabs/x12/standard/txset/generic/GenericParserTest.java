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

package com.walmartlabs.x12.standard.txset.generic;

import com.walmartlabs.x12.X12Document;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.standard.InterchangeControlEnvelope;
import com.walmartlabs.x12.standard.StandardX12Document;
import com.walmartlabs.x12.standard.StandardX12Parser;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.X12Loop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * test the Generic Transaction Set parser when registered with the Standard X12
 * parser
 *
 */
public class GenericParserTest {

    private StandardX12Parser standardParser;

    @BeforeEach
    public void init() {
        standardParser = new StandardX12Parser();
        standardParser.registerTransactionSetParser(new GenericTransactionSetParser());
    }

    @Test
    public void test_Parsing_SourceIsNull() {
        String sourceData = null;
        X12Document x12 = standardParser.parse(sourceData);
        assertNull(x12);
    }

    @Test
    public void test_Parsing_SourceIsEmpty() {
        String sourceData = "";
        X12Document x12 = standardParser.parse(sourceData);
        assertNull(x12);
    }

    @Test
    public void test_Parsing_GenericDocument() {
        String sourceData = this.genericX12Document();
        StandardX12Document x12Doc = standardParser.parse(sourceData);
        assertNotNull(x12Doc);

        // ISA segment
        this.assertEnvelopeHeader(x12Doc);

        // Transaction Sets
        List<X12TransactionSet> txForGroupOne = x12Doc.getGroups().get(0).getTransactions();
        assertNotNull(txForGroupOne);
        assertEquals(1, txForGroupOne.size());

        // ST
        GenericTransactionSet genericTx = (GenericTransactionSet) txForGroupOne.get(0);
        assertEquals("YYZ", genericTx.getTransactionSetIdentifierCode());
        assertEquals("0001", genericTx.getHeaderControlNumber());

        // Beginning Segment Line
        X12Segment beginSegment = genericTx.getBeginningSegment();
        assertNotNull(beginSegment);
        assertEquals("TOM", beginSegment.getIdentifier());
        assertEquals("TOM", beginSegment.getElement(0));
        assertEquals("00", beginSegment.getElement(1));
        assertEquals(null, beginSegment.getElement(2));
        assertEquals("2112", beginSegment.getElement(3));

        // segments after the beginning segment
        // that appear before the loops
        List<X12Segment> segments = genericTx.getSegmentsBeforeLoops();
        assertNotNull(segments);
        assertEquals(2, segments.size());
        assertEquals("DTM", segments.get(0).getIdentifier());
        assertEquals("19740301", segments.get(0).getElement(2));

        assertEquals("REF", segments.get(1).getIdentifier());
        assertEquals("FISH", segments.get(1).getElement(2));

        // Loop Errors
        List<X12ErrorDetail> loopErrors = genericTx.getLoopingErrors();
        assertNull(loopErrors);

        // Loops
        List<X12Loop> topLoops = genericTx.getLoops();
        assertNotNull(topLoops);
        assertEquals(2, topLoops.size());

        //
        // loop A
        //
        X12Loop loopA = topLoops.get(0);
        assertNotNull(loopA);
        assertEquals("A", loopA.getCode());
        assertEquals("1", loopA.getHierarchicalId());
        assertEquals(null, loopA.getParentHierarchicalId());

        List<X12Segment> loopSegmentsForLoopA = loopA.getSegments();
        assertNotNull(loopSegmentsForLoopA);
        assertEquals(1, loopSegmentsForLoopA.size());
        assertEquals("REF", loopSegmentsForLoopA.get(0).getIdentifier());
        assertEquals("RED", loopSegmentsForLoopA.get(0).getElement(2));

        List<X12Loop> childLoopsForLoopA = loopA.getChildLoops();
        assertNotNull(childLoopsForLoopA);
        assertEquals(1, childLoopsForLoopA.size());

        //
        // loop B
        // child of loop A
        //
        X12Loop loopB = childLoopsForLoopA.get(0);
        assertNotNull(loopB);
        assertEquals("B", loopB.getCode());
        assertEquals("2", loopB.getHierarchicalId());
        assertEquals("1", loopB.getParentHierarchicalId());

        List<X12Segment> loopSegmentsForLoopB = loopB.getSegments();
        assertNotNull(loopSegmentsForLoopB);
        assertEquals(2, loopSegmentsForLoopB.size());
        assertEquals("REF", loopSegmentsForLoopB.get(0).getIdentifier());
        assertEquals("X", loopSegmentsForLoopB.get(0).getElement(3));
        assertEquals("1", loopSegmentsForLoopB.get(0).getElement(4));

        assertEquals("REF", loopSegmentsForLoopB.get(1).getIdentifier());
        assertEquals("X", loopSegmentsForLoopB.get(1).getElement(3));
        assertEquals("2", loopSegmentsForLoopB.get(1).getElement(4));

        List<X12Loop> childLoopsForLoopB = loopB.getChildLoops();
        assertNull(childLoopsForLoopB);

        //
        // loop C
        //
        X12Loop loopC = topLoops.get(1);
        assertNotNull(loopC);
        assertEquals("C", loopC.getCode());
        assertEquals("3", loopC.getHierarchicalId());
        assertEquals(null, loopC.getParentHierarchicalId());

        List<X12Segment> loopSegmentsForLoopC = loopC.getSegments();
        assertNull(loopSegmentsForLoopC);

        List<X12Loop> childLoopsForLoopC = loopC.getChildLoops();
        assertNotNull(childLoopsForLoopC);
        assertEquals(2, childLoopsForLoopC.size());

        //
        // loop D
        // child of loop C
        //
        X12Loop loopD = childLoopsForLoopC.get(0);
        assertNotNull(loopD);
        assertEquals("D", loopD.getCode());
        assertEquals("4", loopD.getHierarchicalId());
        assertEquals("3", loopD.getParentHierarchicalId());

        List<X12Segment> loopSegmentsForLoopD = loopD.getSegments();
        assertNotNull(loopSegmentsForLoopD);
        assertEquals(1, loopSegmentsForLoopD.size());
        assertEquals("REF", loopSegmentsForLoopD.get(0).getIdentifier());
        assertEquals("SYRINX", loopSegmentsForLoopD.get(0).getElement(2));

        List<X12Loop> childLoopsForLoopD = loopD.getChildLoops();
        assertNull(childLoopsForLoopD);


        //
        // loop E
        // child of loop C
        //
        X12Loop loopE = childLoopsForLoopC.get(1);
        assertNotNull(loopE);
        assertEquals("E", loopE.getCode());
        assertEquals("5", loopE.getHierarchicalId());
        assertEquals("3", loopE.getParentHierarchicalId());

        List<X12Segment> loopSegmentsForLoopE = loopE.getSegments();
        assertNotNull(loopSegmentsForLoopE);
        assertEquals(1, loopSegmentsForLoopE.size());
        assertEquals("REF", loopSegmentsForLoopE.get(0).getIdentifier());
        assertEquals("BY-TOR", loopSegmentsForLoopE.get(0).getElement(2));

        List<X12Loop> childLoopsForLoopE = loopE.getChildLoops();
        assertNull(childLoopsForLoopE);

        // CTT
        assertEquals(Integer.valueOf(0), genericTx.getTransactionLineItems());

        // SE
        assertEquals(Integer.valueOf(8), genericTx.getExpectedNumberOfSegments());
        assertEquals("0001", genericTx.getTrailerControlNumber());
    }

    @Test
    public void test_Parsing_PurchaseOrderDocument() {
        String sourceData = this.samplePurchaseOrder();
        StandardX12Document x12Doc = standardParser.parse(sourceData);
        assertNotNull(x12Doc);

        // ISA segment
        this.assertEnvelopeHeader(x12Doc);

        // Transaction Sets
        List<X12TransactionSet> txForGroupOne = x12Doc.getGroups().get(0).getTransactions();
        assertNotNull(txForGroupOne);
        assertEquals(1, txForGroupOne.size());

        // ST
        GenericTransactionSet genericTx = (GenericTransactionSet) txForGroupOne.get(0);
        assertEquals("850", genericTx.getTransactionSetIdentifierCode());
        assertEquals("000000010", genericTx.getHeaderControlNumber());

        // Beginning Segment Line
        X12Segment beginSegment = genericTx.getBeginningSegment();
        assertNotNull(beginSegment);
        assertEquals("BEG", beginSegment.getIdentifier());
        assertEquals("00", beginSegment.getElement(1));
        assertEquals("SA", beginSegment.getElement(2));
        assertEquals("08292233294", beginSegment.getElement(3));

        // segments after the beginning segment
        // that appear before the loops
        List<X12Segment> segments = genericTx.getSegmentsBeforeLoops();
        assertNotNull(segments);
        assertEquals(28, segments.size());

        assertEquals("REF", segments.get(0).getIdentifier());
        assertEquals("DP", segments.get(0).getElement(1));
        assertEquals("038", segments.get(0).getElement(2));

        // PID*F****SMALL WIDGET
        assertEquals("PID", segments.get(11).getIdentifier());
        assertEquals("F", segments.get(11).getElement(1));
        assertEquals("SMALL WIDGET", segments.get(11).getElement(5));

        // Loops
        List<X12Loop> topLoops = genericTx.getLoops();
        assertNull(topLoops);

        // Loop Errors
        List<X12ErrorDetail> loopErrors = genericTx.getLoopingErrors();
        assertNull(loopErrors);

        // CTT
        assertEquals(Integer.valueOf(6), genericTx.getTransactionLineItems());

        // SE
        assertEquals(Integer.valueOf(33), genericTx.getExpectedNumberOfSegments());
        assertEquals("000000010", genericTx.getTrailerControlNumber());
    }

    @Test
    public void test_Parsing_AdvanceShipNoticeDocument() {
        String sourceData = this.sampleAdvanceShipNotice();
        StandardX12Document x12Doc = standardParser.parse(sourceData);
        assertNotNull(x12Doc);

        // ISA segment
        this.assertEnvelopeHeader(x12Doc);

        // Transaction Sets
        List<X12TransactionSet> txForGroupOne = x12Doc.getGroups().get(0).getTransactions();
        assertNotNull(txForGroupOne);
        assertEquals(1, txForGroupOne.size());

        // ST
        GenericTransactionSet genericTx = (GenericTransactionSet) txForGroupOne.get(0);
        assertEquals("856", genericTx.getTransactionSetIdentifierCode());
        assertEquals("0001", genericTx.getHeaderControlNumber());

        // Beginning Segment Line
        X12Segment beginSegment = genericTx.getBeginningSegment();
        assertNotNull(beginSegment);
        assertEquals("BSN", beginSegment.getIdentifier());
        assertEquals("00", beginSegment.getElement(1));
        assertEquals("2820967", beginSegment.getElement(2));
        assertEquals("20210329", beginSegment.getElement(3));

        // segments after the beginning segment
        // that appear before the loops
        List<X12Segment> segments = genericTx.getSegmentsBeforeLoops();
        assertNull(segments);

        // Loop Errors
        List<X12ErrorDetail> loopErrors = genericTx.getLoopingErrors();
        assertNull(loopErrors);

        // Loops
        List<X12Loop> topLoops = genericTx.getLoops();
        assertNotNull(topLoops);
        assertEquals(1, topLoops.size());

        // Shipment
        X12Loop shipmentLoop = topLoops.get(0);
        assertNotNull(shipmentLoop);
        assertEquals("S", shipmentLoop.getCode());
        assertEquals("1", shipmentLoop.getHierarchicalId());
        assertEquals(null, shipmentLoop.getParentHierarchicalId());

        List<X12Segment> shipmentSegments = shipmentLoop.getSegments();
        assertNotNull(shipmentSegments);
        assertEquals(9, shipmentSegments.size());
        assertEquals("N1", shipmentSegments.get(5).getIdentifier());
        assertEquals("SF", shipmentSegments.get(5).getElement(1));
        assertEquals("Sunkist Growers Inc", shipmentSegments.get(5).getElement(2));

        List<X12Loop> orderLoops = shipmentLoop.getChildLoops();
        assertNotNull(orderLoops);
        assertEquals(1, orderLoops.size());

        // Order
        X12Loop orderLoop = orderLoops.get(0);
        assertNotNull(orderLoop);
        assertEquals("O", orderLoop.getCode());
        assertEquals("2", orderLoop.getHierarchicalId());
        assertEquals("1", orderLoop.getParentHierarchicalId());

        List<X12Segment> orderSegments = orderLoop.getSegments();
        assertNotNull(orderSegments);
        assertEquals(4, orderSegments.size());
        assertEquals("REF", orderSegments.get(2).getIdentifier());
        assertEquals("IA", orderSegments.get(2).getElement(1));
        assertEquals("694349942", orderSegments.get(2).getElement(2));

        List<X12Loop> batchLoops = orderLoop.getChildLoops();
        assertNotNull(batchLoops);
        assertEquals(1, batchLoops.size());

        // Batch
        X12Loop batchLoop = batchLoops.get(0);
        assertNotNull(batchLoop);
        assertEquals("ZZ", batchLoop.getCode());
        assertEquals("3", batchLoop.getHierarchicalId());
        assertEquals("2", batchLoop.getParentHierarchicalId());

        List<X12Segment> batchSegments = batchLoop.getSegments();
        assertNotNull(batchSegments);
        assertEquals(3, batchSegments.size());
        assertEquals("LIN", batchSegments.get(0).getIdentifier());
        assertEquals("LT", batchSegments.get(0).getElement(2));
        assertEquals("BBC", batchSegments.get(0).getElement(3));

        List<X12Loop> batchLoopChildren = batchLoop.getChildLoops();
        assertNull(batchLoopChildren);

        // CTT
        assertEquals(Integer.valueOf(3), genericTx.getTransactionLineItems());

        // SE
        assertEquals(Integer.valueOf(23), genericTx.getExpectedNumberOfSegments());
        assertEquals("0001", genericTx.getTrailerControlNumber());
    }


    @Test
    public void test_Parsing_AdvanceShipNoticeDocument_bad_loop() {
        String sourceData = this.sampleAdvanceShipNotice();
        sourceData = sourceData.replace("HL*2*1*O", "HL*1*1*O");
        StandardX12Document x12Doc = standardParser.parse(sourceData);
        assertNotNull(x12Doc);

        // ISA segment
        this.assertEnvelopeHeader(x12Doc);

        // Transaction Sets
        List<X12TransactionSet> txForGroupOne = x12Doc.getGroups().get(0).getTransactions();
        assertNotNull(txForGroupOne);
        assertEquals(1, txForGroupOne.size());

        // ST
        GenericTransactionSet genericTx = (GenericTransactionSet) txForGroupOne.get(0);
        assertEquals("856", genericTx.getTransactionSetIdentifierCode());
        assertEquals("0001", genericTx.getHeaderControlNumber());

        // Beginning Segment Line
        X12Segment beginSegment = genericTx.getBeginningSegment();
        assertNotNull(beginSegment);
        assertEquals("BSN", beginSegment.getIdentifier());


        // segments after the beginning segment
        // that appear before the loops
        List<X12Segment> segments = genericTx.getSegmentsBeforeLoops();
        assertNull(segments);

        // Loops
        List<X12Loop> topLoops = genericTx.getLoops();
        assertNotNull(topLoops);
        assertEquals(1, topLoops.size());

        // Loop Errors
        List<X12ErrorDetail> loopErrors = genericTx.getLoopingErrors();
        assertNotNull(loopErrors);
        assertEquals(2, loopErrors.size());
        // loop error 1
        assertEquals("HL segment already exists", loopErrors.get(0).getIssueText());
        assertEquals("HL segment with id (1) already exists", loopErrors.get(0).getInvalidValue());
        // loop error 2
        assertEquals("HL segment is missing parent", loopErrors.get(1).getIssueText());
        assertEquals("HL segment with id (3) is missing parent (2)", loopErrors.get(1).getInvalidValue());

        // Shipment
        X12Loop shipmentLoop = topLoops.get(0);
        assertNotNull(shipmentLoop);
        assertEquals("S", shipmentLoop.getCode());
        assertEquals("1", shipmentLoop.getHierarchicalId());
        assertEquals(null, shipmentLoop.getParentHierarchicalId());

        List<X12Loop> orderLoops = shipmentLoop.getChildLoops();
        assertNotNull(orderLoops);
        assertEquals(1, orderLoops.size());

        // Order
        X12Loop orderLoop = orderLoops.get(0);
        assertNotNull(orderLoop);
        assertEquals("O", orderLoop.getCode());
        assertEquals("1", orderLoop.getHierarchicalId());
        assertEquals("1", orderLoop.getParentHierarchicalId());

        List<X12Loop> batchLoops = orderLoop.getChildLoops();
        assertNull(batchLoops);

        // SE
        assertEquals(Integer.valueOf(23), genericTx.getExpectedNumberOfSegments());
        assertEquals("0001", genericTx.getTrailerControlNumber());
    }

    private String genericX12Document() {
        return new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABC*ZZ*123456789012345*101127*1719*U*00400*000003438*0*P*>")
            .append("\n")
            .append("GS*SH*0000000000*999999999*20210408*1045*00*X*005010")
            .append("\n")
            .append("ST*YYZ*0001")
            .append("\n")
            .append("TOM*00**2112")
            .append("\n")
            .append("DTM*00*19740301")
            .append("\n")
            .append("REF*42*FISH")
            .append("\n")
            .append("HL*1**A")  // 1st top level loop (A)
            .append("\n")
            .append("REF*XX*RED*SECTOR*A")
            .append("\n")
            .append("HL*2*1*B")  // child of A
            .append("\n")
            .append("REF*XX*CYGNUS*X*1")
            .append("\n")
            .append("REF*YY*CYGNUS*X*2")
            .append("\n")
            .append("HL*3**C")  // 2nd top level loop (C)
            .append("\n")
            .append("HL*4*3*D")  // child of C
            .append("\n")
            .append("REF*XX*SYRINX")
            .append("\n")
            .append("HL*5*3*E")  // child of C
            .append("\n")
            .append("REF*XX*BY-TOR")
            .append("\n")
            .append("CTT*00")
            .append("\n")
            .append("SE*8*0001")
            .append("\n")
            .append("GE*1*00")
            .append("\n")
            .append("IEA*1*000003438")
            .toString();
    }

    /**
     * Based on sample
     * https://www.1edisource.com/resources/edi-transactions-sets/edi-850/
     */
    private String samplePurchaseOrder() {
        return new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABC*ZZ*123456789012345*101127*1719*U*00400*000003438*0*P*>")
            .append("\n")
            .append("GS*PO*4405197800*999999999*20101127*1719*1421*X*004010VICS")
            .append("\n")
            .append("ST*850*000000010")
            .append("\n")
            .append("BEG*00*SA*08292233294**20101127*610385385")
            .append("\n")
            .append("REF*DP*038")
            .append("\n")
            .append("REF*PS*R")
            .append("\n")
            .append("ITD*14*3*2**45**46")
            .append("\n")
            .append("DTM*002*20101214")
            .append("\n")
            .append("PKG*F*68***PALLETIZE SHIPMENT")
            .append("\n")
            .append("PKG*F*66***REGULAR")
            .append("\n")
            .append("TD5*A*92*P3**SEE XYZ RETAIL ROUTING GUIDE")
            .append("\n")
            .append("N1*ST*XYZ RETAIL*9*0003947268292")
            .append("\n")
            .append("N3*31875 SOLON RD")
            .append("\n")
            .append("N4*SOLON*OH*44139")
            .append("\n")
            .append("PO1*1*120*EA*9.25*TE*CB*065322-117*PR*RO*VN*AB3542")
            .append("\n")
            .append("PID*F****SMALL WIDGET")
            .append("\n")
            .append("PO4*4*4*EA*PLT94**3*LR*15*CT")
            .append("\n")
            .append("PO1*2*220*EA*13.79*TE*CB*066850-116*PR*RO*VN*RD5322")
            .append("\n")
            .append("PID*F****MEDIUM WIDGET")
            .append("\n")
            .append("PO4*2*2*EA")
            .append("\n")
            .append("PO1*3*126*EA*10.99*TE*CB*060733-110*PR*RO*VN*XY5266")
            .append("\n")
            .append("PID*F****LARGE WIDGET")
            .append("\n")
            .append("PO4*6*1*EA*PLT94**3*LR*12*CT")
            .append("\n")
            .append("PO1*4*76*EA*4.35*TE*CB*065308-116*PR*RO*VN*VX2332")
            .append("\n")
            .append("PID*F****NANO WIDGET")
            .append("\n")
            .append("PO4*4*4*EA*PLT94**6*LR*19*CT")
            .append("\n")
            .append("PO1*5*72*EA*7.5*TE*CB*065374-118*PR*RO*VN*RV0524")
            .append("\n")
            .append("PID*F****BLUE WIDGET")
            .append("\n")
            .append("PO4*4*4*EA")
            .append("\n")
            .append("PO1*6*696*EA*9.55*TE*CB*067504-118*PR*RO*VN*DX1875")
            .append("\n")
            .append("PID*F****ORANGE WIDGET")
            .append("\n")
            .append("PO4*6*6*EA*PLT94**3*LR*10*CT")
            .append("\n")
            .append("CTT*6")
            .append("\n")
            .append("AMT*1*13045.94")
            .append("\n")
            .append("SE*33*000000010")
            .append("\n")
            .append("GE*1*1421")
            .append("\n")
            .append("IEA*1*000003438")
            .append("\n")
            .toString();
    }

    private String sampleAdvanceShipNotice() {
        return new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABC*ZZ*123456789012345*101127*1719*U*00400*000003438*0*P*>")
            .append("\n")
            .append("GS*PO*4405197800*999999999*20101127*1719*1421*X*004010VICS")
            .append("\n")
            .append("ST*856*0001")
            .append("\n")
            .append("BSN*00*2820967*20210329*2226*ZZZZ")
            .append("\n")
            .append("HL*1**S")
            .append("\n")
            .append("TD1**160****G*6256*LB")
            .append("\n")
            .append("TD5**2*WALMRT")
            .append("\n")
            .append("REF*UCB*60504900000438841")
            .append("\n")
            .append("DTM*011*20210329")
            .append("\n")
            .append("FOB*PP")
            .append("\n")
            .append("N1*SF*Sunkist Growers Inc*UL*0605049000013")
            .append("\n")
            .append("N3*27770 N. Entertainment Drive")
            .append("\n")
            .append("N4*Valencia*CA*91355-1092")
            .append("\n")
            .append("N1*ST*WAL-MART GROCERY DC #6057*UL*0078742033808")
            .append("\n")
            .append("HL*2*1*O")
            .append("\n")
            .append("SN1**160*CA")
            .append("\n")
            .append("PRF*0558834757***20210323")
            .append("\n")
            .append("REF*IA*694349942")
            .append("\n")
            .append("REF*IV*438841")
            .append("\n")
            .append("HL*3*2*ZZ")
            .append("\n")
            .append("LIN**LT*BBC")
            .append("\n")
            .append("SN1**32*EA")
            .append("\n")
            .append("DTM*510*20210329")
            .append("\n")
            .append("CTT*3")
            .append("\n")
            .append("SE*23*0001")
            .append("\n")
            .append("GE*1*1421")
            .append("\n")
            .append("IEA*1*000003438")
            .append("\n")
            .toString();
    }

    private void assertEnvelopeHeader(StandardX12Document x12Doc) {
        // ISA segment
        InterchangeControlEnvelope isa = x12Doc.getInterchangeControlEnvelope();
        assertNotNull(isa);
        assertEquals("01", isa.getAuthorizationInformationQualifier());
        assertEquals("0000000000", isa.getAuthorizationInformation());
        assertEquals("01", isa.getSecurityInformationQualifier());
        assertEquals("0000000000", isa.getSecurityInformation());
        assertEquals("ZZ", isa.getInterchangeIdQualifier());
        assertEquals("ABC", isa.getInterchangeSenderId());
        assertEquals("ZZ", isa.getInterchangeIdQualifierTwo());
        assertEquals("123456789012345", isa.getInterchangeReceiverId());
        assertEquals("101127", isa.getInterchangeDate());
        assertEquals("1719", isa.getInterchangeTime());
        assertEquals("U", isa.getInterchangeControlStandardId());
        assertEquals("00400", isa.getInterchangeControlVersion());
        assertEquals("000003438", isa.getInterchangeControlNumber());
        assertEquals("0", isa.getAcknowledgementRequested());
        assertEquals("P", isa.getUsageIndicator());
        assertEquals(">", isa.getElementSeparator());

        // Groups
        assertEquals(new Integer(1), isa.getNumberOfGroups());
        assertEquals("000003438", isa.getTrailerInterchangeControlNumber());

        List<X12Group> groups = x12Doc.getGroups();
        assertNotNull(groups);
        assertEquals(1, groups.size());
    }

}
