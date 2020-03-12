package com.walmartlabs.x12.standard.txset;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.standard.X12Group;

import java.util.List;

public interface TransactionSetParser {
    
    /**
     * parse the transaction set 
     * implementation should NOT add the transaction set to the group
     * the group is available to give the transaction context if needed
     * 
     * @param transactionSegments
     * @param x12Group
     * @return the parsed transaction set
     */
    X12TransactionSet parseTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group);
    
}
