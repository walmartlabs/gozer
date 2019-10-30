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
package com.walmartlabs.x12.types;

import org.junit.Test;

import static org.junit.Assert.*;

public class WeightQualifierTest {

    @Test
    public void test_valid_value() {
        WeightQualifier wq = WeightQualifier.convert("N");
        assertEquals(WeightQualifier.N, wq);
        assertEquals("ACTUAL_NET_WEIGHT", wq.getDescription());
    }

    @Test
    public void test_invalid_value() {
        WeightQualifier wq = WeightQualifier.convert("BOGUS");
        assertEquals(WeightQualifier.UNKNOWN, wq);
        assertEquals("BOGUS", wq.getDescription());
    }

    @Test
    public void test_invalid_empty() {
        WeightQualifier wq = WeightQualifier.convert("");
        assertEquals(WeightQualifier.UNKNOWN, wq);
        assertEquals("", wq.getDescription());
    }

    @Test
    public void test_invalid_null() {
        assertEquals(null, UnitMeasure.convert(null));
    }

}
