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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * DEX 894 Base Record Transaction Set is essentially a set of invoices
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
 */
public abstract class AbstractStandardX12Parser<T extends AbstractStandardX12Document> implements X12Parser<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStandardX12Parser.class);

    public static final String ISA_HEADER_ID = "ISA";
    public static final String ISA_TRAILER_ID = "ISE";

    public static final String GROUP_HEADER_ID = "GS";
    public static final String GROUP_TRAILER_ID = "GE";

    /**
     * template for parsing a standard EDI X12 document
     *
     * @return T (the Class associated with the parser)
     * @throws X12ParserException
     */
    @Override
    public T parse(String sourceData) {
        AbstractStandardX12Document x12Doc = null;

        try {
            if (!StringUtils.isEmpty(sourceData)) {
                x12Doc = this.createX12Document();
                List<X12Segment> segmentLines = this.splitSourceDataIntoSegments(sourceData);
                int segmentIdx = 0;
                this.parseInterchangeControlHeader(segmentLines.get(segmentIdx++), x12Doc);
                this.parseGroupHeader(segmentLines.get(segmentIdx++), x12Doc);
                this.parseCustom(segmentLines, x12Doc);
            }
        } catch (Exception e) {
            throw new X12ParserException("Invalid EDI X12 message: unexpected error", e);
        }

        return (T) x12Doc;
    }

    protected abstract AbstractStandardX12Document createX12Document();

    protected abstract void parseCustom(List<X12Segment> segmentLines, AbstractStandardX12Document x12Doc);

    /**
     * parse the ISA segment
     * @param segment
     * @param x12
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
     * parse the GS segment
     * @param segment
     * @param x12
     */
    protected void parseGroupHeader(X12Segment segment, AbstractStandardX12Document x12Doc) {
        LOGGER.debug(segment.getSegmentIdentifier());

        String segmentIdentifier = segment.getSegmentIdentifier();
        if (GROUP_HEADER_ID.equals(segmentIdentifier)) {
            X12Group groupHeader = new X12Group();
            groupHeader.setFunctionalCodeId(segment.getSegmentElement(1));
            groupHeader.setApplicationSenderCode(segment.getSegmentElement(2));
            groupHeader.setApplicationReceiverCode(segment.getSegmentElement(3));
            groupHeader.setDate(segment.getSegmentElement(4));
            groupHeader.setTime(segment.getSegmentElement(5));
            groupHeader.setHeaderGroupControlNumber(segment.getSegmentElement(6));
            groupHeader.setResponsibleAgencyCode(segment.getSegmentElement(7));
            groupHeader.setVersion(segment.getSegmentElement(8));

            x12Doc.addGroupHeader(groupHeader);
        } else {
            handleUnexpectedSegment(GROUP_HEADER_ID, segmentIdentifier);
        }
    }
}
