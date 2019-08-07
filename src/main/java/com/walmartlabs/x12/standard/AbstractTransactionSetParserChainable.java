package com.walmartlabs.x12.standard;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;

import java.util.List;

public abstract class AbstractTransactionSetParserChainable implements TransactionSetParser {

    TransactionSetParser nextParser;
    
    /**
     * chainable implementation of the {@link TransactionSetParser} interface
     * 
     * if this implementation does not handle the transaction set it will 
     * pass it on to the next parser in the chain
     */
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
    
    /**
     * convenience method that will allow one or more {@link TransactionSetParser} 
     * to be registered w/ the parser
     * 
     * Note: if a transaction set type does not have a registered parser it is ignored
     * 
     * @param transactionParser
     * @return true if non-null and added, otherwise false
     */
    public boolean registerNextTransactionSetParser(TransactionSetParser txParser) {
        boolean isAdded = false;
        
        if (txParser != null) {
            if (this.nextParser == null) {
                // we don't have a next parser
                // so we will register it 
                isAdded = true;
                this.nextParser = txParser;
            } else if (this.nextParser instanceof AbstractTransactionSetParserChainable) {
                // we have a next parser already
                // try to add this to the end of the chain
                isAdded = ((AbstractTransactionSetParserChainable) this.nextParser)
                    .registerNextTransactionSetParser(txParser);
            }
        }
        
        return isAdded;
    }
    
    /**
     * determines whether the implementation can parse the transaction set (or not).
     * 
     * @param transactionSegments
     * @param x12Group
     * @return true if responsible for parsing transaction, otherwise false
     */
    protected abstract boolean handlesTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group);

    /**
     * parse the transaction set 
     * 
     * @param transactionSegments
     * @param x12Group
     * @return the parsed transaction set
     */
    protected abstract X12TransactionSet doParse(List<X12Segment> transactionSegments, X12Group x12Group);
}
