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
package com.walmartlabs.x12.util;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.X12Loop;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class X12ParsingUtilTest {

    
    @Test
    public void test_findHierarchicalLoops_null() {
        List<X12Segment> segmentList = null;
        List<X12Loop> loops = X12ParsingUtil.findHierarchicalLoops(segmentList);
        assertNotNull(loops);
        assertTrue(loops.isEmpty());
    }
    
    @Test
    public void test_findHierarchicalLoops_empty() {
        List<X12Segment> segmentList = Collections.emptyList();
        List<X12Loop> loops = X12ParsingUtil.findHierarchicalLoops(segmentList);
        assertNotNull(loops);
        assertTrue(loops.isEmpty());
    }
    
    @Test
    public void test_findHierarchicalLoops_one_loop() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("HL*1**S");
        segmentList.add(segment);
        segment = new X12Segment("DTM*011*20190524");
        segmentList.add(segment);
        segment = new X12Segment("TD3*TL");
        segmentList.add(segment);  
        
        List<X12Loop> loops = X12ParsingUtil.findHierarchicalLoops(segmentList);
        assertNotNull(loops);
        assertEquals(1, loops.size());
        
        X12Loop loop = loops.get(0);
        assertNotNull(loop);
        assertEquals("1", loop.getHierarchicalId());
        assertEquals(null, loop.getParentHierarchicalId());
        assertEquals("S", loop.getCode());
        
        List<X12Segment> segmentsInLoop = loop.getSegments();
        assertNotNull(segmentsInLoop);
        assertEquals(2, segmentsInLoop.size());
        assertEquals("DTM", segmentsInLoop.get(0).getSegmentIdentifier());
        assertEquals("TD3", segmentsInLoop.get(1).getSegmentIdentifier());
        
        List<X12Loop> childLoops = loop.getChildLoops();
        assertNull(childLoops);
    }
    
    @Test
    public void test_findHierarchicalLoops_one_loop_with_children() {
        List<X12Segment> segmentList = new ArrayList<>();
        // shipment
        X12Segment segment = new X12Segment("HL*1**S");
        segmentList.add(segment);
        segment = new X12Segment("DTM*011*20190524");
        segmentList.add(segment);
        segment = new X12Segment("TD3*TL");
        segmentList.add(segment);  
        // order 1
        segment = new X12Segment("HL*2*1*O");
        segmentList.add(segment);
        segment = new X12Segment("PRF*222");
        segmentList.add(segment);
        segment = new X12Segment("REF*IA*12345");
        segmentList.add(segment);
        // order 2
        segment = new X12Segment("HL*3*1*O");
        segmentList.add(segment);
        segment = new X12Segment("PRF*333");
        segmentList.add(segment);
        segment = new X12Segment("REF*IA*54321");
        segmentList.add(segment);
        // pack 1 on order 2
        segment = new X12Segment("HL*4*3*P");
        segmentList.add(segment);
        segment = new X12Segment("MAN*GM*56");
        segmentList.add(segment);
        
        List<X12Loop> loops = X12ParsingUtil.findHierarchicalLoops(segmentList);
        assertNotNull(loops);
        assertEquals(1, loops.size());
        
        // shipment loop
        X12Loop shipmentLoop = loops.get(0);
        assertNotNull(shipmentLoop);
        assertEquals("1", shipmentLoop.getHierarchicalId());
        assertEquals(null, shipmentLoop.getParentHierarchicalId());
        assertEquals("S", shipmentLoop.getCode());
        
        List<X12Segment> segmentsInShipmentLoop = shipmentLoop.getSegments();
        assertNotNull(segmentsInShipmentLoop);
        assertEquals(2, segmentsInShipmentLoop.size());
        assertEquals("DTM", segmentsInShipmentLoop.get(0).getSegmentIdentifier());
        assertEquals("TD3", segmentsInShipmentLoop.get(1).getSegmentIdentifier());
        
        List<X12Loop> childrenOfShipmentLoop = shipmentLoop.getChildLoops();
        assertNotNull(childrenOfShipmentLoop);
        assertEquals(2, childrenOfShipmentLoop.size());
        
        // loop order 1
        X12Loop orderLoop = childrenOfShipmentLoop.get(0);
        assertNotNull(orderLoop);
        assertEquals("2", orderLoop.getHierarchicalId());
        assertEquals("1", orderLoop.getParentHierarchicalId());
        assertEquals("O", orderLoop.getCode());
        
        List<X12Segment> segmentsInOrderLoop = orderLoop.getSegments();
        assertNotNull(segmentsInOrderLoop);
        assertEquals(2, segmentsInOrderLoop.size());
        assertEquals("PRF", segmentsInOrderLoop.get(0).getSegmentIdentifier());
        assertEquals("222", segmentsInOrderLoop.get(0).getSegmentElement(1));
        assertEquals("REF", segmentsInOrderLoop.get(1).getSegmentIdentifier());
        
        assertNull(orderLoop.getChildLoops());
        
        // loop order 2
        orderLoop = childrenOfShipmentLoop.get(1);
        assertNotNull(orderLoop);
        assertEquals("3", orderLoop.getHierarchicalId());
        assertEquals("1", orderLoop.getParentHierarchicalId());
        assertEquals("O", orderLoop.getCode());
        
        segmentsInOrderLoop = orderLoop.getSegments();
        assertNotNull(segmentsInOrderLoop);
        assertEquals(2, segmentsInOrderLoop.size());
        assertEquals("PRF", segmentsInOrderLoop.get(0).getSegmentIdentifier());
        assertEquals("333", segmentsInOrderLoop.get(0).getSegmentElement(1));
        assertEquals("REF", segmentsInOrderLoop.get(1).getSegmentIdentifier());
        
        List<X12Loop>childrenOfOrderLoop = orderLoop.getChildLoops();
        assertNotNull(childrenOfOrderLoop);
        assertEquals(1, childrenOfOrderLoop.size());
        
        // pack on order 2
        X12Loop packLoop = childrenOfOrderLoop.get(0);
        assertNotNull(packLoop);
        assertEquals("4", packLoop.getHierarchicalId());
        assertEquals("3", packLoop.getParentHierarchicalId());
        assertEquals("P", packLoop.getCode());
        
        List<X12Segment> segmentsInPackLoop = packLoop.getSegments();
        assertNotNull(segmentsInPackLoop);
        assertEquals(1, segmentsInPackLoop.size());
        assertEquals("MAN", segmentsInPackLoop.get(0).getSegmentIdentifier());
        
        assertNull(packLoop.getChildLoops());
    }
    
    @Test
    public void test_findHierarchicalLoops_one_loop_missing_parent() {
        List<X12Segment> segmentList = new ArrayList<>();
        // shipment
        X12Segment segment = new X12Segment("HL*1**S");
        segmentList.add(segment);
        segment = new X12Segment("DTM*011*20190524");
        segmentList.add(segment);
        segment = new X12Segment("TD3*TL");
        segmentList.add(segment);  
        // order 1
        segment = new X12Segment("HL*2*1*O");
        segmentList.add(segment);
        segment = new X12Segment("PRF*222");
        segmentList.add(segment);
        segment = new X12Segment("REF*IA*12345");
        segmentList.add(segment);
        // pack 1 on order 2 which does not exit
        segment = new X12Segment("HL*4*3*P");
        segmentList.add(segment);
        segment = new X12Segment("MAN*GM*56");
        segmentList.add(segment);
        
        try {
            X12ParsingUtil.findHierarchicalLoops(segmentList);
            fail("expected X12ParserException");
        } catch (X12ParserException e) {
            assertEquals("HL segment (4) is missing parent (3)", e.getMessage());
        }
    }
    
    @Test
    public void test_findHierarchicalLoops_one_loop_repeated_id() {
        List<X12Segment> segmentList = new ArrayList<>();
        // shipment
        X12Segment segment = new X12Segment("HL*1**S");
        segmentList.add(segment);
        segment = new X12Segment("DTM*011*20190524");
        segmentList.add(segment);
        segment = new X12Segment("TD3*TL");
        segmentList.add(segment);  
        // order 1
        segment = new X12Segment("HL*2*1*O");
        segmentList.add(segment);
        segment = new X12Segment("PRF*222");
        segmentList.add(segment);
        segment = new X12Segment("REF*IA*12345");
        segmentList.add(segment);
        // pack 1 on order 1 but repeats the id 
        segment = new X12Segment("HL*2*2*P");
        segmentList.add(segment);
        segment = new X12Segment("MAN*GM*56");
        segmentList.add(segment);
        
        try {
            X12ParsingUtil.findHierarchicalLoops(segmentList);
            fail("expected X12ParserException");
        } catch (X12ParserException e) {
            assertEquals("HL segment with id (2) already exists", e.getMessage());
        }
    }
    
    @Test
    public void test_findHierarchicalLoops_multiple_loops() {
        List<X12Segment> segmentList = new ArrayList<>();
        // shipment 1
        X12Segment segment = new X12Segment("HL*1**S");
        segmentList.add(segment);
        segment = new X12Segment("DTM*011*20190524");
        segmentList.add(segment);
        segment = new X12Segment("TD3*A");
        segmentList.add(segment);
        // shipment 2
        segment = new X12Segment("HL*2**S");
        segmentList.add(segment);
        segment = new X12Segment("TD3*B");
        segmentList.add(segment);          
        
        List<X12Loop> loops = X12ParsingUtil.findHierarchicalLoops(segmentList);
        assertNotNull(loops);
        assertEquals(2, loops.size());
        
        // first loop
        X12Loop loop = loops.get(0);
        assertNotNull(loop);
        assertEquals("1", loop.getHierarchicalId());
        assertEquals(null, loop.getParentHierarchicalId());
        assertEquals("S", loop.getCode());
        
        List<X12Segment> segmentsInLoop = loop.getSegments();
        assertNotNull(segmentsInLoop);
        assertEquals(2, segmentsInLoop.size());
        assertEquals("DTM", segmentsInLoop.get(0).getSegmentIdentifier());
        assertEquals("TD3", segmentsInLoop.get(1).getSegmentIdentifier());
        assertEquals("A", segmentsInLoop.get(1).getSegmentElement(1));
        
        List<X12Loop> childLoops = loop.getChildLoops();
        assertNull(childLoops);
        
        // second loop
        loop = loops.get(1);
        assertNotNull(loop);
        assertEquals("2", loop.getHierarchicalId());
        assertEquals(null, loop.getParentHierarchicalId());
        assertEquals("S", loop.getCode());
        
        segmentsInLoop = loop.getSegments();
        assertNotNull(segmentsInLoop);
        assertEquals(1, segmentsInLoop.size());
        assertEquals("TD3", segmentsInLoop.get(0).getSegmentIdentifier());
        assertEquals("B", segmentsInLoop.get(0).getSegmentElement(1));
        
        childLoops = loop.getChildLoops();
        assertNull(childLoops);
    }
    
    @Test
    public void test_findHierarchicalLoops_wrong_starting_segment() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("TOP*1");
        segmentList.add(segment);
        segment = new X12Segment("HL*1**S");
        segmentList.add(segment);
        segment = new X12Segment("TD3*TL");
        segmentList.add(segment);  
        
        try {
            X12ParsingUtil.findHierarchicalLoops(segmentList);
            fail("expected X12ParserException");
        } catch (X12ParserException e) {
            assertEquals("expected HL segment but found TOP", e.getMessage());
        }
    }
    
    @Test
    public void test_isValidEnvelope() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("TOP*1");
        segmentList.add(segment);
        segment = new X12Segment("MIDDLE*2");
        segmentList.add(segment);
        segment = new X12Segment("BOTTOM*3");
        segmentList.add(segment);  
        
        assertTrue(X12ParsingUtil.isValidEnvelope(segmentList, "TOP", "BOTTOM"));
    }
    
    @Test
    public void test_isValidEnvelope_fails_missing_bottom() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("TOP*1");
        segmentList.add(segment);
        segment = new X12Segment("BOTTOM*2");
        segmentList.add(segment);
        segment = new X12Segment("MIDDLE*3");
        segmentList.add(segment);  
        
        assertFalse(X12ParsingUtil.isValidEnvelope(segmentList, "TOP", "BOTTOM"));
    }
    
    @Test
    public void test_isValidEnvelope_fails_missing_top() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("HEAD*1");
        segmentList.add(segment);
        segment = new X12Segment("MIDDLE*2");
        segmentList.add(segment);
        segment = new X12Segment("BOTTOM*3");
        segmentList.add(segment);  
        
        assertFalse(X12ParsingUtil.isValidEnvelope(segmentList, "TOP", "BOTTOM"));
    }
    
    @Test
    public void test_isValidEnvelope_fails_missing_both() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("HEAD*1");
        segmentList.add(segment);
        segment = new X12Segment("MIDDLE*2");
        segmentList.add(segment);
        segment = new X12Segment("ANOTHER*3");
        segmentList.add(segment);  
        
        assertFalse(X12ParsingUtil.isValidEnvelope(segmentList, "TOP", "BOTTOM"));
    }
    
    @Test
    public void test_isValidEnvelope_null() {
        List<X12Segment> segmentList = null;
        assertFalse(X12ParsingUtil.isValidEnvelope(segmentList, "TOP", "BOTTOM"));
    }
    
    @Test
    public void test_isValidEnvelope_empty() {
        List<X12Segment> segmentList = Collections.emptyList();
        assertFalse(X12ParsingUtil.isValidEnvelope(segmentList, "TOP", "BOTTOM"));
    }
    
    @Test
    public void test_verifyTransactionSetType() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("ST*856*0001");
        segmentList.add(segment);
        segment = new X12Segment("BSN*00****0001");
        segmentList.add(segment);
        segment = new X12Segment("SE*1*0001");
        segmentList.add(segment);  
        
        assertTrue(X12ParsingUtil.verifyTransactionSetType(segmentList, "856"));
    }
    
    @Test
    public void test_verifyTransactionSetType_wrong_type() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("ST*856*0001");
        segmentList.add(segment);
        segment = new X12Segment("BSN*00****0001");
        segmentList.add(segment);
        segment = new X12Segment("SE*1*0001");
        segmentList.add(segment);  
        
        assertFalse(X12ParsingUtil.verifyTransactionSetType(segmentList, "999"));
    }
    
    @Test
    public void test_verifyTransactionSetType_partial_header() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("ST");
        segmentList.add(segment);
        segment = new X12Segment("BSN*00****0001");
        segmentList.add(segment);
        segment = new X12Segment("SE*1*0001");
        segmentList.add(segment);  
        
        assertFalse(X12ParsingUtil.verifyTransactionSetType(segmentList, "999"));
    }
    
    @Test
    public void test_verifyTransactionSetType_wrong_first_line() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("XX*856*0001");
        segmentList.add(segment);
        segment = new X12Segment("BSN*00****0001");
        segmentList.add(segment);
        segment = new X12Segment("SE*1*0001");
        segmentList.add(segment);  
        
        assertFalse(X12ParsingUtil.verifyTransactionSetType(segmentList, "856"));
    }
    
    @Test
    public void test_verifyTransactionSetType_null_type() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("ST*856*0001");
        segmentList.add(segment);
        segment = new X12Segment("BSN*00****0001");
        segmentList.add(segment);
        segment = new X12Segment("SE*1*0001");
        segmentList.add(segment);  
        
        assertFalse(X12ParsingUtil.verifyTransactionSetType(segmentList, null));
    }
    
    @Test
    public void test_verifyTransactionSetType_null() {
        List<X12Segment> segmentList = null;
        assertFalse(X12ParsingUtil.verifyTransactionSetType(segmentList, "856"));
    }
    
    @Test
    public void test_verifyTransactionSetType_empty() {
        List<X12Segment> segmentList = Collections.emptyList();
        assertFalse(X12ParsingUtil.verifyTransactionSetType(segmentList, "856"));
    }
    
    @Test
    public void test_parseVersion() {
        assertNull(X12ParsingUtil.parseVersion(null));
        assertNull(X12ParsingUtil.parseVersion(""));
        assertEquals(new Integer(4010), X12ParsingUtil.parseVersion("004010UCS"));
        assertEquals(new Integer(5010), X12ParsingUtil.parseVersion("005010UCS"));
        assertEquals(new Integer(4010), X12ParsingUtil.parseVersion("4010"));
        assertEquals(new Integer(4010), X12ParsingUtil.parseVersion("004010"));
        assertEquals(new Integer(4010), X12ParsingUtil.parseVersion("4010UCS"));
    }

    @Test(expected = X12ParserException.class)
    public void test_parseVersion_error_text() {
        X12ParsingUtil.parseVersion("VERSION");
    }

    @Test(expected = X12ParserException.class)
    public void test_parseVersion_error_incorrect_ending() {
        X12ParsingUtil.parseVersion("004010VERSION");
    }

}
