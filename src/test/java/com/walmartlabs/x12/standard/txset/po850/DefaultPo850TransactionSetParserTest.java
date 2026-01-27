package com.walmartlabs.x12.standard.txset.po850;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.standard.X12Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultPo850TransactionSetParserTest {


    private DefaultPo850TransactionSetParser txParser;

    @BeforeEach
    public void init() {
        txParser = new DefaultPo850TransactionSetParser();
    }

    @Test
    public void test_handlesTransactionSet() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getTransactionSetSegments();
        assertTrue(txParser.handlesTransactionSet(segments, x12Group));
    }

    @Test
    public void test_handlesTransactionSet_fails_invalid_envelope() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getTransactionSetSegments();
        // remove the last segment (SE)
        segments.remove(segments.size() - 1);
        assertFalse(txParser.handlesTransactionSet(segments, x12Group));
    }

    @Test
    public void test_handlesTransactionSet_OnlyEnvelope() {
        X12Group x12Group = new X12Group();
        List<X12Segment> segments = this.getTransactionSetEnvelopeOnly();
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

    private List<X12Segment> getTransactionSetEnvelopeOnly() {
        List<X12Segment> txSegments = new ArrayList<>();

        txSegments.add(new X12Segment("ST*850*000000010"));
        txSegments.add(new X12Segment("SE*33*000000010"));

        return txSegments;
    }

    private List<X12Segment> getTransactionSetSegments() {
        List<X12Segment> txSegments = new ArrayList<>();

        //
        // PO 850
        txSegments.add(new X12Segment("ST*850*000000010"));
        txSegments.add(new X12Segment("BEG*00*SA*08292233294**20101127*610385385"));

        // more to come
        
        //
        // SE
        //
        txSegments.add(new X12Segment("SE*33*000000010"));

        return txSegments;
    }
}