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

package com.walmartlabs.x12.testing.util.txset.bbb;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.txset.AbstractTransactionSetParserChainable;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BbbTransactionSetParser extends AbstractTransactionSetParserChainable {

    @Override
    protected X12TransactionSet doParse(List<X12Segment> txLines, X12Group x12Group) {
        assertNotNull(txLines);
        assertEquals(3, txLines.size());
        assertEquals("ST", txLines.get(0).getIdentifier());
        assertEquals("BBB", txLines.get(0).getElement(1));
        assertEquals("TEST", txLines.get(1).getIdentifier());
        assertEquals("SE", txLines.get(2).getIdentifier());

        TypeBbbTransactionSet tx = new TypeBbbTransactionSet();
        tx.setTransactionSetIdentifierCode(txLines.get(0).getElement(1));
        tx.setValue(txLines.get(1).getElement(1));
        return tx;
    }

    @Override
    protected boolean handlesTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group) {
        boolean isHandled = false;
        if (transactionSegments != null) {
            X12Segment segment = transactionSegments.get(0);
            if (segment != null && "BBB".equals(segment.getElement(1))) {
                isHandled = true;
            }
        }
        return isHandled;
    }
}