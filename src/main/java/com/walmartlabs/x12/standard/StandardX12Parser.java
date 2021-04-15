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

package com.walmartlabs.x12.standard;

import com.walmartlabs.x12.SegmentIterator;
import com.walmartlabs.x12.X12Parser;
import com.walmartlabs.x12.X12ParsingUtil;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.txset.AbstractTransactionSetParserChainable;
import com.walmartlabs.x12.standard.txset.TransactionSetParser;
import com.walmartlabs.x12.standard.txset.UnhandledTransactionSet;
import com.walmartlabs.x12.util.ConversionUtil;
import com.walmartlabs.x12.util.SourceToSegmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * X12 Parser
 *
 * Envelope
 * -- ISA
 * ----- Groups
 * -- ISE
 *
 * Group
 * -- GS
 * ----- Transactions
 * -- GE
 *
 * Transaction
 * -- ST
 * ----- Transaction Details
 * -- SE
 *
 */
public final class StandardX12Parser implements X12Parser<StandardX12Document> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StandardX12Parser.class);

    public static final String ENVELOPE_HEADER_ID = "ISA";
    public static final String ENVELOPE_TRAILER_ID = "IEA";

    public static final String GROUP_HEADER_ID = "GS";
    public static final String GROUP_TRAILER_ID = "GE";
    
    private TransactionSetParser transactionParser;
    private UnhandledTransactionSet unhandledTransactionSet;

    /**
     * parse an X12 document into the representative Java POJO
     *
     * @param sourceData the document to be parsed
     * @return a {@link StandardX12Document} or null if sourceData is null
     * @throws X12ParserException if the document can't be parsed
     */
    @Override
    public StandardX12Document parse(String sourceData) {
        StandardX12Document x12Doc = null;

        try {
            if (!StringUtils.isEmpty(sourceData)) {
                x12Doc = new StandardX12Document();

                // remove any excess white space
                // and
                // break document up into segment lines
                List<X12Segment> segmentList = SourceToSegmentUtil.splitSourceDataIntoSegments(sourceData.trim());
                if (X12ParsingUtil.isValidEnvelope(segmentList, ENVELOPE_HEADER_ID, ENVELOPE_TRAILER_ID)) {
                    // standard parsing of segment lines
                    SegmentIterator segments = new SegmentIterator(segmentList);
                    this.standardParsingTemplate(segments, x12Doc);
                } else  {
                    throw new X12ParserException("Invalid EDI X12 message: must be wrapped in ISA/ISE");
                }
            }
        } catch (X12ParserException e) {
            // if the exception is already an
            // X12ParserException pass it through
            throw e;
        } catch (Exception e) {
            // all exceptions except an X12ParserException
            // should be wrapped
            throw new X12ParserException("Invalid EDI X12 message: unexpected error", e);
        }

        return x12Doc;
    }
    
    /**
     * convenience method that will allow a Collection of {@link TransactionSetParser} 
     * to be registered w/ the parser
     * 
     * Any null value in the Collection will be ignored.
     * 
     * Note: if there are one or more {@link TransactionSetParser} already registered 
     * with the parser, this method will append the parsers in the Collection 
     * to the existing chain of parsers. 
     * 
     * @param transactionParsers - a Collection of TransactionSetParser(s)
     * @return true if all were added, false otherwise
     */
    public boolean registerTransactionSetParser(Collection<TransactionSetParser> transactionParsers) {
        boolean isAdded = false;
        
        if (transactionParsers != null && !transactionParsers.isEmpty()) {
            isAdded = transactionParsers.stream()
                .filter(Objects::nonNull)
                .map(this::registerTransactionSetParser)
                .reduce(true, (currAdded, wasAdded) -> currAdded && wasAdded);
        }

        return isAdded;
    }
    
    /**
     * convenience method that will allow one or more {@link TransactionSetParser} 
     * to be registered w/ the parser
     * Note: if a transaction set type does not have a registered parser it is ignored
     * 
     * @param transactionParsers
     * @return true if non-null and added, otherwise false
     */
    public boolean registerTransactionSetParser(TransactionSetParser txParser) {
        boolean isAdded = false;
        
        if (txParser != null) {
            if (this.transactionParser == null) {
                // we don't have a transaction set parser
                // so we will register this one
                isAdded = true;
                this.transactionParser = txParser;
            } else if (this.transactionParser instanceof AbstractTransactionSetParserChainable) {
                // we have a transaction set parser
                // so try to add this to the end of the existing chain
                return ((AbstractTransactionSetParserChainable) this.transactionParser)
                    .registerNextTransactionSetParser(txParser);
            }
        }
        
        return isAdded;
    }
    
    /**
     * register a handler for unhandled transaction sets
     * an unhandled transaction set is one that did not have 
     * a {@link TransactionSetParser} registered.
     * 
     * @param txUnhandled
     */
    public void registerUnhandledTransactionSet(UnhandledTransactionSet txUnhandled) {
        this.unhandledTransactionSet = txUnhandled;
    }

    /**
     * template for parsing a standard EDI X12 document
     *
     * @throws X12ParserException if the document can't be parsed
     */
    private void standardParsingTemplate(SegmentIterator segments, StandardX12Document x12Doc) {

        //
        // interchange control header
        //
        X12Segment currentSegment = segments.next();
        this.parseInterchangeControlHeader(currentSegment, x12Doc);

        //
        // parse groups
        //
        boolean insideGroup = false;
        boolean insideTransaction = false;

        while (segments.hasNext()) {
            // get the next segment
            currentSegment = segments.next();

            // parse group header
            X12Group currentGroup = this.parseGroupHeader(currentSegment, x12Doc);
            x12Doc.addGroupHeader(currentGroup);
            insideGroup = true;

            // get all segment lines for a single transaction
            // there may be more than one transaction set
            // in a group
            List<X12Segment> transactionSet = new ArrayList<>();
            while (insideGroup && segments.hasNext()) {
                // get the next segment
                currentSegment = segments.next();

                if (X12TransactionSet.TRANSACTION_SET_HEADER.equals(currentSegment.getIdentifier())) {
                    if (insideTransaction) {
                        // we are already in a transaction
                        // and have not encountered the end
                        // so we will stop parsing
                        this.handleUnexpectedSegment(X12TransactionSet.TRANSACTION_SET_TRAILER, currentSegment.getIdentifier());
                    } else {
                        insideTransaction = true;
                        transactionSet.add(currentSegment);
                    }
                } else if (X12TransactionSet.TRANSACTION_SET_TRAILER.equals(currentSegment.getIdentifier())) {
                    if (insideTransaction) {
                        transactionSet.add(currentSegment);
                        // delegate parsing of transaction set
                        this.parseTransactionSet(transactionSet, currentGroup);
                        // get ready for next segment
                        insideTransaction = false;
                        transactionSet.clear();
                    } else {
                        // we are not in a transaction
                        // so should not have gotten transaction trailer
                        this.handleUnexpectedSegment(X12TransactionSet.TRANSACTION_SET_HEADER, currentSegment.getIdentifier());
                    }
                } else if (GROUP_TRAILER_ID.equals(currentSegment.getIdentifier())) {
                    if (insideTransaction) {
                        // we are already in a transaction
                        // and have not encountered the end
                        // so we will stop parsing
                        this.handleUnexpectedSegment(X12TransactionSet.TRANSACTION_SET_TRAILER, currentSegment.getIdentifier());
                    } else {
                        insideGroup = false;
                    }
                } else {
                    // add the segment to the current transaction set
                    transactionSet.add(currentSegment);
                }
            }

            // if we got here we should have cleanly
            // exited a transaction
            if (insideTransaction) {
                this.handleUnexpectedSegment(X12TransactionSet.TRANSACTION_SET_TRAILER, currentSegment.getIdentifier());
            }

            // if we got here we should have cleanly
            // exited a group
            if (insideGroup) {
                this.handleUnexpectedSegment(GROUP_TRAILER_ID, currentSegment.getIdentifier());
            }

            // parse group trailer
            this.parseGroupTrailer(currentSegment, currentGroup);

            // check for end of message
            if (segments.hasNext()) {
                currentSegment = segments.next();
                if (ENVELOPE_TRAILER_ID.equals(currentSegment.getIdentifier())) {
                    this.parseInterchangeControlTrailer(currentSegment, x12Doc);
                } else {
                    // move back one
                    // and let the parser
                    // see if it is valid
                    currentSegment = segments.previous();
                }
            }
        }
    }

    /**
     * parse the ISA segment
     *
     * @param segment
     * @param x12Doc
     */
    private void parseInterchangeControlHeader(X12Segment segment, StandardX12Document x12Doc) {
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (ENVELOPE_HEADER_ID.equals(segmentIdentifier)) {
            InterchangeControlEnvelope isa = new InterchangeControlEnvelope();
            isa.setAuthorizationInformationQualifier(segment.getElement(1));
            isa.setAuthorizationInformation(segment.getElement(2));
            isa.setSecurityInformationQualifier(segment.getElement(3));
            isa.setSecurityInformation(segment.getElement(4));
            isa.setInterchangeIdQualifier(segment.getElement(5));
            isa.setInterchangeSenderId(segment.getElement(6));
            isa.setInterchangeIdQualifierTwo(segment.getElement(7));
            isa.setInterchangeReceiverId(segment.getElement(8));
            isa.setInterchangeDate(segment.getElement(9));
            isa.setInterchangeTime(segment.getElement(10));
            isa.setInterchangeControlStandardId(segment.getElement(11));
            isa.setInterchangeControlVersion(segment.getElement(12));
            isa.setInterchangeControlNumber(segment.getElement(13));
            isa.setAcknowledgementRequested(segment.getElement(14));
            isa.setUsageIndicator(segment.getElement(15));
            isa.setElementSeparator(segment.getElement(16));

            x12Doc.setInterchangeControlEnvelope(isa);
        } else {
            this.handleUnexpectedSegment(ENVELOPE_HEADER_ID, segmentIdentifier);
        }
    }

    /**
     * parse the ISE segment
     *
     * @param segment
     * @param x12Doc
     */
    private void parseInterchangeControlTrailer(X12Segment segment, StandardX12Document x12Doc) {
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (ENVELOPE_TRAILER_ID.equals(segmentIdentifier)) {
            InterchangeControlEnvelope isa = x12Doc.getInterchangeControlEnvelope();
            isa.setNumberOfGroups(ConversionUtil.convertStringToInteger(segment.getElement(1)));
            isa.setTrailerInterchangeControlNumber(segment.getElement(2));
        } else {
            this.handleUnexpectedSegment(ENVELOPE_TRAILER_ID, segmentIdentifier);
        }
    }

    /**
     * parse the GS segment
     *
     * @param segment
     * @param x12Doc
     */
    private X12Group parseGroupHeader(X12Segment segment, StandardX12Document x12Doc) {
        LOGGER.debug(segment.getIdentifier());

        X12Group groupHeader = null;
        String segmentIdentifier = segment.getIdentifier();
        if (GROUP_HEADER_ID.equals(segmentIdentifier)) {
            groupHeader = new X12Group();
            groupHeader.setFunctionalCodeId(segment.getElement(1));
            groupHeader.setApplicationSenderCode(segment.getElement(2));
            groupHeader.setApplicationReceiverCode(segment.getElement(3));
            groupHeader.setDate(segment.getElement(4));
            groupHeader.setTime(segment.getElement(5));
            groupHeader.setHeaderGroupControlNumber(segment.getElement(6));
            groupHeader.setResponsibleAgencyCode(segment.getElement(7));
            groupHeader.setVersion(segment.getElement(8));
        } else {
            this.handleUnexpectedSegment(GROUP_HEADER_ID, segmentIdentifier);
        }
        return groupHeader;
    }

    /**
     * parse the GE segment
     * @param segment
     * @param x12Doc
     */
    private void parseGroupTrailer(X12Segment segment, X12Group x12Group) {
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (GROUP_TRAILER_ID.equals(segmentIdentifier)) {
            x12Group.setNumberOfTransactions(ConversionUtil.convertStringToInteger(segment.getElement(1)));
            x12Group.setTrailerGroupControlNumber(segment.getElement(2));
        } else {
            this.handleUnexpectedSegment(GROUP_TRAILER_ID, segmentIdentifier);
        }
    }
    
    /**
     * register the correct {@link TransactionSetParser} to parse the transaction set(s) and add the resulting objects to the X12 Group
     * @param transactionSegments
     * @param x12Group
     */
    private void parseTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group) {
        if (transactionParser != null) {
            X12TransactionSet txSet = transactionParser.parseTransactionSet(transactionSegments, x12Group);
            if (txSet != null) {
                x12Group.addTransactionSet(txSet);
            } else {
                // no transaction set parser for that type
                if (unhandledTransactionSet != null) {
                    unhandledTransactionSet.unhandledTransactionSet(transactionSegments, x12Group);
                }
            }
        } else {
            LOGGER.warn("No TransactionSetParser has been registered!");
        }
    }
    
}
