package com.walmartlabs.x12.standard.txset.generic;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.X12Loop;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GenericTransactionSetParserTest {


    private GenericTransactionSetParser txParser;
    
    @Before
    public void init() {
        txParser = new GenericTransactionSetParser();
    }
    
    /**
     * the generic parser always handles the transaction set
     */
    @Test
    public void test_handlesTransactionSet() {
        X12Group x12Group = null;
        List<X12Segment> segments = null;
        assertTrue(txParser.handlesTransactionSet(segments, x12Group));
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
    public void test_doParse_OnlyTransactionEnvelope() {
        try {
            X12Group x12Group = new X12Group();
            List<X12Segment> segments = this.getSegmentsOnlyTransactionEnvelope();
            txParser.doParse(segments, x12Group);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            assertEquals("expected Beginning segment but found SE", e.getMessage());
        }
    }
    
    @Test
    public void test_doParse_NoHierarchicalLoops() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getSegmentsNoHierarchicalLoops();
        X12TransactionSet txSet = txParser.doParse(segments, x12Group);
        assertNotNull(txSet);
        
        GenericTransactionSet genericTx = (GenericTransactionSet) txSet;
        assertEquals("BSN",genericTx.getBeginningSegment().getElement(0));
        assertEquals("05755986",genericTx.getBeginningSegment().getElement(2));
        
        List<X12Segment> segmentsBeforeLoop = genericTx.getSegmentsBeforeLoops();
        assertNull(segmentsBeforeLoop);
        
        List<X12Loop> loops = genericTx.getLoops();
        assertNull(loops);
    }
    
    @Test
    public void test_doParse_TwoTopLevelHierarchicalLoops() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getTwoShipmentLoops();
        X12TransactionSet txSet = txParser.doParse(segments, x12Group);
        assertNotNull(txSet);
        
        GenericTransactionSet genericTx = (GenericTransactionSet) txSet;
        assertEquals("BSN",genericTx.getBeginningSegment().getElement(0));
        assertEquals("05755986",genericTx.getBeginningSegment().getElement(2));
        
        List<X12Segment> segmentsBeforeLoop = genericTx.getSegmentsBeforeLoops();
        assertNull(segmentsBeforeLoop);
        
        List<X12Loop> loops = genericTx.getLoops();
        assertNotNull(loops);
        assertEquals(2, loops.size());
    }
    
    @Test
    public void test_doParse_UnexpectedSegmentBeforeHierarchicalLoops() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getSegmentBeforeHierarchicalLoops();
        X12TransactionSet txSet = txParser.doParse(segments, x12Group);
        assertNotNull(txSet);
        
        GenericTransactionSet genericTx = (GenericTransactionSet) txSet;
        assertEquals("BSN",genericTx.getBeginningSegment().getElement(0));
        assertEquals("05755986",genericTx.getBeginningSegment().getElement(2));
        
        List<X12Segment> segmentsBeforeLoop = genericTx.getSegmentsBeforeLoops();
        assertNotNull(segmentsBeforeLoop);
        assertEquals(2, segmentsBeforeLoop.size());
        
        assertEquals("DTM", segmentsBeforeLoop.get(0).getIdentifier());
        assertEquals("REF", segmentsBeforeLoop.get(1).getIdentifier());
        
        List<X12Loop> loops = genericTx.getLoops();
        assertNotNull(loops);
        assertEquals(1, loops.size());
    }
    
    @Test
    public void test_doParse() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getTestSegments();
        X12TransactionSet txSet = txParser.doParse(segments, x12Group);
        assertNotNull(txSet);
        
        GenericTransactionSet genericTx = (GenericTransactionSet) txSet;
        assertEquals("BSN",genericTx.getBeginningSegment().getElement(0));
        assertEquals("05755986",genericTx.getBeginningSegment().getElement(2));
        List<X12Segment> segmentsBeforeLoop = genericTx.getSegmentsBeforeLoops();
        assertNull(segmentsBeforeLoop);
    }
    
    private List<X12Segment> getSegmentsOnlyTransactionEnvelope() {
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
    
    private List<X12Segment> getSegmentBeforeHierarchicalLoops() {
        List<X12Segment> txSegments = new ArrayList<>();
        
        txSegments.add(new X12Segment("ST*856*368090001"));
        txSegments.add(new X12Segment("BSN*00*05755986*20190523*171543*0002"));
        txSegments.add(new X12Segment("DTM*067*20210323"));
        txSegments.add(new X12Segment("REF*ZZ*420554090"));
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
