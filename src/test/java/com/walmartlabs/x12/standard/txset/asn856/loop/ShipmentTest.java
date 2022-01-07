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

package com.walmartlabs.x12.standard.txset.asn856.loop;

import com.walmartlabs.x12.standard.X12Loop;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
