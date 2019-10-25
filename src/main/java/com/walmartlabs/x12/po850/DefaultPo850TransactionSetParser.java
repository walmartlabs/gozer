package com.walmartlabs.x12.po850;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.standard.AbstractTransactionSetParserChainable;
import com.walmartlabs.x12.standard.X12Group;

import java.util.List;

public class DefaultPo850TransactionSetParser extends AbstractTransactionSetParserChainable {

    @Override
    protected boolean handlesTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected X12TransactionSet doParse(List<X12Segment> transactionSegments, X12Group x12Group) {
        // TODO Auto-generated method stub
        return null;
    }

}
