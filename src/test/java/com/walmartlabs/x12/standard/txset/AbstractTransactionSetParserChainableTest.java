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

package com.walmartlabs.x12.standard.txset;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.testing.util.txset.aaa.AaaChainableTransactionSetParser;
import com.walmartlabs.x12.testing.util.txset.aaa.TypeAaaTransactionSet;
import com.walmartlabs.x12.testing.util.txset.bbb.BbbChainableTransactionSetParser;
import com.walmartlabs.x12.testing.util.txset.bbb.TypeBbbTransactionSet;
import com.walmartlabs.x12.testing.util.txset.ccc.CccUnchainableTransactionSetParser;
import com.walmartlabs.x12.testing.util.txset.ccc.TypeCccTransactionSet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AbstractTransactionSetParserChainableTest {

    AbstractTransactionSetParserChainable firstParser;

    @Test
    public void test_chaining_null() {
        firstParser = new AaaChainableTransactionSetParser();
        assertFalse(firstParser.registerNextTransactionSetParser(null));

        //
        // parse AAA
        // should be successful
        //
        List<X12Segment> aaaTxSegment = this.generateTransactionSetSegments("AAA");
        X12TransactionSet txSet = firstParser.parseTransactionSet(aaaTxSegment, null);
        assertNotNull(txSet);
        assertTrue(txSet instanceof TypeAaaTransactionSet);

        //
        // parse BBB
        // should NOT be successful (not registered)
        //
        List<X12Segment> bbbTxSegment = this.generateTransactionSetSegments("BBB");
        txSet = firstParser.parseTransactionSet(bbbTxSegment, null);
        assertNull(txSet);

        //
        // parse CCC
        // should NOT be successful (not registered)
        //
        List<X12Segment> yyzTxSegment = this.generateTransactionSetSegments("YYZ");
        txSet = firstParser.parseTransactionSet(yyzTxSegment, null);
        assertNull(txSet);
    }

    @Test
    public void test_chaining_mixed_null() {
        firstParser = new AaaChainableTransactionSetParser();
        assertFalse(firstParser.registerNextTransactionSetParser(null));
        assertTrue(firstParser.registerNextTransactionSetParser(new BbbChainableTransactionSetParser()));

        //
        // parse AAA
        // should be successful
        //
        List<X12Segment> aaaTxSegment = this.generateTransactionSetSegments("AAA");
        X12TransactionSet txSet = firstParser.parseTransactionSet(aaaTxSegment, null);
        assertNotNull(txSet);
        assertTrue(txSet instanceof TypeAaaTransactionSet);

        //
        // parse BBB
        // should be successful
        //
        List<X12Segment> bbbTxSegment = this.generateTransactionSetSegments("BBB");
        txSet = firstParser.parseTransactionSet(bbbTxSegment, null);
        assertNotNull(txSet);
        assertTrue(txSet instanceof TypeBbbTransactionSet);

        //
        // parse CCC
        // should NOT be successful (not registered)
        //
        List<X12Segment> yyzTxSegment = this.generateTransactionSetSegments("CCC");
        txSet = firstParser.parseTransactionSet(yyzTxSegment, null);
        assertNull(txSet);
    }

    @Test
    public void test_chaining_last_not_chainable() {
        firstParser = new AaaChainableTransactionSetParser();
        BbbChainableTransactionSetParser bbbParser = new BbbChainableTransactionSetParser();
        CccUnchainableTransactionSetParser cccParser = new CccUnchainableTransactionSetParser();

        assertTrue(firstParser.registerNextTransactionSetParser(bbbParser));
        assertTrue(firstParser.registerNextTransactionSetParser(cccParser));

        //
        // parse AAA
        // should be successful
        //
        List<X12Segment> aaaTxSegment = this.generateTransactionSetSegments("AAA");
        X12TransactionSet txSet = firstParser.parseTransactionSet(aaaTxSegment, null);
        assertNotNull(txSet);
        assertTrue(txSet instanceof TypeAaaTransactionSet);

        //
        // parse BBB
        // should be successful
        //
        List<X12Segment> bbbTxSegment = this.generateTransactionSetSegments("BBB");
        txSet = firstParser.parseTransactionSet(bbbTxSegment, null);
        assertNotNull(txSet);
        assertTrue(txSet instanceof TypeBbbTransactionSet);

        //
        // parse CCC
        // should be successful
        //
        List<X12Segment> cccTxSegment = this.generateTransactionSetSegments("CCC");
        txSet = firstParser.parseTransactionSet(cccTxSegment, null);
        assertNotNull(txSet);
        assertTrue(txSet instanceof TypeCccTransactionSet);
    }

    @Test
    public void test_chaining_middle_not_chainable() {
        firstParser = new AaaChainableTransactionSetParser();
        BbbChainableTransactionSetParser bbbParser = new BbbChainableTransactionSetParser();
        CccUnchainableTransactionSetParser cccParser = new CccUnchainableTransactionSetParser();

        assertTrue(firstParser.registerNextTransactionSetParser(cccParser));
        assertFalse(firstParser.registerNextTransactionSetParser(bbbParser));

        //
        // parse AAA
        // should be successful
        //
        List<X12Segment> aaaTxSegment = this.generateTransactionSetSegments("AAA");
        X12TransactionSet txSet = firstParser.parseTransactionSet(aaaTxSegment, null);
        assertNotNull(txSet);
        assertTrue(txSet instanceof TypeAaaTransactionSet);

        //
        // parse BBB
        // should NOT be successful  (registered after non-chaining parser)
        //
        List<X12Segment> bbbTxSegment = this.generateTransactionSetSegments("BBB");
        txSet = firstParser.parseTransactionSet(bbbTxSegment, null);
        assertNull(txSet);

        //
        // parse CCC
        // should be successful
        //
        List<X12Segment> cccTxSegment = this.generateTransactionSetSegments("CCC");
        txSet = firstParser.parseTransactionSet(cccTxSegment, null);
        assertNotNull(txSet);
        assertTrue(txSet instanceof TypeCccTransactionSet);
    }

    protected List<X12Segment> generateTransactionSetSegments(String type) {
        List<X12Segment> list = new ArrayList<>();
        list.add(new X12Segment("ST*" + type + "*0001"));
        list.add(new X12Segment("TEST*1"));
        list.add(new X12Segment("SE*1*0001"));

        return list;
    }

}
