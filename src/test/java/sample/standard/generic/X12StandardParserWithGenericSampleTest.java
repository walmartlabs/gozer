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

package sample.standard.generic;

import com.walmartlabs.x12.X12Document;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.standard.InterchangeControlEnvelope;
import com.walmartlabs.x12.standard.StandardX12Document;
import com.walmartlabs.x12.standard.StandardX12Parser;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.standard.txset.generic.GenericTransactionSet;
import com.walmartlabs.x12.standard.txset.generic.GenericTransactionSetParser;
import com.walmartlabs.x12.testing.util.X12DocumentTestData;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * this sample shows how to use the {@link StandardX12Parser}
 * when registered with the {@link GenericTransactionSetParser}
 *
 * it is much more difficult to access various attributes
 * on a document in the transaction set when compared to 
 * the more specialized parsers
 *
 */
public class X12StandardParserWithGenericSampleTest {

    StandardX12Parser x12Parser;

    @Before
    public void init() {
        x12Parser = new StandardX12Parser();
        x12Parser.registerTransactionSetParser(new GenericTransactionSetParser());
    }

    @Test
    public void test_generic_parse_po850() {
        
        String sourceData = X12DocumentTestData.readFile("src/test/resources/po850/po850.txt");
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
        assertEquals("PO", group.getFunctionalCodeId());
        assertEquals("4405197800", group.getApplicationSenderCode());
        assertEquals("999999999", group.getApplicationReceiverCode());
        
        // check transaction sets
        List<X12TransactionSet> txSets = group.getTransactions();
        assertNotNull(txSets);
        assertEquals(1, txSets.size());
        
        X12TransactionSet txSet = txSets.get(0);
        // checking the ST code will give the 
        // user hints as to what the transaction set type is
        assertEquals("850", txSet.getTransactionSetIdentifierCode());
        
        // we are using generic transaction set parser 
        // so from here things get a bit less easy to query/use
        GenericTransactionSet genericTx = (GenericTransactionSet) txSet;
        X12Segment beginSegment = genericTx.getBeginningSegment();
        assertEquals("BEG", beginSegment.getIdentifier());
        // document number (BEG03)
        assertEquals("08292233294", beginSegment.getElement(3));
        // document date (BEG05)
        assertEquals("20101127", beginSegment.getElement(5));
        
        // segments
        List<X12Segment> segments = genericTx.getSegmentsBeforeLoops();
        assertNotNull(segments);
        
        X12Segment segment = segments.get(0);
        assertEquals("REF", segment.getIdentifier());
        assertEquals("DP", segment.getElement(1));
        assertEquals("038", segment.getElement(2));
        assertEquals(null, segment.getElement(3));
        
        segment = segments.get(2);
        assertEquals("ITD", segment.getIdentifier());
        assertEquals("14", segment.getElement(1));
        assertEquals("3", segment.getElement(2));
        assertEquals("2", segment.getElement(3));
        assertEquals(null, segment.getElement(4));
        assertEquals("45", segment.getElement(5));
        assertEquals(null, segment.getElement(6));
        assertEquals("46", segment.getElement(7));
        
        // there should be no loops
        assertNull(genericTx.getLoops());
        assertNull(genericTx.getLoopingErrors());
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
        List<X12TransactionSet> txSets = group.getTransactions();
        assertNotNull(txSets);
        assertEquals(1, txSets.size());
        
        X12TransactionSet txSet = txSets.get(0);
        // checking the ST code will give the 
        // user hints as to what the transaction set type is
        assertEquals("856", txSet.getTransactionSetIdentifierCode());
        
        // we are using generic transaction set parser 
        // so from here things get a bit less easy to query/use
        GenericTransactionSet genericTx = (GenericTransactionSet) txSet;
        X12Segment beginSegment = genericTx.getBeginningSegment();
        assertEquals("BSN", beginSegment.getIdentifier());
        // document number (BSN02)
        assertEquals("829716", beginSegment.getElement(2));
        // document date (BSN03)
        assertEquals("20111206", beginSegment.getElement(3));
        
        // there should be no segments
        List<X12Segment> segments = genericTx.getSegmentsBeforeLoops();
        assertNull(segments);
        
        // there should be no loops
        assertNull(genericTx.getLoopingErrors());
        assertNotNull(genericTx.getLoops());
        
        List<X12Loop> loops = genericTx.getLoops();
        // how many top level loops
        assertEquals(1, loops.size());
        
        // top Loop
        X12Loop topLoop = loops.get(0);
        assertEquals("S", topLoop.getCode());
        assertEquals("1", topLoop.getHierarchicalId());
        assertEquals(null, topLoop.getParentHierarchicalId());
        
        List<X12Segment> topLoopSegments = topLoop.getSegments();
        assertEquals(14, topLoopSegments.size());
        X12Segment segment = topLoopSegments.get(3);
        assertEquals("REF", segment.getIdentifier());
        assertEquals("CN", segment.getElement(1));
        assertEquals("5787970539", segment.getElement(2));
        assertEquals(null, segment.getElement(3));
        
        // children of top loop
        List<X12Loop> childLoops = topLoop.getChildLoops();
        assertEquals(1, childLoops.size());
        
        X12Loop loop = childLoops.get(0);
        assertEquals("O", loop.getCode());
        assertEquals("2", loop.getHierarchicalId());
        assertEquals("1", loop.getParentHierarchicalId());
        
        // children of loop
        childLoops = loop.getChildLoops();
        assertEquals(2, childLoops.size());
        
        // SOI (first item)
        loop = childLoops.get(0);
        assertEquals("I", loop.getCode());
        assertEquals("3", loop.getHierarchicalId());
        assertEquals("2", loop.getParentHierarchicalId());
        
        List<X12Segment> loopSegments = loop.getSegments();
        assertEquals(4, loopSegments.size());
        
        segment = loopSegments.get(1);
        assertEquals("SN1", segment.getIdentifier());
        assertEquals("1", segment.getElement(1));
        assertEquals("24", segment.getElement(2));
        assertEquals("EA", segment.getElement(3));
        
        // SOI (2nd item)
        loop = childLoops.get(1);
        assertEquals("I", loop.getCode());
        assertEquals("4", loop.getHierarchicalId());
        assertEquals("2", loop.getParentHierarchicalId());
        loopSegments = loop.getSegments();
        assertEquals(4, loopSegments.size());
        
        segment = loopSegments.get(1);
        assertEquals("SN1", segment.getIdentifier());
        assertEquals("2", segment.getElement(1));
        assertEquals("6", segment.getElement(2));
        assertEquals("EA", segment.getElement(3));
    }
}
