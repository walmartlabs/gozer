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

package com.walmartlabs.x12.standard.txset;

import com.walmartlabs.x12.X12ParsingUtil;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.util.ConversionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractTransactionSetParserChainable implements TransactionSetParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractTransactionSetParserChainable.class);

    private TransactionSetParser nextParser;
    
    /**
     * chainable implementation of the {@link TransactionSetParser} interface
     * 
     * if this implementation does not handle the transaction set it will 
     * pass it on to the next parser in the chain
     */
    @Override
    public X12TransactionSet parseTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group) {
        if (this.handlesTransactionSet(transactionSegments, x12Group)) {
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
    
    /**
     * parse the ST segment (reusable from concrete class)
     * @param segment
     * @param transactionSet
     * @throws X12ParserException if segment is not ST (unexpected)
     */
    protected void parseTransactionSetHeader(X12Segment segment, X12TransactionSet transactionSet) {
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (X12TransactionSet.TRANSACTION_SET_HEADER.equals(segmentIdentifier)) {
            transactionSet.setTransactionSetIdentifierCode(segment.getElement(1));
            transactionSet.setHeaderControlNumber(segment.getElement(2));
        } else {
            throw X12ParsingUtil.handleUnexpectedSegment(X12TransactionSet.TRANSACTION_SET_HEADER, segmentIdentifier);
        }
    }
    
    /**
     * parse the SE segment (reusable from concrete class)
     * @param segment
     * @param transactionSet
     * @throws X12ParserException if segment is not SE
     */
    protected void parseTransactionSetTrailer(X12Segment segment, X12TransactionSet transactionSet) {
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (X12TransactionSet.TRANSACTION_SET_TRAILER.equals(segmentIdentifier)) {
            transactionSet.setExpectedNumberOfSegments(ConversionUtil.convertStringToInteger(segment.getElement(1)));
            transactionSet.setTrailerControlNumber(segment.getElement(2));
        } else {
            throw X12ParsingUtil.handleUnexpectedSegment(X12TransactionSet.TRANSACTION_SET_TRAILER, segmentIdentifier);
        }
    }
}
