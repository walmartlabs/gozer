package com.walmartlabs.x12.standard;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;

import java.util.List;

public interface TransactionSetParser {
    
    X12TransactionSet parseTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group);
    
}
