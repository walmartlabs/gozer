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
import com.walmartlabs.x12.common.segment.DTMDateTimeReference;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.X12Group;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class DefaultAsn856TransactionSetParserTest {

    private DefaultAsn856TransactionSetParser txParser;
    
    @Before
    public void init() {
        txParser = new DefaultAsn856TransactionSetParser();
    }
    
    @Test
    public void test_handlesTransactionSet() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getTestSegments();
        assertTrue(txParser.handlesTransactionSet(segments, x12Group));
    }
    
    @Test
    public void test_handlesTransactionSet_fails_invalid_envelope() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getTestSegments();
        // remove the last segment (SE)
        segments.remove(segments.size() - 1);
        assertFalse(txParser.handlesTransactionSet(segments, x12Group));
    }
    
    @Test
    public void test_handlesTransactionSet_OnlyEnvelope() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getTestSegments();
        segments.remove(2); // remove HL(S)
        segments.remove(1); // remove BSN
        assertTrue(txParser.handlesTransactionSet(segments, x12Group));
    }
    
    @Test
    public void test_handlesTransactionSet_empty() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = Collections.emptyList();
        assertFalse(txParser.handlesTransactionSet(segments, x12Group));
    }
    
    @Test
    public void test_handlesTransactionSet_null() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = null;
        assertFalse(txParser.handlesTransactionSet(segments, x12Group));
    }
    
    @Test
    public void test_doParse_null() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = null;
        X12TransactionSet txSet = txParser.doParse(segments, x12Group);
        assertNull(txSet);
    }
    
    @Test
    public void test_doParse_empty() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = Collections.emptyList();
        X12TransactionSet txSet = txParser.doParse(segments, x12Group);
        assertNull(txSet);
    }
    
    @Test
    public void test_doParse_OnlyEnvelope() {
        try {
            X12Group x12Group = new X12Group();
            List<X12Segment> segments = this.getSegmentsOnlyEnvelope();
            txParser.doParse(segments, x12Group);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            assertEquals("expected BSN segment but found SE", e.getMessage());
        }
    }
    
    @Test
    public void test_doParse_NoHierarchicalLoops() {
        try {
            X12Group x12Group = new X12Group();
            List<X12Segment> segments = this.getSegmentsNoHierarchicalLoops();
            txParser.doParse(segments, x12Group);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            assertEquals("expected HL segment but found SE", e.getMessage());
        }
    }
    
    @Test
    public void test_doParse_TwoTopLevelHierarchicalLoops() {
        try {
            X12Group x12Group = new X12Group();
            List<X12Segment> segments = this.getTwoShipmentLoops();
            txParser.doParse(segments, x12Group);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            assertEquals("expected one top level HL", e.getMessage());
        }
    }
    
    @Test
    public void test_doParse_UnexpectedSegmentBeforeHierarchicalLoops() {
        try {
            X12Group x12Group = new X12Group();
            List<X12Segment> segments = this.getUnexpectedSegmentBeforeHierarchicalLoops();
            txParser.doParse(segments, x12Group);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            assertEquals("expected HL segment but found REF", e.getMessage());
        }
    }
    
    @Test
    public void test_doParse_DTM_BeforeFirstLoop() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getDTMBeforeFirstLoopSegments();
        txParser.doParse(segments, x12Group);
        X12TransactionSet txSet = txParser.doParse(segments, x12Group);
        assertNotNull(txSet);
        
        AsnTransactionSet asnTx = (AsnTransactionSet) txSet;
        assertEquals("05755986", asnTx.getShipmentIdentification());
        List<DTMDateTimeReference> dtms = asnTx.getDtmReferences();
        assertEquals(2, dtms.size());
    }

    @Test
    public void test_doParse() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getTestSegments();
        X12TransactionSet txSet = txParser.doParse(segments, x12Group);
        assertNotNull(txSet);
        
        AsnTransactionSet asnTx = (AsnTransactionSet) txSet;
        assertEquals("05755986", asnTx.getShipmentIdentification());
        List<DTMDateTimeReference> dtms = asnTx.getDtmReferences();
        assertNull(dtms);
    }
    
    private List<X12Segment> getSegmentsOnlyEnvelope() {
        List<X12Segment> txSegments = new ArrayList<>();
        
        txSegments.add(new X12Segment("ST*856*368090001"));
        txSegments.add(new X12Segment("SE*296*368090001"));
        
        return txSegments;
    }
    
    private List<X12Segment> getSegmentsNoHierarchicalLoops() {
        List<X12Segment> txSegments = new ArrayList<>();
        
        txSegments.add(new X12Segment("ST*856*368090001"));
        txSegments.add(new X12Segment("BSN*00*05755986*20190523*171543*0002"));
        txSegments.add(new X12Segment("SE*296*368090001"));
        
        return txSegments;
    }
    
    private List<X12Segment> getTwoShipmentLoops() {
        List<X12Segment> txSegments = new ArrayList<>();
        
        txSegments.add(new X12Segment("ST*856*368090001"));
        txSegments.add(new X12Segment("BSN*00*05755986*20190523*171543*0002"));
        txSegments.add(new X12Segment("HL*1**S"));
        txSegments.add(new X12Segment("HL*2**S"));
        txSegments.add(new X12Segment("SE*296*368090001"));
        
        return txSegments;
    }
    
    private List<X12Segment> getUnexpectedSegmentBeforeHierarchicalLoops() {
        List<X12Segment> txSegments = new ArrayList<>();
        
        txSegments.add(new X12Segment("ST*856*368090001"));
        txSegments.add(new X12Segment("BSN*00*05755986*20190523*171543*0002"));
        txSegments.add(new X12Segment("DTM*067*20210323"));
        txSegments.add(new X12Segment("REF*ZZ*420554090"));
        txSegments.add(new X12Segment("HL*1**S"));
        txSegments.add(new X12Segment("SE*296*368090001"));
        
        return txSegments;
    }
    
    private List<X12Segment> getDTMBeforeFirstLoopSegments() {
        List<X12Segment> txSegments = new ArrayList<>();
        
        txSegments.add(new X12Segment("ST*856*368090001"));
        txSegments.add(new X12Segment("BSN*00*05755986*20190523*171543*0002"));
        txSegments.add(new X12Segment("DTM*011*20210323"));
        txSegments.add(new X12Segment("DTM*067*20210323"));
        txSegments.add(new X12Segment("HL*1**S"));
        txSegments.add(new X12Segment("SE*296*368090001"));
        
        return txSegments;
    }
    
    private List<X12Segment> getTestSegments() {
        List<X12Segment> txSegments = new ArrayList<>();
        
        txSegments.add(new X12Segment("ST*856*368090001"));
        txSegments.add(new X12Segment("BSN*00*05755986*20190523*171543*0002"));
        txSegments.add(new X12Segment("HL*1**S"));
        txSegments.add(new X12Segment("SE*296*368090001"));
        
        return txSegments;
    }
    
}
