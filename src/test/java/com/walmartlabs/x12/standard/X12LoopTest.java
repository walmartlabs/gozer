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
