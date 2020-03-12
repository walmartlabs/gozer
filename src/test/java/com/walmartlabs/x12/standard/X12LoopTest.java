package com.walmartlabs.x12.standard;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class X12LoopTest {


    @Test
    public void test_shipment_loop_check_nulls() {
        X12Loop loop = null;
        assertFalse(X12Loop.isLoopWithCode(loop, null));
    }
    
    @Test
    public void test_shipment_loop_check_null() {
        X12Loop loop = null;
        assertFalse(X12Loop.isLoopWithCode(loop, "S"));
    }
    
    @Test
    public void test_shipment_loop_check_no_code() {
        X12Loop loop = new X12Loop();
        assertFalse(X12Loop.isLoopWithCode(loop, "S"));
    }
    
    @Test
    public void test_shipment_loop_check_wrong_code() {
        X12Loop loop = new X12Loop();
        loop.setHierarchicalId("1'");
        loop.setCode("X");
        assertFalse(X12Loop.isLoopWithCode(loop, "S"));
    }
    
    @Test
    public void test_shipment_loop_check() {
        X12Loop loop = new X12Loop();
        loop.setHierarchicalId("1'");
        loop.setCode("S");
        assertTrue(X12Loop.isLoopWithCode(loop, "S"));
    }
    
    @Test
    public void test_shipment_loop_check_noDesiredCode() {
        X12Loop loop = new X12Loop();
        loop.setHierarchicalId("1'");
        loop.setCode("S");
        assertFalse(X12Loop.isLoopWithCode(loop, null));
    }
    
    @Test
    public void test_shipment_loop_check_no_code_noDesiredCode() {
        X12Loop loop = new X12Loop();
        loop.setHierarchicalId("1'");
        loop.setCode(null);
        assertFalse(X12Loop.isLoopWithCode(loop, null));
    }
}
