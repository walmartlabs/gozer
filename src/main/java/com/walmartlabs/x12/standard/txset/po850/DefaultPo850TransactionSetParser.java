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

package com.walmartlabs.x12.standard.txset.po850;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.txset.AbstractTransactionSetParserChainable;
import com.walmartlabs.x12.util.X12ParsingUtil;

import java.util.List;

public class DefaultPo850TransactionSetParser extends AbstractTransactionSetParserChainable {

    public static final String PO_TRANSACTION_TYPE = "850";
    public static final String PO_TRANSACTION_HEADER = "BEG";

    @Override
    protected boolean handlesTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group) {
        // the first segment should be an ST with the 850 transaction type code
        // Gozer is NOT enforcing the rule that the transaction set should be in a group
        // with a PO functional id code
        return X12ParsingUtil.verifyTransactionSetType(transactionSegments, PO_TRANSACTION_TYPE);
    }

    @Override
    protected X12TransactionSet doParse(List<X12Segment> transactionSegments, X12Group x12Group) {
        // TODO Auto-generated method stub
        return null;
    }

}
