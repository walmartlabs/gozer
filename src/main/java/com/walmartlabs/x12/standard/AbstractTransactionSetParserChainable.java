package com.walmartlabs.x12.standard;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;

import java.util.List;

public abstract class AbstractTransactionSetParserChainable implements TransactionSetParser {

    AbstractTransactionSetParserChainable nextParser;
    
    @Override
    public X12TransactionSet parseTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group) {
        if (handlesTransactionSet(transactionSegments, x12Group)) {
            // we handle this transaction
            // so parse it
            return this.doParse(transactionSegments, x12Group);
        } else if (nextParser != null) {
            // we don't handle the transaction
            // try the next link in the chain
            return nextParser.parseTransactionSet(transactionSegments, x12Group);
        } else {
             // if we got here
            // this transaction type is not handled
            return null;
        }
    }
    
    protected abstract boolean handlesTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group);

    protected abstract X12TransactionSet doParse(List<X12Segment> transactionSegments, X12Group x12Group);
}
