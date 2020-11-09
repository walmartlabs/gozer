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

package com.walmartlabs.x12.util.split;

import com.walmartlabs.x12.SegmentIterator;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.rule.X12Rule;
import com.walmartlabs.x12.standard.StandardX12Parser;
import com.walmartlabs.x12.util.SourceToSegmentUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * Given an EDI message 
 * Break it up into multiple messages based on the ST/SE transaction
 * 
 * This has bare-bones validation
 * 
 * Each transaction will have the following:
 * - the ISA header
 * - the GS header it is part of
 * - the ST/SE transaction
 * - the GE trailer it is part of
 * - the ISE trailer
 *
 * Note: no modifications will be made to any of the segments
 * after it is split. This could impact some validations.
 * 
 * Anything that needs to be verified on the entire EDI
 * message should be registered as an {@link X12Rule} in the splitter.
 * 
 */
public class X12TransactionSplitter {
    
    private static final String EOL = "\r\n";
    
    private List<X12Rule> rules;
    
    /**
     * split the EDI message (raw file)
     * so that each transaction 
     * is a separate "document" w/ the original 
     * ISA and group headers and trailers
     * 
     * @param sourceData
     * @return the list of EDI transactions
     * @throws X12ParserException
     */
    public List<String>  split(String sourceData) {
        if (StringUtils.isEmpty(sourceData)) {
            return Collections.emptyList();
        } else {
            List<X12Segment> segmentList = SourceToSegmentUtil.splitSourceDataIntoSegments(sourceData.trim());
            return this.split(segmentList);
        }
    }
    
    /**
     * split the EDI message (list of X12Segments)
     * so that each transaction 
     * is a separate "document" w/ the original 
     * ISA and group headers and trailers
     * 
     * @param segmentList 
     * @return the list of EDI transactions
     * @throws X12ParserException
     */
    public List<String> split(List<X12Segment> segmentList) {
        if (CollectionUtils.isEmpty(segmentList)) {
            return Collections.emptyList();
        } else {
            // do some basic validation
            // on the entire EDI message
            // using X12Rule set provided
            this.runRules(segmentList);
            
            // parse and split
            // the EDI message
            List<TransactionHolder> transactionHolders = this.doParse(segmentList);
            return this.doSplit(transactionHolders);
        }
    }
    
    /**
     * add an {@link X12Rule} to the splitter
     * @param rule
     */
    public void registerX12Rule(X12Rule rule) {
        if (this.rules == null) {
            this.rules = new ArrayList<>();
        }
        
        this.rules.add(rule);
    }
    
    /**
     * remove all of the rules
     */
    public void resetX12Rules() {
        rules = null;
    }
    
    private List<TransactionHolder> doParse(List<X12Segment> segmentList) {
        List<TransactionHolder> transactionHolders = new ArrayList<>();
        
        X12Segment isaHeader = null;
        X12Segment iseTrailer = null;
        
        SegmentIterator segments = new SegmentIterator(segmentList);

        X12Segment currentSegment = segments.next();
        String currentSegmentId = currentSegment.getIdentifier();
        
        //
        // first segment better be ISA
        //
        if (StandardX12Parser.ENVELOPE_HEADER_ID.equals(currentSegmentId)) {
            isaHeader = currentSegment;
        } else {
            // error - should be start of ISA envelope
            this.throwParserException(StandardX12Parser.ENVELOPE_HEADER_ID, currentSegmentId);
        }
        
        //
        // last segment better be ISE
        //
        X12Segment lastSegment = segmentList.get(segmentList.size() - 1);
        if (StandardX12Parser.ENVELOPE_TRAILER_ID.equals(lastSegment.getIdentifier())) {
            iseTrailer = lastSegment;
        } else {
            // error - should be start of ISA envelope
            this.throwParserException(StandardX12Parser.ENVELOPE_TRAILER_ID, currentSegmentId);
        }
        
        while (segments.hasNext()) {
            // manage each group
            List<TransactionHolder> transactionsInGroup = this.doGroup(segments);
            transactionHolders.addAll(transactionsInGroup);
            
            // what's next
            // could be a GS or IEA
            if (segments.hasNext()) {
                currentSegment = segments.next();
                currentSegmentId = currentSegment.getIdentifier();
                if (StandardX12Parser.ENVELOPE_TRAILER_ID.equals(currentSegmentId)) {
                    // end of the envelope
                    break;
                } else {
                    // not the end of envelope
                    // assume it starts another group
                    // so move back and process
                    segments.previous();
                }
            } else {
                this.throwParserExceptionUnexpectedEnd(currentSegmentId);
            }
        }

        this.populateIsaEnvelope(isaHeader, iseTrailer, transactionHolders);
        
        return transactionHolders;
    }
    
    private void populateIsaEnvelope(X12Segment isaHeader, X12Segment iseTrailer,
        List<TransactionHolder> transactionHolders) {

        this.alterEnvelopeTrailerBasedOnSplit(iseTrailer);
        
        transactionHolders.forEach(txHolder -> {
            txHolder.setIsaHeader(isaHeader);
            txHolder.setIseTrailer(iseTrailer);
        });
    }
    
    private List<TransactionHolder> doGroup(SegmentIterator segments) {
        List<TransactionHolder> transactionsInGroup = new ArrayList<>();
        
        X12Segment groupHeader = null;
        
        X12Segment currentSegment = segments.next();
        String currentSegmentId = currentSegment.getIdentifier();
        
        //
        // first segment better be GS (start of group)
        //
        if (StandardX12Parser.GROUP_HEADER_ID.equals(currentSegmentId)) {
            groupHeader = currentSegment;
            
            // loop until we find the GE (end of group)
            while (segments.hasNext()) {
                // handle the start of the transaction
                TransactionHolder transactionHolder = this.doTransaction(segments);
                transactionHolder.setGsHeader(groupHeader);
                transactionsInGroup.add(transactionHolder);
                
                // what's next?
                // after a transaction it
                // could be an ST or GE
                if (segments.hasNext()) {
                    currentSegment = segments.next();
                    currentSegmentId = currentSegment.getIdentifier();
                    if (StandardX12Parser.GROUP_TRAILER_ID.equals(currentSegmentId)) {
                        // end of the group
                        
                        X12Segment groupTrailer = currentSegment;
                        
                        // extension point
                        this.alterGroupTrailerBasedOnSplit(groupTrailer);
                        
                        // add group trailer to all transactions
                        transactionsInGroup.forEach(txHolder -> {
                            txHolder.setGeTrailer(groupTrailer);
                        });
                        
                        break;
                    } else {
                        // not the end of group
                        // assume it starts another transaction
                        // so move back and process
                        segments.previous();
                    }
                } else {
                    // if we didn't get an ST or GE
                    // we have a bad EDI file
                    this.throwParserExceptionUnexpectedEnd(currentSegmentId);
                }
            }
            
        } else { 
            // error
            this.throwParserException("GS", currentSegmentId);
        }
        
        return transactionsInGroup;
    }
    
    /**
     * an extension point for consumers that 
     * may want to alter the Group Trailer segment 
     * based on the splitting that occurred
     * 
     * @param groupTrailer
     * @param transactionsInGroup
     */
    protected void alterGroupTrailerBasedOnSplit(X12Segment groupTrailer) {
        // a consumer could decide to replace the transaction count
        // presumably by overwriting the document value w/ a one 
        // since the splitting will be done on the ST/SE boundary
    }
    
    /**
     * an extension point for consumers that 
     * may want to alter the Envelope Trailer segment 
     * based on the splitting that occurred
     * 
     * @param envelopeTrailer
     */
    protected void alterEnvelopeTrailerBasedOnSplit(X12Segment envelopeTrailer) {
        // a consumer could decide to replace the group count
        // presumably by overwriting the document value w/ a one 
        // since the splitting will be done on the ST/SE boundary
    }
    
    private TransactionHolder doTransaction(SegmentIterator segments) {
        
        TransactionHolder transactionHolder = new TransactionHolder();
        
        // start collecting transaction 
        // segments until we hit the SE (end of transaction)
        // or run out of segments
        X12Segment currentSegment = segments.next();
        String currentSegmentId = currentSegment.getIdentifier();
        
        //
        // first segment better be ST (start of transaction)
        //
        if (X12TransactionSet.TRANSACTION_SET_HEADER.equals(currentSegmentId)) {
            // add the header
            transactionHolder.addSegmentToTransaction(currentSegment);
            
            // loop until we find the SE (end of transaction)
            while (segments.hasNext() && ! X12TransactionSet.TRANSACTION_SET_TRAILER.equals(currentSegmentId)) {
                currentSegment = segments.next();
                currentSegmentId = currentSegment.getIdentifier();
                transactionHolder.addSegmentToTransaction(currentSegment);
            }
            
        } else {
            // error
            this.throwParserException(X12TransactionSet.TRANSACTION_SET_HEADER, currentSegmentId);
        }
        
        //
        // we should have broken out of loop 
        // because the current segment is SE (end of transaction)
        //
        if (!X12TransactionSet.TRANSACTION_SET_TRAILER.equals(currentSegmentId)) {
            // error
            this.throwParserException(X12TransactionSet.TRANSACTION_SET_TRAILER, currentSegmentId);
        }
    
        return transactionHolder;
    }
    
    private List<String> doSplit(List<TransactionHolder> transactionHolders) {
        List<String> transactions = new ArrayList<>();  
        
        transactionHolders.forEach(transaction -> {
            
            // add headers
            StringBuilder sb = new StringBuilder();
            sb.append(transaction.getIsaHeader().toString()).append(EOL);
            sb.append(transaction.getGsHeader().toString()).append(EOL);
            
            // add each line of transaction
            transaction.getTransaction().forEach(txSegment -> {
                sb.append(txSegment.toString()).append(EOL);
            });
            
            // add trailers
            sb.append(transaction.getGeTrailer().toString()).append(EOL);
            sb.append(transaction.getIseTrailer().toString());
            
            transactions.add(sb.toString());
        });
        
        return transactions;
    }
    
    private void runRules(List<X12Segment> segmentList) {
        if (rules != null) {
            rules.forEach(rule -> {
                if (rule != null) {
                    rule.verify(segmentList);
                }
            });
        }
    }
    
    private void throwParserException(String expectedSegmentId, String actualSegmentId) {
        StringBuilder sb = new StringBuilder();
        sb.append("expected ").append(expectedSegmentId);
        sb.append(" segment but got ").append(actualSegmentId);
        throw new X12ParserException(new X12ErrorDetail(expectedSegmentId, "", sb.toString()));
    }
    
    private void throwParserExceptionUnexpectedEnd(String actualSegmentId) {
        StringBuilder sb = new StringBuilder();
        sb.append("unexpectedly ran out of segments - last segment id (").append(actualSegmentId).append(")");
        throw new X12ParserException(new X12ErrorDetail("", "", sb.toString()));
    }
    
    /**
     * allow construction w/o rules
     */
    public X12TransactionSplitter() {
    }
    
    /**
     * allow construction w/ rules
     */
    public X12TransactionSplitter(List<X12Rule> rules) {
        this.rules = rules;
    }

    
    public class TransactionHolder {
   
        private X12Segment isaHeader;
        private X12Segment iseTrailer;
        
        private X12Segment gsHeader;
        private X12Segment geTrailer;
        
        private List<X12Segment> transactionSegmentList;

        public X12Segment getIsaHeader() {
            return isaHeader;
        }

        public void setIsaHeader(X12Segment isaHeader) {
            this.isaHeader = isaHeader;
        }

        public X12Segment getIseTrailer() {
            return iseTrailer;
        }

        public void setIseTrailer(X12Segment iseTrailer) {
            this.iseTrailer = iseTrailer;
        }

        public X12Segment getGsHeader() {
            return gsHeader;
        }

        public void setGsHeader(X12Segment gsHeader) {
            this.gsHeader = gsHeader;
        }

        public X12Segment getGeTrailer() {
            return geTrailer;
        }

        public void setGeTrailer(X12Segment geTrailer) {
            this.geTrailer = geTrailer;
        }

        public List<X12Segment> getTransaction() {
            return transactionSegmentList;
        }

        public void setTransaction(List<X12Segment> transaction) {
            this.transactionSegmentList = transaction;
        }
        
        /**
         */
        public void addSegmentToTransaction(X12Segment segment) {
            if (transactionSegmentList == null) {
                transactionSegmentList = new ArrayList<>();
            }
            transactionSegmentList.add(segment);
        }
        
    }
}
