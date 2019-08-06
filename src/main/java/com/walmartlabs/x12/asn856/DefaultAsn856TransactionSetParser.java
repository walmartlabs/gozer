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
package com.walmartlabs.x12.asn856;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.standard.AbstractTransactionSetParserChainable;
import com.walmartlabs.x12.standard.TransactionSetParser;
import com.walmartlabs.x12.standard.X12Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * ASN 856 is the Advance Shipping Notice Used to communicate the contents of a
 * shipment prior to arriving at the facility where the contents will be
 * delivered.
 *
 */
public class DefaultAsn856TransactionSetParser extends AbstractTransactionSetParserChainable {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAsn856TransactionSetParser.class);

    protected boolean handlesTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group) {
        // TODO implement me
        return true;
    }

    protected X12TransactionSet doParse(List<X12Segment> transactionSegments, X12Group x12Group) {
        AsnTransactionSet asnTx = new AsnTransactionSet();
        asnTx.setSampleAsnOnly("TEST");
        x12Group.addTransactionSet(asnTx);
        return asnTx;
    }

}
