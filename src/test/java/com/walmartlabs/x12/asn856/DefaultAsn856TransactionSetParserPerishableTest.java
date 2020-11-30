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
import com.walmartlabs.x12.asn856.segment.MANMarkNumber;
import com.walmartlabs.x12.asn856.segment.PRFPurchaseOrderReference;
import com.walmartlabs.x12.asn856.segment.SN1ItemDetail;
import com.walmartlabs.x12.common.segment.DTMDateTimeReference;
import com.walmartlabs.x12.common.segment.LINItemIdentification;
import com.walmartlabs.x12.common.segment.N1PartyIdentification;
import com.walmartlabs.x12.common.segment.N3PartyLocation;
import com.walmartlabs.x12.common.segment.N4GeographicLocation;
import com.walmartlabs.x12.common.segment.PIDProductIdentification;
import com.walmartlabs.x12.common.segment.REFReferenceInformation;
import com.walmartlabs.x12.common.segment.TD1CarrierDetail;
import com.walmartlabs.x12.common.segment.TD3CarrierDetail;
import com.walmartlabs.x12.common.segment.TD5CarrierDetail;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.X12Loop;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DefaultAsn856TransactionSetParserPerishableTest {

    private DefaultAsn856TransactionSetParser txParser;

    @Before
    public void init() {
        txParser = new DefaultAsn856TransactionSetParser();
    }

    @Test
    public void test_doParse() {
        // set up the data
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getTransactionSetSegments();

        // parse
        X12TransactionSet txSet = txParser.doParse(segments, x12Group);
        assertNotNull(txSet);
        assertEquals("856", txSet.getTransactionSetIdentifierCode());
        assertTrue(txSet instanceof AsnTransactionSet);

        AsnTransactionSet asnTx = (AsnTransactionSet) txSet;
        assertEquals("00", asnTx.getPurposeCode());
        assertEquals("1298354", asnTx.getShipmentIdentification());
        assertEquals("20200523", asnTx.getShipmentDate());
        assertEquals("215946", asnTx.getShipmentTime());
        assertEquals("ZZZZ", asnTx.getHierarchicalStructureCode());

        this.verifyTheShipment(asnTx);
        this.verifyTheFirstOrder(asnTx.getShipment());
    }

    private void verifyTheShipment(AsnTransactionSet asnTx) {
        //
        // shipment
        //
        Shipment shipment = asnTx.getShipment();
        assertNotNull(shipment);
        assertEquals("S", shipment.getCode());

        TD1CarrierDetail td1 = shipment.getTd1();
        assertNotNull(td1);
        assertNull(td1.getRawPackagingCode());
        assertEquals("G", td1.getWeightQualifier());
        assertEquals("2490.0000", td1.getWeight().toString());
        assertEquals("LB", td1.getUnitOfMeasure());

        TD3CarrierDetail td3 = shipment.getTd3();
        assertNull(td3);

        TD5CarrierDetail td5 = shipment.getTd5();
        assertNotNull(td5);
        assertEquals("2", td5.getIdentificationCodeQualifier());
        assertEquals("PRIJ", td5.getIdentificationCode());
        assertEquals("M", td5.getTransportationMethodTypeCode());

        List<REFReferenceInformation> refs = shipment.getRefList();
        assertNotNull(refs);
        assertEquals(2, refs.size());
        
        REFReferenceInformation ref = refs.get(0);
        assertNotNull(ref);
        assertEquals("UCB", ref.getReferenceIdentificationQualifier());
        assertEquals("10000650002269359", ref.getReferenceIdentification());

        ref = refs.get(1);
        assertNotNull(ref);
        assertEquals("AO", ref.getReferenceIdentificationQualifier());
        assertEquals("22693594", ref.getReferenceIdentification());
        
        List<DTMDateTimeReference> dtmList = shipment.getDtmReferences();
        assertNotNull(dtmList);
        assertEquals(1, dtmList.size());
        
        DTMDateTimeReference dtm = dtmList.get(0);
        assertNotNull(dtm);
        assertEquals("011", dtm.getDateTimeQualifier());
        assertEquals("20200523", dtm.getDate());
        assertNull(dtm.getTime());
        
        List<N1PartyIdentification> n1List = shipment.getN1PartyIdentifications();
        assertNotNull(n1List);
        assertEquals(2, n1List.size());

        //
        // shipping from
        //
        N1PartyIdentification n1Two = n1List.get(0);
        assertNotNull(n1Two);
        assertEquals("SF", n1Two.getEntityIdentifierCode());
        assertEquals("Los Angeles Salad Company", n1Two.getName());
        assertNull(n1Two.getIdentificationCodeQualifier());
        assertNull(n1Two.getIdentificationCode());
        N3PartyLocation n3 = n1Two.getN3();
        assertNotNull(n3);
        assertEquals("3030 E. WASHINGTON BLVD", n3.getAddressInfoOne());
        N4GeographicLocation n4 = n1Two.getN4();
        assertEquals("LOS ANGELES", n4.getCityName());
        assertEquals("CA", n4.getStateOrProvinceCode());
        assertEquals("90023", n4.getPostalCode());
        
        //
        // shipping to
        //
        N1PartyIdentification n1One = n1List.get(1);
        assertNotNull(n1One);
        assertEquals("ST", n1One.getEntityIdentifierCode());
        assertEquals("LONDON 6097", n1One.getName());
        assertEquals("UL", n1One.getIdentificationCodeQualifier());
        assertEquals("0078742035499", n1One.getIdentificationCode());
        n3 = n1One.getN3();
        assertNull(n3);
        n4 = n1One.getN4();
        assertNull(n4);

        // Orders
        List<X12Loop> shipmentChildLoops = shipment.getParsedChildrenLoops();
        assertNotNull(shipmentChildLoops);
        assertEquals(1, shipmentChildLoops.size());
        
        // always have access to 
        // segments even though 
        // it is easier to use the
        // X12Loop instance 
        List<X12Segment> shipmentSegments = shipment.getSegments();
        assertNotNull(shipmentSegments);
        assertEquals(10, shipmentSegments.size());
        assertEquals("TD1******G*2490*LB", shipmentSegments.get(0).toString());
        assertEquals("N1*ST*LONDON 6097*UL*0078742035499", shipmentSegments.get(9).toString());
    }

    private void verifyTheFirstOrder(Shipment shipment) {
        List<X12Loop> shipmentChildLoops = shipment.getParsedChildrenLoops();

        X12Loop shipmentChildLoop = shipmentChildLoops.get(0);
        assertNotNull(shipmentChildLoop);
        // 2 ways to determine what the HL is
        assertTrue(shipmentChildLoop instanceof Order);
        assertEquals("O", shipmentChildLoop.getCode());

        //
        // order
        //
        Order order = (Order) shipmentChildLoop;
        assertNotNull(order);

        PRFPurchaseOrderReference prf = order.getPrf();
        assertNotNull(prf);
        assertEquals("0391494868", prf.getPurchaseOrderNumber());

        List<REFReferenceInformation> refs = order.getRefList();
        assertNotNull(refs);
        assertEquals(1, refs.size());

        REFReferenceInformation ref = refs.get(0);
        assertNotNull(ref);
        assertEquals("IA", ref.getReferenceIdentificationQualifier());
        assertEquals("579284804", ref.getReferenceIdentification());

        List<X12Loop> orderChildLoops = order.getParsedChildrenLoops();
        assertNotNull(orderChildLoops);
        assertEquals(1, orderChildLoops.size());
        
        // always have access to 
        // segments even though 
        // it is easier to use the
        // X12Loop instance 
        List<X12Segment> firtOrderSegments = order.getSegments();
        assertNotNull(firtOrderSegments);
        assertEquals(2, firtOrderSegments.size());
        assertEquals("PRF*0391494868", firtOrderSegments.get(0).toString());
        assertEquals("REF*IA*579284804", firtOrderSegments.get(1).toString());
        
        //
        // tare 1
        // 
        X12Loop orderChildLoop = orderChildLoops.get(0);
        assertNotNull(orderChildLoop);
        assertTrue(orderChildLoop instanceof Tare);
        assertEquals("T", orderChildLoop.getCode());
        
        Tare tare = (Tare) orderChildLoop;
        
        MANMarkNumber man = tare.getMan();
        assertNotNull(man);
        assertEquals("GM", man.getQualifier());
        assertEquals("00000006510095090366", man.getNumber());
        
        //        PKGPackaging pkg = tare.getPkg();
        //        assertNotNull(pkg);
        //        assertEquals("S", pkg.getItemDescriptionType());
        //        assertEquals("68", pkg.getPackagingCharacteristicCode());
        //        assertEquals("FD", pkg.getItemDescriptionType());
        //        assertEquals("37", pkg.getPackagingDescriptionCode());

        List<X12Loop> tareChildLoops = tare.getParsedChildrenLoops();
        assertNotNull(tareChildLoops);
        assertEquals(2, tareChildLoops.size());
        
        //
        // pack 1
        //
        X12Loop tareChildLoop = tareChildLoops.get(0);
        this.verifyTheFirstPackOnOrderOne(tareChildLoop);
        
        //
        // pack 2
        //
        X12Loop tareChildLoopTwo = tareChildLoops.get(1);
        this.verifyTheSecondPackOnOrderOne(tareChildLoopTwo);
    }
    
    private void verifyTheFirstPackOnOrderOne(X12Loop tareChildLoop) {
        //
        // pack 1 on tare 1
        //
        assertNotNull(tareChildLoop);
        assertTrue(tareChildLoop instanceof Pack);
        assertEquals("P", tareChildLoop.getCode());

        Pack packOne = (Pack) tareChildLoop;

        List<PIDProductIdentification> pids = packOne.getProductIdentifications();
        assertNotNull(pids);
        assertEquals(2, pids.size());
        PIDProductIdentification pid = pids.get(0);
        assertNotNull(pid);
        assertEquals("F", pid.getItemDescriptionType());
        assertEquals("RADISH BUNCH OM", pid.getDescription());
        pid = pids.get(1);
        assertNotNull(pid);
        assertEquals("F", pid.getItemDescriptionType());
        assertEquals("PRODUCT OF USA-CALIFORNIA", pid.getDescription());
        
        TD1CarrierDetail td1 = packOne.getTd1();
        assertNotNull(td1);
        assertEquals("G", td1.getWeightQualifier());
        assertEquals("2490.0000", td1.getWeight().toString());
        assertEquals("LB", td1.getUnitOfMeasure());
        
        SN1ItemDetail sn1 = packOne.getSn1();
        assertNotNull(sn1);
        assertEquals("5.000000", sn1.getNumberOfUnits().toString());
        assertEquals("CA", sn1.getUnitOfMeasurement());

        List<LINItemIdentification> itemIdList = packOne.getItemIdentifications();
        assertNotNull(itemIdList);
        assertEquals(4, itemIdList.size());

        LINItemIdentification lin = itemIdList.get(0);
        assertNotNull(lin);
        assertEquals("IN", lin.getProductIdQualifier());
        assertEquals("009444543", lin.getProductId());
        
        lin = itemIdList.get(1);
        assertNotNull(lin);
        assertEquals("UK", lin.getProductIdQualifier());
        assertEquals("10000651037551", lin.getProductId());

        lin = itemIdList.get(2);
        assertNotNull(lin);
        assertEquals("UP", lin.getProductIdQualifier());
        assertEquals("000651814056", lin.getProductId());
        
        lin = itemIdList.get(3);
        assertNotNull(lin);
        assertEquals("CH", lin.getProductIdQualifier());
        assertEquals("US-CA", lin.getProductId());

        List<X12Loop> packChildLoops = packOne.getParsedChildrenLoops();
        assertNotNull(packChildLoops);
        assertEquals(1, packChildLoops.size());
        
        //
        // batch on pack 1
        //
        X12Loop batchChildLoopOne = packChildLoops.get(0);
        assertNotNull(batchChildLoopOne);
        assertTrue(batchChildLoopOne instanceof Batch);
        assertEquals("ZZ", batchChildLoopOne.getCode());

        Batch batchOne = (Batch) batchChildLoopOne;
        
        List<LINItemIdentification> batchItemIdList = batchOne.getItemIdentifications();
        assertNotNull(batchItemIdList);
        assertEquals(1, batchItemIdList.size());

        lin = batchItemIdList.get(0);
        assertNotNull(lin);
        assertEquals("LT", lin.getProductIdQualifier());
        assertEquals("1-1356484", lin.getProductId());
        
        sn1 = batchOne.getSn1();
        assertNotNull(sn1);
        assertEquals("5.000000", sn1.getNumberOfUnits().toString());
        assertEquals("CA", sn1.getUnitOfMeasurement());
        
        List<N1PartyIdentification> n1List = batchOne.getN1PartyIdentifications();
        assertNotNull(n1List);
        assertEquals(1, n1List.size());
        N1PartyIdentification n1 = n1List.get(0);
        assertNotNull(n1);
        assertEquals("ZW", n1.getEntityIdentifierCode());
        assertEquals("2901 LETTUCE FIELD SW", n1.getName());
        N3PartyLocation n3 = n1.getN3();
        assertNotNull(n3);
        assertEquals("208 APPLE ST", n3.getAddressInfoOne());
        N4GeographicLocation n4 = n1.getN4();
        assertNotNull(n4);
        assertEquals("COOLTOWN", n4.getCityName());
        assertEquals("CA", n4.getStateOrProvinceCode());
        assertEquals("90839", n4.getPostalCode());
        
        List<DTMDateTimeReference> dtmList = batchOne.getDtmReferences();
        assertNotNull(dtmList);
        assertEquals(1, dtmList.size());
        
        DTMDateTimeReference dtm = dtmList.get(0);
        assertNotNull(dtm);
        assertEquals("510", dtm.getDateTimeQualifier());
        assertEquals("20200521", dtm.getDate());
        assertNull(dtm.getTime());
    }
    
    private void verifyTheSecondPackOnOrderOne(X12Loop tareChildLoopTwo) {
        //
        // pack 2 on tare 1
        //
        assertNotNull(tareChildLoopTwo);
        assertTrue(tareChildLoopTwo instanceof Pack);
        assertEquals("P", tareChildLoopTwo.getCode());

        Pack packTwo = (Pack) tareChildLoopTwo;

        List<PIDProductIdentification> pids = packTwo.getProductIdentifications();
        assertNotNull(pids);
        assertEquals(2, pids.size());
        PIDProductIdentification pid = pids.get(0);
        assertNotNull(pid);
        assertEquals("F", pid.getItemDescriptionType());
        assertEquals("ARTICHOKE LG CA OM", pid.getDescription());
        pid = pids.get(1);
        assertNotNull(pid);
        assertEquals("F", pid.getItemDescriptionType());
        assertEquals("PRODUCT OF USA-CALIFORNIA", pid.getDescription());

        TD1CarrierDetail td1 = packTwo.getTd1();
        assertNull(td1);
        
        SN1ItemDetail sn1 = packTwo.getSn1();
        assertNotNull(sn1);
        assertEquals("45.000000", sn1.getNumberOfUnits().toString());
        assertEquals("CA", sn1.getUnitOfMeasurement());

        List<LINItemIdentification> itemIdList = packTwo.getItemIdentifications();
        assertNotNull(itemIdList);
        assertEquals(4, itemIdList.size());

        LINItemIdentification lin = itemIdList.get(0);
        assertNotNull(lin);
        assertEquals("IN", lin.getProductIdQualifier());
        assertEquals("009495175", lin.getProductId());
        
        lin = itemIdList.get(1);
        assertNotNull(lin);
        assertEquals("UK", lin.getProductIdQualifier());
        assertEquals("10000651044436", lin.getProductId());

        lin = itemIdList.get(2);
        assertNotNull(lin);
        assertEquals("UP", lin.getProductIdQualifier());
        assertEquals("000000047623", lin.getProductId());
        
        lin = itemIdList.get(3);
        assertNotNull(lin);
        assertEquals("CH", lin.getProductIdQualifier());
        assertEquals("US-CA", lin.getProductId());

        List<X12Loop> packChildLoops = packTwo.getParsedChildrenLoops();
        assertNotNull(packChildLoops);
        assertEquals(1, packChildLoops.size());
        
        //
        // batch on pack 2
        //
        X12Loop batchChildLoopTwo = packChildLoops.get(0);
        assertNotNull(batchChildLoopTwo);
        assertTrue(batchChildLoopTwo instanceof Batch);
        assertEquals("ZZ", batchChildLoopTwo.getCode());
        
        Batch batchTwo = (Batch) batchChildLoopTwo;
        
        List<LINItemIdentification> batchItemIdList = batchTwo.getItemIdentifications();
        assertNotNull(batchItemIdList);
        assertEquals(1, batchItemIdList.size());

        lin = batchItemIdList.get(0);
        assertNotNull(lin);
        assertEquals("LT", lin.getProductIdQualifier());
        assertEquals("1-1354687", lin.getProductId());
        
        sn1 = batchTwo.getSn1();
        assertNotNull(sn1);
        assertEquals("45.000000", sn1.getNumberOfUnits().toString());
        assertEquals("CA", sn1.getUnitOfMeasurement());
        
        List<N1PartyIdentification> n1List = batchTwo.getN1PartyIdentifications();
        assertNotNull(n1List);
        assertEquals(1, n1List.size());
        N1PartyIdentification n1 = n1List.get(0);
        assertNotNull(n1);
        assertEquals("ZW", n1.getEntityIdentifierCode());
        assertEquals("2901 LETTUCE FIELD SW", n1.getName());
        N3PartyLocation n3 = n1.getN3();
        assertNotNull(n3);
        assertEquals("208 APPLE ST", n3.getAddressInfoOne());
        N4GeographicLocation n4 = n1.getN4();
        assertNotNull(n4);
        assertEquals("COOLTOWN", n4.getCityName());
        assertEquals("CA", n4.getStateOrProvinceCode());
        assertEquals("90839", n4.getPostalCode());
        
        List<DTMDateTimeReference> dtmList = batchTwo.getDtmReferences();
        assertNotNull(dtmList);
        assertEquals(1, dtmList.size());
        
        DTMDateTimeReference dtm = dtmList.get(0);
        assertNotNull(dtm);
        assertEquals("510", dtm.getDateTimeQualifier());
        assertEquals("20200521", dtm.getDate());
        assertNull(dtm.getTime());
    }

    private List<X12Segment> getTransactionSetSegments() {
        List<X12Segment> txSegments = new ArrayList<>();

        //
        // ASN 856
        txSegments.add(new X12Segment("ST*856*368090001"));
        txSegments.add(new X12Segment("BSN*00*1298354*20200523*215946*ZZZZ"));

        //
        // shipment
        //
        txSegments.add(new X12Segment("HL*1**S"));
        txSegments.add(new X12Segment("TD1******G*2490*LB"));
        txSegments.add(new X12Segment("TD5**2*PRIJ*M"));
        txSegments.add(new X12Segment("REF*UCB*10000650002269359"));
        txSegments.add(new X12Segment("REF*AO*22693594"));
        txSegments.add(new X12Segment("DTM*011*20200523"));
        txSegments.add(new X12Segment("FOB*PP"));
        // ship from
        txSegments.add(new X12Segment("N1*SF*Los Angeles Salad Company"));
        txSegments.add(new X12Segment("N3*3030 E. WASHINGTON BLVD"));
        txSegments.add(new X12Segment("N4*LOS ANGELES*CA*90023"));
        // ship to
        txSegments.add(new X12Segment("N1*ST*LONDON 6097*UL*0078742035499"));

        //
        // order 1
        //
        txSegments.add(new X12Segment("HL*2*1*O"));
        txSegments.add(new X12Segment("PRF*0391494868"));
        txSegments.add(new X12Segment("REF*IA*579284804"));

        // Tare
        txSegments.add(new X12Segment("HL*3*2*T"));
        txSegments.add(new X12Segment("PKG*S*68*FD*37"));
        txSegments.add(new X12Segment("MAN*GM*00000006510095090366"));
        
        // Pack 1 on Order 1
        txSegments.add(new X12Segment("HL*4*3*P"));
        txSegments.add(new X12Segment("LIN**IN*009444543*UK*10000651037551*UP*000651814056*CH*US-CA"));
        txSegments.add(new X12Segment("SN1**5*CA"));
        txSegments.add(new X12Segment("PO4****************RPC6413"));
        txSegments.add(new X12Segment("PID*F****RADISH BUNCH OM"));
        txSegments.add(new X12Segment("PID*F*MSG***PRODUCT OF USA-CALIFORNIA"));
        txSegments.add(new X12Segment("TD1******G*2490*LB"));
        
        // Batch
        txSegments.add(new X12Segment("HL*5*4*ZZ"));
        txSegments.add(new X12Segment("LIN**LT*1-1356484"));
        txSegments.add(new X12Segment("SN1**5*CA"));
        txSegments.add(new X12Segment("DTM*510*20200521"));
        txSegments.add(new X12Segment("N1*ZW*2901 LETTUCE FIELD SW"));
        txSegments.add(new X12Segment("N3*208 APPLE ST"));
        txSegments.add(new X12Segment("N4*COOLTOWN*CA*90839"));
        
        // Pack 2 on Order 1
        txSegments.add(new X12Segment("HL*6*3*P"));
        txSegments.add(new X12Segment("LIN**IN*009495175*UK*10000651044436*UP*000000047623*CH*US-CA"));
        txSegments.add(new X12Segment("SN1**45*CA"));
        txSegments.add(new X12Segment("PO4****************RPC6413"));
        txSegments.add(new X12Segment("PID*F****ARTICHOKE LG CA OM"));
        txSegments.add(new X12Segment("PID*F*MSG***PRODUCT OF USA-CALIFORNIA"));
        
        // Batch
        txSegments.add(new X12Segment("HL*7*6*ZZ"));
        txSegments.add(new X12Segment("LIN**LT*1-1354687"));
        txSegments.add(new X12Segment("SN1**45*CA"));
        txSegments.add(new X12Segment("DTM*510*20200521"));
        txSegments.add(new X12Segment("N1*ZW*2901 LETTUCE FIELD SW"));
        txSegments.add(new X12Segment("N3*208 APPLE ST"));
        txSegments.add(new X12Segment("N4*COOLTOWN*CA*90839"));
        
        // End of Transaction
        txSegments.add(new X12Segment("SE*296*368090001"));

        return txSegments;
    }
}