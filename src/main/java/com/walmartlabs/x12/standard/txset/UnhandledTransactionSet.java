package com.walmartlabs.x12.standard.txset;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.standard.X12Group;

import java.util.List;

public interface UnhandledTransactionSet {
    
    /**
     * 
     * implementations of this interface will be passed any transaction set 
     * that didn't have a registered {@link TransactionSetParser} when a 
     * document was parsed
     * 
     * this allows users to customize what should happen 
     * with unhandled transaction sets
     * 
     * @param transactionSegments
     * @param x12Group
     */
    void  unhandledTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group);
    
}
