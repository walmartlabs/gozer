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
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.util.X12ParsingUtil;
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
    
    public static final String ASN_TRANSACTION_TYPE = "856";
    public static final String ASN_TRANSACTION_HEADER = "BSN";

    protected boolean handlesTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group) {
        // the first segment should be an ST with the 856 transaction type code
        return X12ParsingUtil.verifyTransactionSetType(transactionSegments, ASN_TRANSACTION_TYPE);
    }

    protected X12TransactionSet doParse(List<X12Segment> transactionSegments, X12Group x12Group) {
        AsnTransactionSet asnTx = null;

        if (transactionSegments != null) {
            // MUST be wrapped in a valid transaction set envelope
            if (X12ParsingUtil.isValidEnvelope(transactionSegments, 
                X12TransactionSet.TRANSACTION_SET_HEADER,
                X12TransactionSet.TRANSACTION_SET_TRAILER)) {

                // TODO: make sure first segment is a BSN

                asnTx = new AsnTransactionSet();
                asnTx.setSampleAsnOnly("TEST");
            }
        }
        
        return asnTx;
    }

}
