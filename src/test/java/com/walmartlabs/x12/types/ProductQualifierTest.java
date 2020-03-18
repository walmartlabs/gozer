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

import com.walmartlabs.x12.types.ProductQualifier;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProductQualifierTest {

    @Test
    public void test_valid_value() {
        ProductQualifier pq = ProductQualifier.convert("EN");
        assertEquals(ProductQualifier.EN, pq);
        assertEquals("EAN/UCC-13", pq.getDescription());
    }

    @Test
    public void test_invalid_value() {
        ProductQualifier pq = ProductQualifier.convert("BOGUS");
        assertEquals(ProductQualifier.UNKNOWN, pq);
        assertEquals("BOGUS", pq.getDescription());
    }

    @Test
    public void test_invalid_empty() {
        ProductQualifier pq = ProductQualifier.convert("");
        assertEquals(ProductQualifier.UNKNOWN, pq);
        assertEquals("", pq.getDescription());
    }

    @Test
    public void test_invalid_null() {
        assertEquals(null, ProductQualifier.convert(null));
    }

}
