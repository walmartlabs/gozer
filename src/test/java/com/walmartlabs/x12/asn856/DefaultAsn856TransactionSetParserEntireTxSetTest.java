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
import com.walmartlabs.x12.common.segment.N1PartyIdentification;
import com.walmartlabs.x12.common.segment.N3PartyLocation;
import com.walmartlabs.x12.common.segment.N4GeographicLocation;
import com.walmartlabs.x12.common.segment.TD1CarrierDetails;
import com.walmartlabs.x12.common.segment.TD3CarrierDetails;
import com.walmartlabs.x12.common.segment.TD5CarrierDetails;
import com.walmartlabs.x12.common.segment.parser.N3PartyLocationParser;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.X12Group;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
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
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getTransactionSetSegments();
        X12TransactionSet txSet = txParser.doParse(segments, x12Group);
        assertNotNull(txSet);
        assertTrue(txSet instanceof AsnTransactionSet);
        
        AsnTransactionSet asnTx = (AsnTransactionSet)txSet;
        assertEquals("05755986", asnTx.getShipmentIdentification());
        
        Shipment shipment = asnTx.getShipment();
        assertNotNull(shipment);
        
        TD1CarrierDetails td1 = shipment.getTd1();
        assertNotNull(td1);
        assertEquals("PLT94", td1.getRawPackagingCode());
        
        TD3CarrierDetails td3 = shipment.getTd3();
        assertNull(td3);
        
        TD5CarrierDetails td5 = shipment.getTd5();
        assertNotNull(td5);
        assertEquals("SQCA", td5.getIdentificationCode());
        
        List<N1PartyIdentification> n1List = shipment.getN1PartyIdenfications();
        assertNotNull(n1List);
        assertEquals(2, n1List.size());

        N1PartyIdentification n1One = n1List.get(0);
        assertNotNull(n1One);
        assertEquals("ST", n1One.getEntityIdentifierCode());
        assertEquals("WALMART CASA GRANDE PERISHABLE 7013", n1One.getName());
        N3PartyLocation n3 = n1One.getN3();
        assertNotNull(n3);
        assertEquals("868 W. PETERS ROAD", n3.getAddressInfoOne());
        N4GeographicLocation n4 = n1One.getN4();
        assertNotNull(n4);
        assertEquals("CASA GRANDE", n4.getCityName());
        
        N1PartyIdentification n1Two = n1List.get(1);
        assertNotNull(n1Two);
        assertEquals("SF", n1Two.getEntityIdentifierCode());
        assertEquals("RESER'S FINE FOODS, INC.", n1Two.getName());
        n3 = n1Two.getN3();
        assertNotNull(n3);
        assertEquals("15570 S.W. JENKINS ROAD", n3.getAddressInfoOne());
        n4 = n1Two.getN4();
        assertNotNull(n4);
        assertEquals("BEAVERTON", n4.getCityName());
        
        List<Order> orderList = shipment.getOrders();
        assertNotNull(orderList);
        assertEquals(1, orderList.size());
        
    }
    
    private List<X12Segment> getTransactionSetSegments() {
        List<X12Segment> txSegments = new ArrayList<>();
        
        txSegments.add(new X12Segment("ST*856*368090001"));
        txSegments.add(new X12Segment("BSN*00*05755986*20190523*171543*0002"));
        
        txSegments.add(new X12Segment("HL*1**S"));
        txSegments.add(new X12Segment("TD1*PLT94*1****G*31302*LB"));
        txSegments.add(new X12Segment("TD5**2*SQCA"));
        txSegments.add(new X12Segment("REF*UCB*711170010491361"));
        txSegments.add(new X12Segment("DTM*011*20190523"));
        txSegments.add(new X12Segment("DTM*067*201905233"));
        txSegments.add(new X12Segment("FOB*PP"));
        txSegments.add(new X12Segment("N1*ST*WALMART CASA GRANDE PERISHABLE 7013*UL*0078742042930"));
        txSegments.add(new X12Segment("N3*868 W. PETERS ROAD"));
        txSegments.add(new X12Segment("N4*CASA GRANDE*AZ*85193"));
        txSegments.add(new X12Segment("N1*SF*RESER'S FINE FOODS, INC.*UL*0090266420000"));
        txSegments.add(new X12Segment("N3*15570 S.W. JENKINS ROAD"));
        txSegments.add(new X12Segment("N4*BEAVERTON*OR*97006"));
        
        txSegments.add(new X12Segment("HL*2*1*O"));
        txSegments.add(new X12Segment("PRF*0391494868"));
        txSegments.add(new X12Segment("REF*IA*579284804"));
        
        txSegments.add(new X12Segment("HL*3*2*I"));
        
        txSegments.add(new X12Segment("HL*4*3*P"));
        txSegments.add(new X12Segment("MAN*UC*10081131916931"));
        
        txSegments.add(new X12Segment("SE*296*368090001"));
        
        return txSegments;
    }
    /*


HL*3*2*I
LIN**IN*008021683*UP*008113191693
SN1**24*CA
PID*F*08***POTATO RED SKIN WALMART 6/4#

HL*4*3*P
MAN*UC*10081131916931
     */
}
