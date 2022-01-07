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

package com.walmartlabs.x12.testing.util.txset.yyz;

import com.walmartlabs.x12.SegmentIterator;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.txset.TransactionSetParser;
import com.walmartlabs.x12.util.loop.X12LoopHolder;
import com.walmartlabs.x12.util.loop.X12LoopUtil;

import java.util.List;

/**
 * handles a YYZ transaction set
 * 
 * ST*YYZ*0099
 * RUSH*99
 * HL*1**P
 * PARENT*99
 * HL*2*1*C
 * CHILD*1
 * HL*3*1*C
 * CHILD*2
 * SE*1*0099
 *
 */
public class YyzTransactionSetParser implements TransactionSetParser {

    public static final String YYZ_TRANSACTION_TYPE = "YYZ";
    public static final String YYZ_TRANSACTION_HEADER = "RUSH";
    
    @Override
    public X12TransactionSet parseTransactionSet(List<X12Segment> txLines, X12Group x12Group) {
        TypeYyzTransactionSet tx = null;
        if (txLines != null && !txLines.isEmpty()) {
            SegmentIterator txSegmentIterator = new SegmentIterator(txLines);
            
            // check first segment
            X12Segment txSegment = txSegmentIterator.next();
            String type = txSegment.getElement(1);
            
            if (YYZ_TRANSACTION_TYPE.equals(type)) {
                tx = new TypeYyzTransactionSet();
                tx.setTransactionSetIdentifierCode(type);
                
                this.handleSegments(txSegmentIterator, tx);
            } else {
                // ignore the transaction set
                // maybe a different implementation
                // will be able to handle it
            }
        }
        return tx;
    }
    
    protected void handleSegments(SegmentIterator txSegments, TypeYyzTransactionSet yyzTx) {
        
        while (txSegments.hasNext()) {
            X12Segment nextLine = txSegments.next();
            
            //
            // quick and dirty parser for YYZ transaction set
            //
            if ("RUSH".equals(nextLine.getIdentifier())) {
                // handle RUSH*NNN
                yyzTx.setRushValue(nextLine.getElement(1));
            } else if (X12LoopUtil.isHierarchicalLoopStart(nextLine)) {
                // handle loops
                
                // include first HL 
                txSegments.previous();
                
                // exclude the last line (SE)
                List<X12Segment> loopSegments = txSegments.subList(txSegments.currentIndex(), txSegments.lastIndex());
                X12LoopHolder loopHolder = X12LoopUtil.organizeHierarchicalLoops(loopSegments);
                
                // add (unparsed) loops
                yyzTx.setLoops(loopHolder.getLoops());
                
                // add loop errors to tx (if any)
                List<X12ErrorDetail> loopErrors = loopHolder.getLoopErrors();
                //yyzTx.setLoopingValid(CollectionUtils.isEmpty(loopErrors));
                yyzTx.setLoopingErrors(loopHolder.getLoopErrors());
            }
        }
    }

}