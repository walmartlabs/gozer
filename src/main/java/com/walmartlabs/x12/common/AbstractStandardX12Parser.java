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
package com.walmartlabs.x12.common;

import com.walmartlabs.x12.X12Parser;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.util.ConversionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

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
public abstract class AbstractStandardX12Parser<T extends AbstractStandardX12Document> implements X12Parser<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStandardX12Parser.class);

    public static final String ISA_HEADER_ID = "ISA";
    public static final String ISA_TRAILER_ID = "IEA";

    public static final String GROUP_HEADER_ID = "GS";
    public static final String GROUP_TRAILER_ID = "GE";

    public static final String TRANSACTION_HEADER_ID = "ST";
    public static final String TRANSACTION_TRAILER_ID = "SE";

    /**
     * template for parsing a standard EDI X12 document
     *
     * @return T (the Class associated with the parser)
     * @throws X12ParserException if the document can't be parsed
     */
    @Override
    public T parse(String sourceData) {
        AbstractStandardX12Document x12Doc = null;

        try {
            if (!StringUtils.isEmpty(sourceData)) {
                // delegate creation of the concrete X12 document
                x12Doc = this.createX12Document();

                // break document up into segment lines
                List<X12Segment> segmentLines = this.splitSourceDataIntoSegments(sourceData);

                //
                // interchange control header
                //
                int segmentIdx = 0;
                X12Segment currentSegment = this.nextSegment(segmentIdx++, segmentLines);
                this.parseInterchangeControlHeader(currentSegment, x12Doc);

                //
                // parse groups
                //
                boolean insideGroup = false;
                boolean insideTransaction = false;

                while (this.hasMoreSegmentLines(segmentIdx, segmentLines)) {
                    // get the next segment
                    currentSegment = this.nextSegment(segmentIdx++, segmentLines);

                    // parse group header
                    X12Group currentGroup = this.parseGroupHeader(currentSegment, x12Doc);
                    x12Doc.addGroupHeader(currentGroup);
                    insideGroup = true;

                    // get all segment lines for a single transaction
                    // there may be more than one transaction set
                    // in a group
                    List<X12Segment> transactionSet = new ArrayList<>();
                    while (insideGroup && this.hasMoreSegmentLines(segmentIdx, segmentLines)) {
                        // get the next segment
                        currentSegment = this.nextSegment(segmentIdx++, segmentLines);

                        if (TRANSACTION_HEADER_ID.equals(currentSegment.getSegmentIdentifier())) {
                            if (insideTransaction) {
                                // we are already in a transaction
                                // and have not encountered the end
                                // so we will stop parsing
                                handleUnexpectedSegment(TRANSACTION_TRAILER_ID, currentSegment.getSegmentIdentifier());
                            } else {
                                insideTransaction = true;
                                transactionSet.add(currentSegment);
                            }
                        } else if (TRANSACTION_TRAILER_ID.equals(currentSegment.getSegmentIdentifier())) {
                            if (insideTransaction) {
                                transactionSet.add(currentSegment);
                                // delegate parsing of transaction set
                                this.parseTransasctionSet(transactionSet, currentGroup);
                                // get ready for next segment
                                insideTransaction = false;
                                transactionSet.clear();
                            } else {
                                // we are not in a transaction
                                // so should not have gotten transaction trailer
                                handleUnexpectedSegment(TRANSACTION_HEADER_ID, currentSegment.getSegmentIdentifier());
                            }
                        } else if (GROUP_TRAILER_ID.equals(currentSegment.getSegmentIdentifier())) {
                            if (insideTransaction) {
                                // we are already in a transaction
                                // and have not encountered the end
                                // so we will stop parsing
                                handleUnexpectedSegment(TRANSACTION_TRAILER_ID, currentSegment.getSegmentIdentifier());
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
                        handleUnexpectedSegment(TRANSACTION_TRAILER_ID, currentSegment.getSegmentIdentifier());
                    }

                    // if we got here we should have cleanly
                    // exited a group
                    if (insideGroup) {
                        handleUnexpectedSegment(GROUP_TRAILER_ID, currentSegment.getSegmentIdentifier());
                    }

                    // parse group trailer
                    this.parseGroupTrailer(currentSegment, currentGroup);

                    // check for end of message
                    if (this.hasMoreSegmentLines(segmentIdx, segmentLines)) {
                        currentSegment = this.nextSegment(segmentIdx, segmentLines);
                        if (ISA_TRAILER_ID.equals(currentSegment.getSegmentIdentifier())) {
                            this.parseInterchangeControlTrailer(currentSegment, x12Doc);
                            segmentIdx++;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new X12ParserException("Invalid EDI X12 message: unexpected error", e);
        }

        return (T) x12Doc;
    }

    /**
     * TODO: candidate for object that manages segment lines
     * compare the current segment index with the segment lines
     */
    private boolean hasMoreSegmentLines(int currentSegmentIdx, List<X12Segment> segmentLines) {
        return (currentSegmentIdx < segmentLines.size()) ;
    }

    /**
     * TODO: candidate for object that manages segment lines
     * @param currentSegmentIdx
     * @param segmentLines
     * @return
     */
    private X12Segment nextSegment(int currentSegmentIdx, List<X12Segment> segmentLines) {
        if (this.hasMoreSegmentLines(currentSegmentIdx, segmentLines)) {
            return segmentLines.get(currentSegmentIdx);
        } else {
            return null;
        }
    }

    protected abstract AbstractStandardX12Document createX12Document();

    protected abstract void parseTransasctionSet(List<X12Segment> transactionSegments, X12Group x12Group);

    /**
     * parse the ISA segment
     *
     * @param segment
     * @param x12Doc
     */
    protected void parseInterchangeControlHeader(X12Segment segment, AbstractStandardX12Document x12Doc) {
        LOGGER.debug(segment.getSegmentIdentifier());

        String segmentIdentifier = segment.getSegmentIdentifier();
        if (ISA_HEADER_ID.equals(segmentIdentifier)) {
            InterchangeControlHeader isa = new InterchangeControlHeader();
            isa.setAuthorizationInformationQualifier(segment.getSegmentElement(1));
            isa.setAuthorizationInformation(segment.getSegmentElement(2));
            isa.setSecurityInformationQualifier(segment.getSegmentElement(3));
            isa.setSecurityInformation(segment.getSegmentElement(4));
            isa.setInterchangeIdQualifier(segment.getSegmentElement(5));
            isa.setInterchangeSenderId(segment.getSegmentElement(6));
            isa.setInterchangeIdQualifier_2(segment.getSegmentElement(7));
            isa.setInterchangeReceiverId(segment.getSegmentElement(8));
            isa.setInterchangeDate(segment.getSegmentElement(9));
            isa.setInterchangeTime(segment.getSegmentElement(10));
            isa.setInterchangeControlStandardId(segment.getSegmentElement(11));
            isa.setInterchangeControlVersion(segment.getSegmentElement(12));
            isa.setInterchangeControlNumber(segment.getSegmentElement(13));
            isa.setAcknowledgementRequested(segment.getSegmentElement(14));
            isa.setUsageIndicator(segment.getSegmentElement(15));
            isa.setElementSeparator(segment.getSegmentElement(16));

            x12Doc.setInterchangeControlHeader(isa);
        } else {
            handleUnexpectedSegment(ISA_HEADER_ID, segmentIdentifier);
        }
    }

    /**
     * parse the ISE segment
     *
     * @param segment
     * @param x12Doc
     */
    protected void parseInterchangeControlTrailer(X12Segment segment, AbstractStandardX12Document x12Doc) {

    }

    /**
     * parse the GS segment
     *
     * @param segment
     * @param x12Doc
     */
    protected X12Group parseGroupHeader(X12Segment segment, AbstractStandardX12Document x12Doc) {
        LOGGER.debug(segment.getSegmentIdentifier());

        X12Group groupHeader = null;
        String segmentIdentifier = segment.getSegmentIdentifier();
        if (GROUP_HEADER_ID.equals(segmentIdentifier)) {
            groupHeader = new X12Group();
            groupHeader.setFunctionalCodeId(segment.getSegmentElement(1));
            groupHeader.setApplicationSenderCode(segment.getSegmentElement(2));
            groupHeader.setApplicationReceiverCode(segment.getSegmentElement(3));
            groupHeader.setDate(segment.getSegmentElement(4));
            groupHeader.setTime(segment.getSegmentElement(5));
            groupHeader.setHeaderGroupControlNumber(segment.getSegmentElement(6));
            groupHeader.setResponsibleAgencyCode(segment.getSegmentElement(7));
            groupHeader.setVersion(segment.getSegmentElement(8));

//            x12Doc.addGroupHeader(groupHeader);
        } else {
            handleUnexpectedSegment(GROUP_HEADER_ID, segmentIdentifier);
        }
        return groupHeader;
    }

    /**
     * parse the GE segment
     * @param segment
     * @param x12Doc
     */
    protected void parseGroupTrailer(X12Segment segment, X12Group x12Group) {
        LOGGER.debug(segment.getSegmentIdentifier());

        String segmentIdentifier = segment.getSegmentIdentifier();
        if (GROUP_TRAILER_ID.equals(segmentIdentifier)) {
            x12Group.setNumberOfTransactions(ConversionUtil.convertStringToInteger(segment.getSegmentElement(1)));
            x12Group.setTrailerGroupControlNumber(segment.getSegmentElement(2));
        } else {
            handleUnexpectedSegment(GROUP_TRAILER_ID, segmentIdentifier);
        }
    }
}
