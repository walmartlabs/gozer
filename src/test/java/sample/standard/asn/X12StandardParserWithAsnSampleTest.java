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

package sample.standard.asn;

import com.walmartlabs.x12.X12Document;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.common.segment.REFReferenceInformation;
import com.walmartlabs.x12.standard.InterchangeControlEnvelope;
import com.walmartlabs.x12.standard.StandardX12Document;
import com.walmartlabs.x12.standard.StandardX12Parser;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.standard.txset.TransactionSetParser;
import com.walmartlabs.x12.standard.txset.asn856.AsnTransactionSet;
import com.walmartlabs.x12.standard.txset.asn856.DefaultAsn856TransactionSetParser;
import com.walmartlabs.x12.standard.txset.asn856.loop.Item;
import com.walmartlabs.x12.standard.txset.asn856.loop.Order;
import com.walmartlabs.x12.standard.txset.asn856.loop.Shipment;
import com.walmartlabs.x12.standard.txset.asn856.segment.PRFPurchaseOrderReference;
import com.walmartlabs.x12.standard.txset.asn856.segment.SN1ItemDetail;
import com.walmartlabs.x12.testing.util.X12DocumentTestData;
import org.junit.Before;
import org.junit.Test;
import sample.standard.generic.X12StandardParserWithGenericSampleTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * this sample shows how to use the {@link StandardX12Parser}
 * when registered with a {@link TransactionSetParser} that can handle 
 * a specific type of document/transaction set 
 *
 * this sample should be compared to {@link X12StandardParserWithGenericSampleTest} 
 * where it is much more difficult to access various attributes
 * in the transaction set when compared to 
 * the more specialized parsers
 *
 */
public class X12StandardParserWithAsnSampleTest {

    StandardX12Parser x12Parser;

    @Before
    public void init() {
        x12Parser = new StandardX12Parser();
        x12Parser.registerTransactionSetParser(new DefaultAsn856TransactionSetParser());
    }
    
    @Test
    public void test_generic_parse_asn856() {
        
        String sourceData = X12DocumentTestData.readFile("src/test/resources/asn856/asn856.txt");
        X12Document x12Doc = x12Parser.parse(sourceData);
        StandardX12Document x12StdDoc = (StandardX12Document) x12Doc;
        
        assertNotNull(x12Doc);
        
        // check on the envelope
        InterchangeControlEnvelope envelope = x12StdDoc.getInterchangeControlEnvelope();
        assertNotNull(envelope);
        assertEquals("ABCDEFGHIJKLMNO", envelope.getInterchangeSenderId());
        assertEquals("000003438", envelope.getInterchangeControlNumber());
        
        // check groups
        List<X12Group> groups = x12StdDoc.getGroups();
        assertNotNull(groups);
        assertEquals(1, groups.size());
        
        X12Group group = groups.get(0);
        assertEquals("SH", group.getFunctionalCodeId());
        assertEquals("4405197800", group.getApplicationSenderCode());
        assertEquals("999999999", group.getApplicationReceiverCode());
        
        // check transaction sets
        // this is where the custom parser
        // and the generic parser are different
        List<X12TransactionSet> txSets = group.getTransactions();
        assertNotNull(txSets);
        assertEquals(1, txSets.size());
        
        X12TransactionSet txSet = txSets.get(0);
        
        // checking the ST code will give the 
        // user hints as to what the transaction set type is
        assertEquals("856", txSet.getTransactionSetIdentifierCode());
        
        // cast so we get advantages
        AsnTransactionSet asnTxSet = (AsnTransactionSet) txSet;
        
        // document number (BSN02)
        assertEquals("829716", asnTxSet.getShipmentIdentification());
        // document date (BSN03)
        assertEquals("20111206", asnTxSet.getShipmentDate());
        
        // check for looping errors
        assertNull(asnTxSet.getLoopingErrors());
        
        // ASN should have 1 top-level loop
        // that is a Shipment
        Shipment shipment = asnTxSet.getShipment();
        
        // top Loop (shipment)
        assertEquals("S", shipment.getCode());
        assertEquals("1", shipment.getHierarchicalId());
        assertEquals(null, shipment.getParentHierarchicalId());
        
        List<X12Segment> shipmentSegments = shipment.getSegments();
        assertEquals(14, shipmentSegments.size());
        

        List<REFReferenceInformation> refList = shipment.getRefList();
        assertNotNull(refList);
        assertEquals(2, refList.size());
        
        REFReferenceInformation ref = refList.get(1);
        assertEquals("CN", ref.getReferenceIdentificationQualifier());
        assertEquals("5787970539", ref.getReferenceIdentification());
        
        // children of shipment
        List<X12Loop> childLoops = shipment.getParsedChildrenLoops();
        assertNotNull(childLoops);
        assertEquals(1, childLoops.size());
        
        // order loop
        X12Loop loop = childLoops.get(0);
        assertTrue(X12Loop.isLoopWithCode(loop, "O"));
        assertEquals("O", loop.getCode());
        assertEquals("2", loop.getHierarchicalId());
        assertEquals("1", loop.getParentHierarchicalId());
        
        Order order = (Order) loop;
        PRFPurchaseOrderReference prf = order.getPrf();
        assertNotNull(prf);
        assertEquals("99999817", prf.getPurchaseOrderNumber());
        assertEquals("20111205", prf.getDate());
        
        // children of order
        childLoops = order.getParsedChildrenLoops();
        assertNotNull(childLoops);
        assertEquals(2, childLoops.size());
        
        // SOI (first item)
        loop = childLoops.get(0);
        assertTrue(X12Loop.isLoopWithCode(loop, "I"));
        assertEquals("I", loop.getCode());
        assertEquals("3", loop.getHierarchicalId());
        assertEquals("2", loop.getParentHierarchicalId());
        
        Item item = (Item) loop;
        SN1ItemDetail sn1 = item.getSn1();
        assertEquals("24", sn1.getNumberOfUnits());
        assertEquals("EA", sn1.getUnitOfMeasurement());
        
        // SOI (2nd item)
        loop = childLoops.get(1);
        assertTrue(X12Loop.isLoopWithCode(loop, "I"));
        assertEquals("I", loop.getCode());
        assertEquals("4", loop.getHierarchicalId());
        assertEquals("2", loop.getParentHierarchicalId());
        
        item = (Item) loop;
        sn1 = item.getSn1();
        assertEquals("6", sn1.getNumberOfUnits());
        assertEquals("EA", sn1.getUnitOfMeasurement());
    }
}
