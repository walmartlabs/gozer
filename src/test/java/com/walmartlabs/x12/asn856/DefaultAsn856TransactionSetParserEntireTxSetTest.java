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

import static org.junit.Assert.*;

public class DefaultAsn856TransactionSetParserEntireTxSetTest {

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
        assertEquals("05755986", asnTx.getShipmentIdentification());
        assertEquals("20190523", asnTx.getShipmentDate());
        assertEquals("171543", asnTx.getShipmentTime());
        assertEquals("0002", asnTx.getHierarchicalStructureCode());

        this.verifyTheShipment(asnTx);
        this.verifyTheFirstOrder(asnTx.getShipment());
        this.verifyTheSecondOrder(asnTx.getShipment());
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
        assertEquals("PLT94", td1.getRawPackagingCode());

        TD3CarrierDetail td3 = shipment.getTd3();
        assertNull(td3);

        TD5CarrierDetail td5 = shipment.getTd5();
        assertNotNull(td5);
        assertEquals("SQCA", td5.getIdentificationCode());

        List<DTMDateTimeReference> dtmList = shipment.getDtmReferences();
        assertNotNull(dtmList);
        assertEquals(2, dtmList.size());
        
        DTMDateTimeReference dtm = dtmList.get(0);
        assertNotNull(dtm);
        assertEquals("011", dtm.getDateTimeQualifier());
        assertEquals("20190523", dtm.getDate());
        assertNull(dtm.getTime());

        dtm = dtmList.get(1);
        assertNotNull(dtm);
        assertEquals("067", dtm.getDateTimeQualifier());
        assertEquals("201905233", dtm.getDate());
        assertNull(dtm.getTime());
        
        List<N1PartyIdentification> n1List = shipment.getN1PartyIdentifications();
        assertNotNull(n1List);
        assertEquals(2, n1List.size());

        //
        // shipping to
        //
        N1PartyIdentification n1One = n1List.get(0);
        assertNotNull(n1One);
        assertEquals("ST", n1One.getEntityIdentifierCode());
        assertEquals("WALMART CASA GRANDE PERISHABLE 7013", n1One.getName());
        assertEquals("UL", n1One.getIdentificationCodeQualifier());
        assertEquals("0078742042930", n1One.getIdentificationCode());
        N3PartyLocation n3 = n1One.getN3();
        assertNotNull(n3);
        assertEquals("868 W. PETERS ROAD", n3.getAddressInfoOne());
        N4GeographicLocation n4 = n1One.getN4();
        assertNotNull(n4);
        assertEquals("CASA GRANDE", n4.getCityName());

        //
        // shipping from
        //
        N1PartyIdentification n1Two = n1List.get(1);
        assertNotNull(n1Two);
        assertEquals("SF", n1Two.getEntityIdentifierCode());
        assertEquals("RESER'S FINE FOODS, INC.", n1Two.getName());
        assertEquals("UL", n1Two.getIdentificationCodeQualifier());
        assertEquals("0090266420000", n1Two.getIdentificationCode());
        n3 = n1Two.getN3();
        assertNotNull(n3);
        assertEquals("15570 S.W. JENKINS ROAD", n3.getAddressInfoOne());
        n4 = n1Two.getN4();
        assertNotNull(n4);
        assertEquals("BEAVERTON", n4.getCityName());

        // has 2 Orders
        List<X12Loop> shipmentChildLoops = shipment.getParsedChildrenLoops();
        assertNotNull(shipmentChildLoops);
        assertEquals(2, shipmentChildLoops.size());
        
        // always have access to 
        // segments even though 
        // it is easier to use the
        // X12Loop instance 
        List<X12Segment> shipmentSegments = shipment.getSegments();
        assertNotNull(shipmentSegments);
        assertEquals(12, shipmentSegments.size());
        assertEquals("TD1*PLT94*1****G*31302*LB", shipmentSegments.get(0).toString());
        assertEquals("N4*BEAVERTON*OR*97006", shipmentSegments.get(11).toString());
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
        // item
        //
        X12Loop orderChildLoop = orderChildLoops.get(0);
        assertNotNull(orderChildLoop);
        assertTrue(orderChildLoop instanceof Item);
        assertEquals("I", orderChildLoop.getCode());

        Item item = (Item) orderChildLoop;

        List<PIDProductIdentification> pids = item.getProductIdentifications();
        assertNotNull(pids);
        assertEquals(1, pids.size());
        PIDProductIdentification pid = pids.get(0);
        assertNotNull(pid);
        assertEquals("POTATO RED SKIN WALMART 6/4#", pid.getDescription());

        SN1ItemDetail sn1 = item.getSn1();
        assertNotNull(sn1);
        assertEquals("2.000000", sn1.getNumberOfUnits().toString());
        assertEquals("CA", sn1.getUnitOfMeasurement());

        List<LINItemIdentification> itemIdList = item.getItemIdentifications();
        assertNotNull(itemIdList);
        assertEquals(2, itemIdList.size());

        LINItemIdentification lin = itemIdList.get(0);
        assertNotNull(lin);
        assertEquals("IN", lin.getProductIdQualifier());
        assertEquals("008021683", lin.getProductId());

        lin = itemIdList.get(1);
        assertNotNull(lin);
        assertEquals("UP", lin.getProductIdQualifier());
        assertEquals("008113191693", lin.getProductId());

        List<X12Loop> itemChildLoops = item.getParsedChildrenLoops();
        assertNotNull(itemChildLoops);
        assertEquals(2, itemChildLoops.size());

        //
        // Pack 1
        //
        X12Loop itemChildLoop = itemChildLoops.get(0);
        assertNotNull(itemChildLoop);
        assertTrue(itemChildLoop instanceof Pack);
        assertEquals("P", itemChildLoop.getCode());

        Pack pack = (Pack) itemChildLoop;
        MANMarkNumber man = pack.getMan();
        assertNotNull(man);
        assertEquals("UC", man.getQualifier());
        assertEquals("10081131916931", man.getNumber());

        //
        // Pack 2
        //
        itemChildLoop = itemChildLoops.get(1);
        assertNotNull(itemChildLoop);
        assertTrue(itemChildLoop instanceof Pack);
        assertEquals("P", itemChildLoop.getCode());

        pack = (Pack) itemChildLoop;
        man = pack.getMan();
        assertNotNull(man);
        assertEquals("UC", man.getQualifier());
        assertEquals("10081131916932", man.getNumber());
    }

    private void verifyTheSecondOrder(Shipment shipment) {
        List<X12Loop> shipmentChildLoops = shipment.getParsedChildrenLoops();

        //
        // order
        //
        X12Loop shipmentChildLoop = shipmentChildLoops.get(1);
        assertNotNull(shipmentChildLoop);
        assertTrue(shipmentChildLoop instanceof Order);
        assertEquals("O", shipmentChildLoop.getCode());

        Order order = (Order) shipmentChildLoop;
        assertNotNull(order);

        PRFPurchaseOrderReference prf = order.getPrf();
        assertNotNull(prf);
        assertEquals("0210431612", prf.getPurchaseOrderNumber());
        assertEquals("20190520", prf.getDate());

        List<REFReferenceInformation> refs = order.getRefList();
        assertNotNull(refs);
        assertEquals(3, refs.size());

        REFReferenceInformation ref = refs.get(0);
        assertNotNull(ref);
        assertEquals("IA", ref.getReferenceIdentificationQualifier());
        assertEquals("480509093", ref.getReferenceIdentification());

        ref = refs.get(1);
        assertNotNull(ref);
        assertEquals("DP", ref.getReferenceIdentificationQualifier());
        assertEquals("00009", ref.getReferenceIdentification());

        ref = refs.get(2);
        assertNotNull(ref);
        assertEquals("MR", ref.getReferenceIdentificationQualifier());
        assertEquals("0073", ref.getReferenceIdentification());

        List<X12Loop> orderChildLoops = order.getParsedChildrenLoops();
        assertNotNull(orderChildLoops);
        assertEquals(1, orderChildLoops.size());

        //
        // tare
        //
        X12Loop orderChildLoop = orderChildLoops.get(0);
        assertNotNull(orderChildLoop);
        assertTrue(orderChildLoop instanceof Tare);
        assertEquals("T", orderChildLoop.getCode());

        Tare tare = (Tare) orderChildLoop;
        MANMarkNumber man = tare.getMan();
        assertNotNull(man);
        assertEquals("GM", man.getQualifier());
        assertEquals("00100700302232310393", man.getNumber());

        List<X12Loop> tareChildLoops = tare.getParsedChildrenLoops();
        assertNotNull(tareChildLoops);
        assertEquals(2, tareChildLoops.size());

        //
        // Pack 3
        //
        X12Loop tareChildLoop = tareChildLoops.get(0);
        assertNotNull(tareChildLoop);
        assertTrue(tareChildLoop instanceof Pack);
        assertEquals("P", tareChildLoop.getCode());

        Pack pack = (Pack) tareChildLoop;
        man = pack.getMan();
        assertNotNull(man);
        assertEquals("UC", man.getQualifier());
        assertEquals("10081131916933", man.getNumber());

        List<X12Loop> packChildLoops = pack.getParsedChildrenLoops();
        assertNotNull(packChildLoops);
        assertEquals(2, packChildLoops.size());

        //
        // item
        //
        X12Loop packChildLoop = packChildLoops.get(0);
        assertNotNull(packChildLoop);
        assertTrue(packChildLoop instanceof Item);
        assertEquals("I", packChildLoop.getCode());

        Item item = (Item) packChildLoop;

        List<PIDProductIdentification> pids = item.getProductIdentifications();
        assertNull(pids);

        SN1ItemDetail sn1 = item.getSn1();
        assertNotNull(sn1);
        assertEquals("18.000000", sn1.getNumberOfUnits().toString());
        assertEquals("EA", sn1.getUnitOfMeasurement());

        List<LINItemIdentification> itemIdList = item.getItemIdentifications();
        assertNotNull(itemIdList);
        assertEquals(4, itemIdList.size());

        LINItemIdentification lin = itemIdList.get(0);
        assertNotNull(lin);
        assertEquals("UP", lin.getProductIdQualifier());
        assertEquals("039364170623", lin.getProductId());

        lin = itemIdList.get(1);
        assertNotNull(lin);
        assertEquals("IN", lin.getProductIdQualifier());
        assertEquals("005179004", lin.getProductId());

        lin = itemIdList.get(2);
        assertNotNull(lin);
        assertEquals("VN", lin.getProductIdQualifier());
        assertEquals("DBT-12", lin.getProductId());

        lin = itemIdList.get(3);
        assertNotNull(lin);
        assertEquals("UK", lin.getProductIdQualifier());
        assertEquals("00039364170623", lin.getProductId());

        //
        // item
        //
        packChildLoop = packChildLoops.get(1);
        assertNotNull(packChildLoop);
        assertTrue(packChildLoop instanceof Item);
        assertEquals("I", packChildLoop.getCode());

        item = (Item) packChildLoop;

        pids = item.getProductIdentifications();
        assertNull(pids);

        sn1 = item.getSn1();
        assertNotNull(sn1);
        assertEquals("3.000000", sn1.getNumberOfUnits().toString());
        assertEquals("EA", sn1.getUnitOfMeasurement());

        itemIdList = item.getItemIdentifications();
        assertNotNull(itemIdList);
        assertEquals(1, itemIdList.size());

        lin = itemIdList.get(0);
        assertNotNull(lin);
        assertEquals("UP", lin.getProductIdQualifier());
        assertEquals("013921530419", lin.getProductId());

        //
        // Pack 4
        //
        tareChildLoop = tareChildLoops.get(1);
        assertNotNull(tareChildLoop);
        assertTrue(tareChildLoop instanceof Pack);
        assertEquals("P", tareChildLoop.getCode());

        pack = (Pack) tareChildLoop;
        man = pack.getMan();
        assertNotNull(man);
        assertEquals("UC", man.getQualifier());
        assertEquals("10081131916934", man.getNumber());

        packChildLoops = pack.getParsedChildrenLoops();
        assertNotNull(packChildLoops);
        assertEquals(1, packChildLoops.size());

        //
        // item
        //
        packChildLoop = packChildLoops.get(0);
        assertNotNull(packChildLoop);
        assertTrue(packChildLoop instanceof Item);
        assertEquals("I", packChildLoop.getCode());

        item = (Item) packChildLoop;

        pids = item.getProductIdentifications();
        assertNull(pids);

        sn1 = item.getSn1();
        assertNotNull(sn1);
        assertEquals("2.000000", sn1.getNumberOfUnits().toString());
        assertEquals("EA", sn1.getUnitOfMeasurement());

        itemIdList = item.getItemIdentifications();
        assertNotNull(itemIdList);
        assertEquals(1, itemIdList.size());

        lin = itemIdList.get(0);
        assertNotNull(lin);
        assertEquals("UP", lin.getProductIdQualifier());
        assertEquals("039364133147", lin.getProductId());
    }

    private List<X12Segment> getTransactionSetSegments() {
        List<X12Segment> txSegments = new ArrayList<>();

        //
        // ASN 856
        txSegments.add(new X12Segment("ST*856*368090001"));
        txSegments.add(new X12Segment("BSN*00*05755986*20190523*171543*0002"));

        //
        // shipment
        //
        txSegments.add(new X12Segment("HL*1**S"));
        txSegments.add(new X12Segment("TD1*PLT94*1****G*31302*LB"));
        txSegments.add(new X12Segment("TD5**2*SQCA"));
        txSegments.add(new X12Segment("REF*UCB*711170010491361"));
        txSegments.add(new X12Segment("DTM*011*20190523"));
        txSegments.add(new X12Segment("DTM*067*201905233"));
        txSegments.add(new X12Segment("FOB*PP"));
        // ship to
        txSegments.add(new X12Segment("N1*ST*WALMART CASA GRANDE PERISHABLE 7013*UL*0078742042930"));
        txSegments.add(new X12Segment("N3*868 W. PETERS ROAD"));
        txSegments.add(new X12Segment("N4*CASA GRANDE*AZ*85193"));
        // ship from
        txSegments.add(new X12Segment("N1*SF*RESER'S FINE FOODS, INC.*UL*0090266420000"));
        txSegments.add(new X12Segment("N3*15570 S.W. JENKINS ROAD"));
        txSegments.add(new X12Segment("N4*BEAVERTON*OR*97006"));

        //
        // order 1
        //
        // I,P
        txSegments.add(new X12Segment("HL*2*1*O"));
        txSegments.add(new X12Segment("PRF*0391494868"));
        txSegments.add(new X12Segment("REF*IA*579284804"));

        // Item on both packs 1 and 2
        txSegments.add(new X12Segment("HL*3*2*I"));
        txSegments.add(new X12Segment("LIN**IN*008021683*UP*008113191693"));
        txSegments.add(new X12Segment("SN1**2*CA"));
        txSegments.add(new X12Segment("PID*F*08***POTATO RED SKIN WALMART 6/4#"));

        // Pack 1 on Order 1
        txSegments.add(new X12Segment("HL*4*3*P"));
        txSegments.add(new X12Segment("MAN*UC*10081131916931"));

        // Pack 2 on Order 2
        txSegments.add(new X12Segment("HL*5*3*P"));
        txSegments.add(new X12Segment("MAN*UC*10081131916932"));

        //
        // order 2
        //
        // T,P,I
        txSegments.add(new X12Segment("HL*6*1*O"));
        txSegments.add(new X12Segment("PRF*0210431612***20190520"));
        txSegments.add(new X12Segment("REF*IA*480509093"));
        txSegments.add(new X12Segment("REF*DP*00009"));
        txSegments.add(new X12Segment("REF*MR*0073"));

        // Tare on Order 2
        txSegments.add(new X12Segment("HL*7*6*T"));
        txSegments.add(new X12Segment("MAN*GM*00100700302232310393"));

        // Pack 3 on Tare
        txSegments.add(new X12Segment("HL*8*7*P"));
        txSegments.add(new X12Segment("MAN*UC*10081131916933"));

        // Item on Pack 3
        txSegments.add(new X12Segment("HL*9*8*I"));
        txSegments.add(new X12Segment("LIN**UP*039364170623*IN*005179004*VN*DBT-12*UK*00039364170623"));
        txSegments.add(new X12Segment("SN1**18*EA"));

        // Item on Pack 3
        txSegments.add(new X12Segment("HL*10*8*I"));
        txSegments.add(new X12Segment("LIN**UP*013921530419"));
        txSegments.add(new X12Segment("SN1**3*EA"));

        // Pack 4 on Tare
        txSegments.add(new X12Segment("HL*11*7*P"));
        txSegments.add(new X12Segment("MAN*UC*10081131916934"));

        // Item on Pack 4
        txSegments.add(new X12Segment("HL*12*11*I"));
        txSegments.add(new X12Segment("LIN**UP*039364133147"));
        txSegments.add(new X12Segment("SN1**2*EA"));

        txSegments.add(new X12Segment("SE*296*368090001"));

        return txSegments;
    }
}