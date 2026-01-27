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

package com.walmartlabs.x12.testing.util.txset.ccc;

import com.walmartlabs.x12.SegmentIterator;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.txset.AbstractTransactionSetParserChainable;
import com.walmartlabs.x12.standard.txset.AbstractTransactionSetParserChainableTest;
import com.walmartlabs.x12.standard.txset.TransactionSetParser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * is NOT extending {@link AbstractTransactionSetParserChainable}
 * see the {@link AbstractTransactionSetParserChainableTest}
 * 
 */
public class CccUnchainableTransactionSetParser implements TransactionSetParser {

    @Override
    public X12TransactionSet parseTransactionSet(List<X12Segment> txLines, X12Group x12Group) {
        // basic implementation w/o abstract class
        TypeCccTransactionSet tx = null;
        if (txLines != null && !txLines.isEmpty()) {
            SegmentIterator txSegmentIterator = new SegmentIterator(txLines);

            // check first segment
            X12Segment txSegment = txSegmentIterator.next();
            String type = txSegment.getElement(1);

            if ("CCC".equals(type)) {
                tx = new TypeCccTransactionSet();
                tx.setTransactionSetIdentifierCode(type);
                tx.setTheCccOnlyValue(txLines.get(1).getElement(1));
                
                this.doAssert(txLines);
            } else {
                // ignore the transaction set
                // maybe a different implementation
                // will be able to handle it
            }
        }
        return tx;
    }

    private void doAssert(List<X12Segment> txLines) {
        assertNotNull(txLines);
        assertEquals(3, txLines.size());
        assertEquals("ST", txLines.get(0).getIdentifier());
        assertEquals("CCC", txLines.get(0).getElement(1));
        assertEquals("TEST", txLines.get(1).getIdentifier());
        assertEquals("SE", txLines.get(2).getIdentifier());
    }

}