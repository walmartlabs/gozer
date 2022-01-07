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
import com.walmartlabs.x12.testing.util.txset.aaa.AaaTransactionSetParser;
import com.walmartlabs.x12.testing.util.txset.aaa.TypeAaaTransactionSet;
import com.walmartlabs.x12.testing.util.txset.bbb.BbbTransactionSetParser;
import com.walmartlabs.x12.testing.util.txset.bbb.TypeBbbTransactionSet;
import com.walmartlabs.x12.testing.util.txset.yyz.TypeYyzTransactionSet;
import com.walmartlabs.x12.testing.util.txset.yyz.YyzTransactionSetParser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AbstractTransactionSetParserChainableTest {

    AbstractTransactionSetParserChainable firstParser;
    
    @Test
    public void test_chaining_null() {
        firstParser = new AaaTransactionSetParser();
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
        // should NOT be successful
        //
        List<X12Segment> bbbTxSegment = this.generateTransactionSetSegments("BBB");
        txSet = firstParser.parseTransactionSet(bbbTxSegment, null);
        assertNull(txSet);
        
        //
        // parse YYZ 
        // should NOT be successful
        //
        List<X12Segment> yyzTxSegment = this.generateTransactionSetSegments("YYZ");
        txSet = firstParser.parseTransactionSet(yyzTxSegment, null);
        assertNull(txSet);
    }
    
    @Test
    public void test_chaining_mixed_null() {
        firstParser = new AaaTransactionSetParser();
        assertFalse(firstParser.registerNextTransactionSetParser(null));
        assertTrue(firstParser.registerNextTransactionSetParser(new BbbTransactionSetParser()));
        
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
        // parse YYZ 
        // should NOT be successful
        //
        List<X12Segment> yyzTxSegment = this.generateTransactionSetSegments("YYZ");
        txSet = firstParser.parseTransactionSet(yyzTxSegment, null);
        assertNull(txSet);
    }
    
    @Test
    public void test_chaining_last_not_chainable() {
        firstParser = new AaaTransactionSetParser();
        BbbTransactionSetParser bbbParser = new BbbTransactionSetParser();
        YyzTransactionSetParser yyzParser = new YyzTransactionSetParser();
        
        assertTrue(firstParser.registerNextTransactionSetParser(bbbParser));
        assertTrue(firstParser.registerNextTransactionSetParser(yyzParser));
        
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
        // parse YYZ 
        // should be successful
        //
        List<X12Segment> yyzTxSegment = this.generateTransactionSetSegments("YYZ");
        txSet = firstParser.parseTransactionSet(yyzTxSegment, null);
        assertNotNull(txSet);
        assertTrue(txSet instanceof TypeYyzTransactionSet);
    }
    
    @Test
    public void test_chaining_middle_not_chainable() {
        firstParser = new AaaTransactionSetParser();
        BbbTransactionSetParser bbbParser = new BbbTransactionSetParser();
        YyzTransactionSetParser yyzParser = new YyzTransactionSetParser();
        
        assertTrue(firstParser.registerNextTransactionSetParser(yyzParser));
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
        // should NOT be successful
        //
        List<X12Segment> bbbTxSegment = this.generateTransactionSetSegments("BBB");
        txSet = firstParser.parseTransactionSet(bbbTxSegment, null);
        assertNull(txSet);
        
        //
        // parse YYZ 
        // should be successful
        //
        List<X12Segment> yyzTxSegment = this.generateTransactionSetSegments("YYZ");
        txSet = firstParser.parseTransactionSet(yyzTxSegment, null);
        assertNotNull(txSet);
        assertTrue(txSet instanceof TypeYyzTransactionSet);
    }
    
    protected List<X12Segment> generateTransactionSetSegments(String type) {
        List<X12Segment> list = new ArrayList<>();
        list.add(new X12Segment("ST*" + type + "*0001"));
        list.add(new X12Segment("TEST*1"));
        list.add(new X12Segment("SE*1*0001"));
        
        return list;
    }

}
