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

package com.walmartlabs.x12.util.loop;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
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

public class X12LoopUtilTest {

    @Test
    public void test_isHierarchicalLoopStart() {
        assertFalse(X12LoopUtil.isHierarchicalLoopStart(null));
        assertFalse(X12LoopUtil.isHierarchicalLoopStart(new X12Segment("REF*ZZ*123")));
        assertTrue(X12LoopUtil.isHierarchicalLoopStart(new X12Segment("HL*1**S")));
    }

    @Test
    public void test_findHierarchicalLoops_null() {
        List<X12Segment> segmentList = null;
        X12LoopHolder loopHolder = X12LoopUtil.organizeHierarchicalLoops(segmentList);
        assertNotNull(loopHolder);

        // loops
        List<X12Loop> loops = loopHolder.getLoops();
        assertNull(loops);
        // errors
        List<X12ErrorDetail> loopErrors = loopHolder.getLoopErrors();
        assertNull(loopErrors);
    }

    @Test
    public void test_findHierarchicalLoops_empty() {
        List<X12Segment> segmentList = Collections.emptyList();
        X12LoopHolder loopHolder = X12LoopUtil.organizeHierarchicalLoops(segmentList);
        assertNotNull(loopHolder);

        // loops
        List<X12Loop> loops = loopHolder.getLoops();
        assertNull(loops);
        // errors
        List<X12ErrorDetail> loopErrors = loopHolder.getLoopErrors();
        assertNull(loopErrors);
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

        X12LoopHolder loopHolder = X12LoopUtil.organizeHierarchicalLoops(segmentList);
        assertNotNull(loopHolder);

        // loops
        List<X12Loop> loops = loopHolder.getLoops();
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
        assertEquals("DTM", segmentsInLoop.get(0).getIdentifier());
        assertEquals("TD3", segmentsInLoop.get(1).getIdentifier());

        List<X12Loop> childLoops = loop.getChildLoops();
        assertNull(childLoops);


        // errors
        List<X12ErrorDetail> loopErrors = loopHolder.getLoopErrors();
        assertNull(loopErrors);
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

        X12LoopHolder loopHolder = X12LoopUtil.organizeHierarchicalLoops(segmentList);
        assertNotNull(loopHolder);

        // loops
        List<X12Loop> loops = loopHolder.getLoops();
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
        assertEquals("DTM", segmentsInShipmentLoop.get(0).getIdentifier());
        assertEquals("TD3", segmentsInShipmentLoop.get(1).getIdentifier());

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
        assertEquals("PRF", segmentsInOrderLoop.get(0).getIdentifier());
        assertEquals("222", segmentsInOrderLoop.get(0).getElement(1));
        assertEquals("REF", segmentsInOrderLoop.get(1).getIdentifier());

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
        assertEquals("PRF", segmentsInOrderLoop.get(0).getIdentifier());
        assertEquals("333", segmentsInOrderLoop.get(0).getElement(1));
        assertEquals("REF", segmentsInOrderLoop.get(1).getIdentifier());

        List<X12Loop> childrenOfOrderLoop = orderLoop.getChildLoops();
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
        assertEquals("MAN", segmentsInPackLoop.get(0).getIdentifier());

        assertNull(packLoop.getChildLoops());

        // errors
        List<X12ErrorDetail> loopErrors = loopHolder.getLoopErrors();
        assertNull(loopErrors);
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
        // pack 1 on order 2 which does not exist
        segment = new X12Segment("HL*4*3*P");
        segmentList.add(segment);
        segment = new X12Segment("MAN*GM*56");
        segmentList.add(segment);

        X12LoopHolder loopHolder = X12LoopUtil.organizeHierarchicalLoops(segmentList);
        assertNotNull(loopHolder);

        // loops
        List<X12Loop> loops = loopHolder.getLoops();
        assertNotNull(loops);
        assertEquals(1, loops.size());

        // shipment loop
        X12Loop shipmentLoop = loops.get(0);
        assertNotNull(shipmentLoop);
        assertEquals("1", shipmentLoop.getHierarchicalId());
        assertEquals(null, shipmentLoop.getParentHierarchicalId());
        assertEquals("S", shipmentLoop.getCode());

        List<X12Loop> childrenOfShipmentLoop = shipmentLoop.getChildLoops();
        assertNotNull(childrenOfShipmentLoop);
        assertEquals(1, childrenOfShipmentLoop.size());

        // order loop
        X12Loop orderLoop = childrenOfShipmentLoop.get(0);
        assertNotNull(orderLoop);
        assertEquals("2", orderLoop.getHierarchicalId());
        assertEquals("1", orderLoop.getParentHierarchicalId());
        assertEquals("O", orderLoop.getCode());

        // no pack loop
        assertNull(orderLoop.getChildLoops());

        // errors
        List<X12ErrorDetail> loopErrors = loopHolder.getLoopErrors();
        assertNotNull(loopErrors);
        assertEquals(1, loopErrors.size());
        X12ErrorDetail loopError = loopErrors.get(0);
        assertEquals("HL", loopError.getSegmentId());
        assertNull(loopError.getElementId());
        assertNull(loopError.getLineNumber());
        assertEquals("HL segment with id (4) is missing parent (3)", loopError.getMessage());
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

        X12LoopHolder loopHolder = X12LoopUtil.organizeHierarchicalLoops(segmentList);
        assertNotNull(loopHolder);

        // loops
        List<X12Loop> loops = loopHolder.getLoops();
        assertNotNull(loops);
        assertEquals(1, loops.size());

        // shipment loop
        X12Loop shipmentLoop = loops.get(0);
        assertNotNull(shipmentLoop);
        assertEquals("1", shipmentLoop.getHierarchicalId());
        assertEquals(null, shipmentLoop.getParentHierarchicalId());
        assertEquals("S", shipmentLoop.getCode());

        List<X12Loop> childrenOfShipmentLoop = shipmentLoop.getChildLoops();
        assertNotNull(childrenOfShipmentLoop);
        assertEquals(1, childrenOfShipmentLoop.size());

        // order loop
        X12Loop orderLoop = childrenOfShipmentLoop.get(0);
        assertNotNull(orderLoop);
        assertEquals("2", orderLoop.getHierarchicalId());
        assertEquals("1", orderLoop.getParentHierarchicalId());
        assertEquals("O", orderLoop.getCode());

        List<X12Loop> childrenOfOrdertLoop = orderLoop.getChildLoops();
        assertNotNull(childrenOfOrdertLoop);
        assertEquals(1, childrenOfOrdertLoop.size());

        // pack loop
        X12Loop packLoop = childrenOfOrdertLoop.get(0);
        assertNotNull(packLoop);
        assertEquals("2", packLoop.getHierarchicalId());
        assertEquals("2", packLoop.getParentHierarchicalId());
        assertEquals("P", packLoop.getCode());

        assertNull(packLoop.getChildLoops());

        // errors
        List<X12ErrorDetail> loopErrors = loopHolder.getLoopErrors();
        assertNotNull(loopErrors);
        assertEquals(1, loopErrors.size());
        X12ErrorDetail loopError = loopErrors.get(0);
        assertEquals("HL", loopError.getSegmentId());
        assertNull(loopError.getElementId());
        assertNull(loopError.getLineNumber());
        assertEquals("HL segment with id (2) already exists", loopError.getMessage());
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

        X12LoopHolder loopHolder = X12LoopUtil.organizeHierarchicalLoops(segmentList);
        assertNotNull(loopHolder);

        // loops
        List<X12Loop> loops = loopHolder.getLoops();
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
        assertEquals("DTM", segmentsInLoop.get(0).getIdentifier());
        assertEquals("TD3", segmentsInLoop.get(1).getIdentifier());
        assertEquals("A", segmentsInLoop.get(1).getElement(1));

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
        assertEquals("TD3", segmentsInLoop.get(0).getIdentifier());
        assertEquals("B", segmentsInLoop.get(0).getElement(1));

        childLoops = loop.getChildLoops();
        assertNull(childLoops);

        // errors
        List<X12ErrorDetail> loopErrors = loopHolder.getLoopErrors();
        assertNull(loopErrors);
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

        X12LoopHolder loopHolder = X12LoopUtil.organizeHierarchicalLoops(segmentList);
        assertNotNull(loopHolder);

        // loops
        List<X12Loop> loops = loopHolder.getLoops();
        assertNull(loops);

        // errors
        List<X12ErrorDetail> loopErrors = loopHolder.getLoopErrors();
        assertNotNull(loopErrors);
        assertEquals(1, loopErrors.size());
        X12ErrorDetail loopError = loopErrors.get(0);
        assertEquals("TOP", loopError.getSegmentId());
        assertNull(loopError.getElementId());
        assertNull(loopError.getLineNumber());
        assertEquals("expected HL segment but found TOP", loopError.getMessage());
    }

}
