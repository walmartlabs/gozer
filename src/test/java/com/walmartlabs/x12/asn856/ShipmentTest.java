package com.walmartlabs.x12.asn856;

import com.walmartlabs.x12.standard.X12Loop;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShipmentTest {

    @Test
    public void test_shipment_loop_check_null() {
        X12Loop loop = null;
        assertFalse(Shipment.isShipmentLoop(loop));
    }
    
    @Test
    public void test_shipment_loop_check_no_code() {
        X12Loop loop = new X12Loop();
        assertFalse(Shipment.isShipmentLoop(loop));
    }
    
    @Test
    public void test_shipment_loop_check_wrong_code() {
        X12Loop loop = new X12Loop();
        loop.setHierarchicalId("1'");
        loop.setCode("X");
        assertFalse(Shipment.isShipmentLoop(loop));
    }
    
    @Test
    public void test_shipment_loop_check() {
        X12Loop loop = new X12Loop();
        loop.setHierarchicalId("1'");
        loop.setCode("S");
        assertTrue(Shipment.isShipmentLoop(loop));
    }

}
