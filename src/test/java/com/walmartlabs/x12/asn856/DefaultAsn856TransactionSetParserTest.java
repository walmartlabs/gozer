package com.walmartlabs.x12.asn856;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
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
        List<X12Segment> segments = Collections.EMPTY_LIST;
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
        List<X12Segment> segments = Collections.EMPTY_LIST;
        X12TransactionSet txSet = txParser.doParse(segments, x12Group);
        assertNull(txSet);
    }
    
    @Test
    public void test_doParse() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getTestSegments();
        X12TransactionSet txSet = txParser.doParse(segments, x12Group);
        assertNotNull(txSet);
    }
    
    @Test
    public void test_doParse_NoHierarchicalLoops() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getTestSegments();
        segments.remove(2); // HL(S)
        X12TransactionSet txSet = txParser.doParse(segments, x12Group);
        assertNotNull(txSet);
    }
    
    @Test
    public void test_doParse_OnlyEnvelope() {
        try {
            X12Group x12Group = new X12Group();
            List<X12Segment> segments = this.getTestSegments();
            segments.remove(2); // HL(S)
            segments.remove(1); // BSN
            txParser.doParse(segments, x12Group);
            fail("expected parsing exception");
        } catch (X12ParserException e) {
            assertEquals("expected BSN segment but found SE", e.getMessage());
        }
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
