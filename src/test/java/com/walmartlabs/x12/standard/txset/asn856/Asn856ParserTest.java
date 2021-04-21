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

import com.walmartlabs.x12.X12Document;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.standard.InterchangeControlEnvelope;
import com.walmartlabs.x12.standard.StandardX12Document;
import com.walmartlabs.x12.standard.StandardX12Parser;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.txset.asn856.AsnTransactionSet;
import com.walmartlabs.x12.standard.txset.asn856.DefaultAsn856TransactionSetParser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class Asn856ParserTest {

    private StandardX12Parser asnParser;

    @Before
    public void init() {
        asnParser = new StandardX12Parser();
        asnParser.registerTransactionSetParser(new DefaultAsn856TransactionSetParser());
    }

    @Test
    public void test_Parsing_SourceIsNull() throws IOException {
        String sourceData = null;
        X12Document x12 = asnParser.parse(sourceData);
        assertNull(x12);
    }

    @Test
    public void test_Parsing_SourceIsEmpty() throws IOException {
        String sourceData = "";
        X12Document x12 = asnParser.parse(sourceData);
        assertNull(x12);
    }

    @Test
    public void test_Parsing_Asn856() throws IOException {
        byte[] asnBytes = Files.readAllBytes(Paths.get("src/test/resources/asn856/asn856.txt"));
        StandardX12Document x12 = asnParser.parse(new String(asnBytes));
        assertNotNull(x12);

        // ISA segment
        InterchangeControlEnvelope isa = x12.getInterchangeControlEnvelope();
        assertNotNull(isa);
        assertEquals("01", isa.getAuthorizationInformationQualifier());
        assertEquals("0000000000", isa.getAuthorizationInformation());
        assertEquals("01", isa.getSecurityInformationQualifier());
        assertEquals("0000000000", isa.getSecurityInformation());
        assertEquals("ZZ", isa.getInterchangeIdQualifier());
        assertEquals("ABCDEFGHIJKLMNO", isa.getInterchangeSenderId());
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
        assertEquals("000000049", isa.getTrailerInterchangeControlNumber());

        List<X12Group> groups = x12.getGroups();
        assertNotNull(groups);
        assertEquals(1, groups.size());

        // Transaction Sets
        List<X12TransactionSet> txForGroupOne = x12.getGroups().get(0).getTransactions();
        assertNotNull(txForGroupOne);
        assertEquals(1, txForGroupOne.size());

        // ST
        AsnTransactionSet asnTx = (AsnTransactionSet) txForGroupOne.get(0);
        assertEquals("856", asnTx.getTransactionSetIdentifierCode());
        assertEquals("0008", asnTx.getHeaderControlNumber());
        
        // BSN
        assertEquals("14", asnTx.getPurposeCode());
        assertEquals("829716", asnTx.getShipmentIdentification());
        assertEquals("20111206", asnTx.getShipmentDate());
        assertEquals("142428", asnTx.getShipmentTime());
        assertEquals("0002", asnTx.getHierarchicalStructureCode());
        
        // SE
        assertEquals(Integer.valueOf(31), asnTx.getExpectedNumberOfSegments());
        assertEquals("0008", asnTx.getTrailerControlNumber());
    }

    /**
         
    
    
    HL*1**S
    TD1*MIX71*201****G*4979.668*LB
    TD5*B*2*WALM*M
    REF*BM*3074167166
    DTM*067*20190531
    FOB*PP
    N1*SF*Monroe, LA
    N1*ST*WALMART #2940 NEIGHBORHOOD MKT*UL*0078742078892
    HL*2*1*O
    PRF*3074167166***20190531
    REF*DP*0005
    REF*MR*0007
    REF*IA*099739950
    REF*IV*3074167166
    HL*3*2*P
    MAN*GM*00140181064478438274
    HL*4*3*I
    LIN**UP*049000024685
    SN1**16*EA
    HL*5*3*I
    LIN**UP*049000045840
    SN1**24*EA
    HL*6*3*I
    LIN**UP*049000024692
    SN1**16*EA
    HL*7*3*I
    LIN**UP*049000025422
    SN1**4*EA
    HL*8*3*I
    LIN**UP*078000003864
    SN1**12*EA
    HL*9*3*I
    LIN**UP*078000003888
    SN1**8*EA
    HL*10*3*I
    LIN**UP*049000025637
    SN1**4*EA
    HL*11*3*I
    LIN**UP*049000000443
    SN1**72*EA
    HL*12*3*I
    LIN**UP*049000000450
    SN1**24*EA
    HL*13*3*I
    LIN**UP*078000082401
    SN1**24*EA
    HL*14*2*P
    MAN*GM*00140181064478438281
    HL*15*14*I
    LIN**UP*049000050141
    SN1**8*EA
    HL*16*14*I
    LIN**UP*049000050110
    SN1**8*EA
    HL*17*14*I
    LIN**UP*049000050134
    SN1**8*EA
    HL*18*14*I
    LIN**UP*049000050158
    SN1**8*EA
    HL*19*14*I
    LIN**UP*049000031652
    SN1**48*EA
    HL*20*2*P
    MAN*GM*00140181064478438298
    HL*21*20*I
    LIN**UP*049000045758
    SN1**30*EA
    HL*22*20*I
    LIN**UP*049000045741
    SN1**60*EA
    HL*23*20*I
    LIN**UP*049000054569
    SN1**45*EA
    HL*24*20*I
    LIN**UP*049000067217
    SN1**9*EA
    HL*25*20*I
    LIN**UP*049000055375
    SN1**12*EA
    HL*26*20*I
    LIN**UP*049000005486
    SN1**12*EA
    HL*27*20*I
    LIN**UP*049000006841
    SN1**6*EA
    HL*28*20*I
    LIN**UP*049000006124
    SN1**18*EA
    HL*29*20*I
    LIN**UP*180127000104
    SN1**24*EA
    HL*30*20*I
    LIN**UP*049000064063
    SN1**8*EA
    HL*31*20*I
    LIN**UP*815154020909
    SN1**3*EA
    HL*32*20*I
    LIN**UP*070847000037
    SN1**24*EA
    HL*33*20*I
    LIN**UP*070847815037
    SN1**2*EA
    HL*34*20*I
    LIN**UP*070847815051
    SN1**2*EA
    HL*35*20*I
    LIN**UP*070847017127
    SN1**2*EA

    HL*36*2*P
    MAN*GM*00140181064478438304
    HL*37*36*I
    LIN**UP*049000072303
    SN1**4*EA
    HL*38*36*I
    LIN**UP*078000002041
    SN1**4*EA
    HL*39*36*I
    LIN**UP*049000028911
    SN1**16*EA
    HL*40*36*I
    LIN**UP*049000042696
    SN1**2*EA
    HL*41*36*I
    LIN**UP*049000028928
    SN1**16*EA
    HL*42*36*I
    LIN**UP*049000028928
    SN1**8*EA
    HL*43*36*I
    LIN**UP*078000085167
    SN1**2*EA
    HL*44*36*I
    LIN**UP*078000023442
    SN1**4*EA
    HL*45*36*I
    LIN**UP*078000083163
    SN1**16*EA
    HL*46*36*I
    LIN**UP*078000083163
    SN1**4*EA
    HL*47*36*I
    LIN**UP*049000030730
    SN1**6*EA
    HL*48*36*I
    LIN**UP*049000030754
    SN1**6*EA
    HL*49*36*I
    LIN**UP*049000030129
    SN1**6*EA
    HL*50*36*I
    LIN**UP*049000012781
    SN1**16*EA
    HL*51*36*I
    LIN**UP*049000042849
    SN1**16*EA
    HL*52*36*I
    LIN**UP*049000010633
    SN1**8*EA
    HL*53*36*I
    LIN**UP*083900005757
    SN1**60*EA
    CTT*53
    SE*167*0001
    GE*1*000149519
    IEA*1*000149519
     */
    private List<X12Segment> getAsnSegments() {
        List<X12Segment> asnSegments = new ArrayList<>();

        // ISA
        asnSegments.add(new X12Segment(
            "ISA*00*          *00*          *ZZ*099739CC       *08*925485US00     *190531*0457*:*00501*000149519*0*P*>"));
     
        // Group
        asnSegments.add(new X12Segment("GS*SH*099739CC*925485US00*20190531*0457*000149519*X*005010"));
        //
        // ASN 856
        asnSegments.add(new X12Segment("ST*856*0001"));
        asnSegments.add(new X12Segment("BSN*00*3074167166*20190531*0455*0001"));

        // TODO: U are here
        
        //
        // shipment
        //
        asnSegments.add(new X12Segment("HL*1**S"));
        asnSegments.add(new X12Segment("TD1*PLT94*1****G*31302*LB"));
        asnSegments.add(new X12Segment("TD5**2*SQCA"));
        // TD3
        asnSegments.add(new X12Segment("TD3*TL*ABCD*07213567******30394938483234"));
        asnSegments.add(new X12Segment("REF*UCB*711170010491361"));
        asnSegments.add(new X12Segment("DTM*011*20190523"));
        asnSegments.add(new X12Segment("DTM*067*20190523"));
        asnSegments.add(new X12Segment("FOB*PP"));
        // TD3
        asnSegments.add(new X12Segment("TD3*TL*YYZ*98765"));
        asnSegments.add(new X12Segment("REF*AO*42"));
        asnSegments.add(new X12Segment("DTM*067*20190523"));
        // ship to
        asnSegments.add(new X12Segment("N1*ST*WALMART CASA GRANDE PERISHABLE 7013*UL*0078742042930"));
        asnSegments.add(new X12Segment("N3*868 W. PETERS ROAD"));
        asnSegments.add(new X12Segment("N4*CASA GRANDE*AZ*85193"));
        // ship from
        asnSegments.add(new X12Segment("N1*SF*RESER'S FINE FOODS, INC.*UL*0090266420000"));
        asnSegments.add(new X12Segment("N3*15570 S.W. JENKINS ROAD"));
        asnSegments.add(new X12Segment("N4*BEAVERTON*OR*97006"));

        //
        // order 1
        //
        // I,P
        asnSegments.add(new X12Segment("HL*2*1*O"));
        asnSegments.add(new X12Segment("PRF*0391494868"));
        asnSegments.add(new X12Segment("REF*IA*579284804"));

        // Item on both packs 1 and 2
        asnSegments.add(new X12Segment("HL*3*2*I"));
        asnSegments.add(new X12Segment("LIN**IN*008021683*UP*008113191693"));
        asnSegments.add(new X12Segment("SN1**2*CA"));
        asnSegments.add(new X12Segment("PID*F*08***POTATO RED SKIN WALMART 6/4#"));

        // Pack 1 on Order 1
        asnSegments.add(new X12Segment("HL*4*3*P"));
        asnSegments.add(new X12Segment("MAN*UC*10081131916931"));

        // Pack 2 on Order 2
        asnSegments.add(new X12Segment("HL*5*3*P"));
        asnSegments.add(new X12Segment("MAN*UC*10081131916932"));

        //
        // order 2
        //
        // T,P,I
        asnSegments.add(new X12Segment("HL*6*1*O"));
        asnSegments.add(new X12Segment("PRF*0210431612***20190520"));
        asnSegments.add(new X12Segment("REF*IA*480509093"));
        asnSegments.add(new X12Segment("REF*DP*00009"));
        asnSegments.add(new X12Segment("REF*MR*0073"));

        // Tare on Order 2
        asnSegments.add(new X12Segment("HL*7*6*T"));
        asnSegments.add(new X12Segment("MAN*GM*00100700302232310393"));
        asnSegments.add(new X12Segment("MAN*CP*11211811413"));

        // Pack 3 on Tare
        asnSegments.add(new X12Segment("HL*8*7*P"));
        asnSegments.add(new X12Segment("MAN*UC*10081131916933"));
        asnSegments.add(new X12Segment("MAN*CP*09970020805822"));

        // Item on Pack 3
        asnSegments.add(new X12Segment("HL*9*8*I"));
        asnSegments.add(new X12Segment("LIN**UP*039364170623*IN*005179004*VN*DBT-12*UK*00039364170623"));
        asnSegments.add(new X12Segment("SN1**18*EA"));

        // Item on Pack 3
        asnSegments.add(new X12Segment("HL*10*8*I"));
        asnSegments.add(new X12Segment("LIN**UP*013921530419"));
        asnSegments.add(new X12Segment("SN1**3*EA"));

        // Pack 4 on Tare
        asnSegments.add(new X12Segment("HL*11*7*P"));
        asnSegments.add(new X12Segment("MAN*UC*10081131916934"));

        // Item on Pack 4
        asnSegments.add(new X12Segment("HL*12*11*I"));
        asnSegments.add(new X12Segment("LIN**UP*039364133147"));
        asnSegments.add(new X12Segment("SN1**2*EA"));

        asnSegments.add(new X12Segment("SE*296*368090001"));

        return asnSegments;
    }

}
