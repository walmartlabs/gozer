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
package com.walmartlabs.x12.dex.dx894;

import com.walmartlabs.x12.X12Parser;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.util.VersionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * DEX 894 Base Record Transaction Set is essentially a set of invoices
 *
 * Envelope
 * -- DXS
 * ----- Transactions
 * -- DXE
 *
 * Transaction (Invoice)
 * -- ST
 * ---- G82 (Transaction header)
 * -------- LS
 * ------------ Transaction #1
 * ------------ G83 (Item Detail)
 * ------------ G22 (Optional)
 * ------------ G72 (Optional)
 *
 * ------------ Transaction #2
 * ------------ G83 (Item Detail)
 * ------------ G22 (Optional)
 * ------------ G72 (Optional)
 * -------- LE
 * ---- G84 (Transaction summary)
 * ---- G86 (electronic signature)
 * ---- G85 (integrity check)
 * -- SE
 *
 */
public class DefaultDex894Parser implements X12Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDex894Parser.class);

    public static final String APPLICATION_HEADER_ID = "DXS";
    public static final String APPLICATION_TRAILER_ID = "DXE";
    public static final String TRANSACTION_SET_HEADER_ID = "ST";
    public static final String TRANSACTION_SET_TRAILER_ID = "SE";
    public static final String G82_ID = "G82";
    public static final String G83_ID = "G83";
    public static final String G22_ID = "G22";
    public static final String G72_ID = "G72";
    public static final String G84_ID = "G84";
    public static final String G85_ID = "G85";
    public static final String G86_ID = "G86";
    public static final String LOOP_HEADER_ID = "LS";
    public static final String LOOP_TRAILER_ID = "LE";

    /**
     * parse the DEX 894 transmission into
     * a representative Java object
     *
     * @return {@link Dex894}
     * @throws X12ParserException
     */
    @Override
    public Dex894 parse(String sourceData) {
        Dex894 dex = null;

        if (!StringUtils.isEmpty(sourceData)) {
            dex = new Dex894();
            List<String> dexLines = this.splitSourceDataIntoSegments(sourceData);

            if (!this.isValidEnvelope(dexLines)) {
                throw new X12ParserException("Invalid DEX envelope");
            } else {
                try {
                    int lastSegment = this.findLastSegmentIndex(dexLines);
                    // application header
                    this.parseApplicationHeader(dexLines.get(0), dex);

                    // parse transactions...
                    int idx = 1;
                    do {
                        Dex894TransactionSet dexTx = new Dex894TransactionSet();
                        idx = this.parseDexTransaction(idx, dexLines, dexTx);
                        dex.addTransaction(dexTx);
                    } while (idx < lastSegment);

                    // application trailer
                    this.parseApplicationTrailer(dexLines.get(lastSegment), dex);
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new X12ParserException("Invalid DEX message: missing mandatory fields");
                }
            }
        }

        return dex;
    }

    /**
     * Under DEX/UCS, all transaction sets are enclosed in a DXS/DXE envelope.
     * A DXS data segment must appear before the first transaction set in a transmission.
     * A DXE data segment must appear after the last transaction set in a transmission
     *
     * @param dexSegments
     * @return true when valid and false otherwise
     */
    protected boolean isValidEnvelope(List<String> dexSegments) {
        boolean isValidEnvelope = false;
        int lastSegment = this.findLastSegmentIndex(dexSegments);
        if (dexSegments.size() > 2) {
            if (APPLICATION_HEADER_ID.equals(this.segmentIdentifier(0, dexSegments))
                    && APPLICATION_TRAILER_ID.equals(this.segmentIdentifier(lastSegment, dexSegments))) {
                isValidEnvelope = true;
            }
        }
        return isValidEnvelope;
    }

    /**
     * parses one DEX transaction
     * @param startingIdx which segment line to start with
     * @param dexSegments
     * @param dexTx
     * @return the starting point for the next transaction
     * @throws X12ParserException if the DEX transaction is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected int parseDexTransaction(final int startingIdx, final List<String> dexSegments, final Dex894TransactionSet dexTx) {
        LOGGER.debug("parseDexTransaction:" + startingIdx);
        String segment = dexSegments.get(startingIdx);
        String segmentId = this.extractSegmentIdentifier(segment);

        int segmentIdx = startingIdx;
        if (!TRANSACTION_SET_HEADER_ID.equals(segmentId)) {
            throw new X12ParserException("Expected start of DEX transaction");
        } else {
            // ST line
            this.parseTransactionSetHeader(segment, dexTx);

            // G82 line
            segment = dexSegments.get(++segmentIdx);
            this.parseG82(segment, dexTx);

            // process all of the lines between the LS and LE
            // stop when the next line is NOT an ST
            // so we can complete processing the DEX transaction
            segmentIdx++;
            do {
                segmentIdx = this.parseDexTransactionLoop(segmentIdx, dexSegments, dexTx);
            } while (TRANSACTION_SET_HEADER_ID.equals(this.segmentIdentifier(segmentIdx, dexSegments)));

            // next set of lines after the transaction loop can vary
            segment = dexSegments.get(segmentIdx);
            segmentId = this.extractSegmentIdentifier(segment);

            // G84 line (conditional)
            if (G84_ID.equals(segmentId)) {
                this.parseG84(segment, dexTx);
                // update next segment & segment id
                segment = dexSegments.get(++segmentIdx);
                segmentId = this.extractSegmentIdentifier(segment);
            }

            // G86 line (optional)
            if (G86_ID.equals(segmentId)) {
                this.parseG86(segment, dexTx);
                // update next segment & segment id
                segment = dexSegments.get(++segmentIdx);
            }

            // G85 line (mandatory)
            this.parseG85(segment, dexTx);

            // SE line
            segment = dexSegments.get(++segmentIdx);
            this.parseTransactionSetTrailer(segment, dexTx);

            // increase index to line after SE
            segmentIdx++;

            // add the actual number of segments
            // in the DEX transaction for later validation
            dexTx.setActualNumberOfSegments(segmentIdx - startingIdx);
        }

        return segmentIdx;
    }

    /**
     * parses one DEX transaction loop (LS to LE)
     * @param startingIdx which segment line to start with
     * @param dexSegments
     * @param dexTx
     * @return the starting point for the next transaction loop
     * @throws X12ParserException if the DEX transaction loop is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected int parseDexTransactionLoop(final int startingIdx, final List<String> dexSegments, final Dex894TransactionSet dexTx) {
        LOGGER.debug("parseDexTransactionLoop:" + startingIdx);
        String segment = dexSegments.get(startingIdx);
        String segmentId = this.extractSegmentIdentifier(segment);

        int segmentIdx = startingIdx;
        if (!LOOP_HEADER_ID.equals(segmentId)) {
            throw new X12ParserException("Expected start of DEX transaction loop");
        } else {
            // LS line
            this.parseLoopHeader(segment, dexTx);

            // process all of the items between LS and LE
            // stop when the last line processed is an LE
            segmentIdx++;
            do {
                // get the segment
                segment = dexSegments.get(segmentIdx);
                segmentId = this.extractSegmentIdentifier(segment);

                if (G83_ID.equals(segmentId)) {
                    segmentIdx = this.parseDexItem(segmentIdx, dexSegments, dexTx);
                } else if (LOOP_TRAILER_ID.equals(segmentId)) {
                    this.parseLoopTrailer(segment, dexTx);
                    // increase the index
                    segmentIdx++;
                } else {
                    // unexpected segment
                    this.throwParserException(G83_ID, segmentId);
                }

            } while (!LOOP_TRAILER_ID.equals(segmentId));
        }

        return segmentIdx;
    }

    /**
     * parses one DEX item in a transaction loop
     * @param startingIdx which segment line to start with
     * @param dexSegments
     * @param dexTx
     * @return the starting point for the next item
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected int parseDexItem(final int startingIdx, final List<String> dexSegments, final Dex894TransactionSet dexTx) {
        LOGGER.debug("parseDexItem:" + startingIdx);

        int segmentIdx = startingIdx;
        String segment = dexSegments.get(segmentIdx);

        // G83 marks a new DEX item
        Dex894Item dexItem = new Dex894Item();
        this.parseG83(segment, dexItem);

        // check next segments
        segment = dexSegments.get(++segmentIdx);
        String segmentId = this.extractSegmentIdentifier(segment);

        // G22 pricing (optional)
        if (G22_ID.equals(segmentId)) {
            this.parseG22(segment, dexItem);
            // update next segment & segment id
            segment = dexSegments.get(++segmentIdx);
            segmentId = this.extractSegmentIdentifier(segment);
        }

        // G72 allowance (optional)
        if (G72_ID.equals(segmentId)) {
            this.parseG72(segment, dexItem);
            // update next segment & segment id
            segment = dexSegments.get(++segmentIdx);
            segmentId = this.extractSegmentIdentifier(segment);
        }

        dexTx.addItem(dexItem);

        return segmentIdx;
    }

    /**
     * parse the DEX envelope header (DXS)
     * @param headerSegment
     * @param dex
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseApplicationHeader(String headerSegment, Dex894 dex) {
        LOGGER.debug(this.extractSegmentIdentifier(headerSegment));
        List<String> elements = this.splitSegmentIntoDataElements(headerSegment);

        String segmentIdentifer = this.retreiveElementFromSegment(elements, 0);
        if (APPLICATION_HEADER_ID.equals(segmentIdentifer)) {
            dex.setSenderCommId(this.retreiveElementFromSegment(elements, 1));
            dex.setFunctionalId(this.retreiveElementFromSegment(elements, 2));
            dex.setVersion(this.retreiveElementFromSegment(elements, 3));
            this.parseVersion(dex);
            dex.setHeaderTransmissionControlNumber(this.retreiveElementFromSegment(elements, 4));
            dex.setReceiverCommId(this.retreiveElementFromSegment(elements, 5));
            dex.setTestIndicator(this.retreiveElementFromSegment(elements, 6));
        } else {
            throwParserException(APPLICATION_HEADER_ID, segmentIdentifer);
        }
    }

    protected void parseVersion(Dex894 dex) {
        dex.setVersionNumber(VersionUtil.parseVersion(dex.getVersion()));
    }

    /**
     * parses the ST segment
     * The transaction set control number should increment with every transaction set so that all transaction sets have a different number.
     *
     * @param segment
     * @param dexTx
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseTransactionSetHeader(String segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(this.extractSegmentIdentifier(segment));
        List<String> elements = this.splitSegmentIntoDataElements(segment);

        String segmentIdentifier = this.retreiveElementFromSegment(elements, 0);
        if (TRANSACTION_SET_HEADER_ID.equals(segmentIdentifier)) {
            dexTx.setTransactionSetIdentifierCode(this.retreiveElementFromSegment(elements, 1));
            dexTx.setHeaderControlNumber(this.retreiveElementFromSegment(elements, 2));
        } else {
            throwParserException(TRANSACTION_SET_HEADER_ID, segmentIdentifier);
        }

    }

    /**
     * parse the G82 segment (Base Record Identifier)
     * @param segment
     * @param dexTx
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseG82(String segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(this.extractSegmentIdentifier(segment));
        List<String> elements = this.splitSegmentIntoDataElements(segment);

        String segmentIdentifier = this.retreiveElementFromSegment(elements, 0);
        if (G82_ID.equals(segmentIdentifier)) {
            dexTx.setDebitCreditFlag(InvoiceType.convertDebitCreditFlag(this.retreiveElementFromSegment(elements, 1)));
            dexTx.setSupplierNumber(this.retreiveElementFromSegment(elements, 2));
            dexTx.setReceiverDuns(this.retreiveElementFromSegment(elements, 3));
            dexTx.setReceiverLocation(this.retreiveElementFromSegment(elements, 4));
            dexTx.setSupplierDuns(this.retreiveElementFromSegment(elements, 5));
            dexTx.setSupplierLocation(this.retreiveElementFromSegment(elements, 6));
            dexTx.setTransactionDate(this.retreiveElementFromSegment(elements, 7));
            dexTx.setPurchaseOrderNumber(this.retreiveElementFromSegment(elements, 8));
            dexTx.setPurchaseOrderDate(this.retreiveElementFromSegment(elements, 9));
        } else {
            throwParserException(G82_ID, segmentIdentifier);
        }
    }

    /**
     * parse the LS segement
     * @param segment
     * @param dexTx
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseLoopHeader(String segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(this.extractSegmentIdentifier(segment));
        List<String> elements = this.splitSegmentIntoDataElements(segment);

        String segmentIdentifier = this.retreiveElementFromSegment(elements, 0);
        if (!LOOP_HEADER_ID.equals(segmentIdentifier)) {
            throwParserException(LOOP_HEADER_ID, segmentIdentifier);
        }
    }

    /**
     * G83 provides the basic and most often used line item
     * data for the delivery and return.
     * Use it at the start of each item loop.
     *
     * @param segment
     * @param dexItem
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseG83(String segment, Dex894Item dexItem) {
        LOGGER.debug(this.extractSegmentIdentifier(segment));
        List<String> elements = this.splitSegmentIntoDataElements(segment);

        String segmentIdentifier = this.retreiveElementFromSegment(elements, 0);
        if (G83_ID.equals(segmentIdentifier)) {
            // this will do a simple parsing of the G83 elements
            // a separate utility will need to determine the retail selling unit
            dexItem.setItemSequenceNumber(this.retreiveElementFromSegment(elements, 1));
            dexItem.setQuantity(this.convertStringToBigDecimal(this.retreiveElementFromSegment(elements, 2), 3));
            dexItem.setUom(UnitMeasure.convertUnitMeasure(this.retreiveElementFromSegment(elements, 3)));
            dexItem.setUpc(this.retreiveElementFromSegment(elements, 4));
            dexItem.setConsumerProductQualifier(ProductQualifier.convertProductQualifier(this.retreiveElementFromSegment(elements, 5)));
            dexItem.setConsumerProductId(this.retreiveElementFromSegment(elements, 6));
            dexItem.setCaseUpc(this.retreiveElementFromSegment(elements, 7));
            dexItem.setItemListCost(this.convertStringToBigDecimal(this.retreiveElementFromSegment(elements, 8), 2));
            dexItem.setPackCount(this.convertStringToInteger(this.retreiveElementFromSegment(elements, 9)));
            dexItem.setItemDescription(this.retreiveElementFromSegment(elements, 10));
            dexItem.setCaseProductQualifier(ProductQualifier.convertProductQualifier(this.retreiveElementFromSegment(elements, 11)));
            dexItem.setCaseProductId(this.retreiveElementFromSegment(elements, 12));
            dexItem.setInnerPackCount(this.convertStringToInteger(this.retreiveElementFromSegment(elements, 13)));
        } else {
            throwParserException(G83_ID, segmentIdentifier);
        }
    }


    /**
     * parse G22
     * This data segment specifies pricing information
     *
     * @param segment
     * @param dexItem
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseG22(String segment, Dex894Item dexItem) {
        LOGGER.debug(this.extractSegmentIdentifier(segment));
    }

    /**
     * parse G72
     * This data segment specifies allowances or charges that are applied to the list item cost
     *
     * @param segment
     * @param dexItem
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseG72(String segment, Dex894Item dexItem) {
        LOGGER.debug(this.extractSegmentIdentifier(segment));
        List<String> elements = this.splitSegmentIntoDataElements(segment);

        String segmentIdentifier = this.retreiveElementFromSegment(elements, 0);
        if (G72_ID.equals(segmentIdentifier)) {
            Dex894Allowance dexAllowance = new Dex894Allowance();
            dexItem.setAllowance(dexAllowance);
            dexAllowance.setAllowanceCode(this.retreiveElementFromSegment(elements, 1));
            dexAllowance.setMethodOfHandlingCode(this.retreiveElementFromSegment(elements, 2));
            dexAllowance.setAllowanceNumber(this.retreiveElementFromSegment(elements, 3));
            dexAllowance.setExceptionNumber(this.retreiveElementFromSegment(elements, 4));
            dexAllowance.setAllowanceRate(this.convertStringToBigDecimal(this.retreiveElementFromSegment(elements, 5), 4));
            dexAllowance.setAllowanceQuantity(this.convertStringToBigDecimal(this.retreiveElementFromSegment(elements, 6), 3));
            dexAllowance.setAllowanceUom(UnitMeasure.convertUnitMeasure(this.retreiveElementFromSegment(elements, 7)));
            dexAllowance.setAllowanceAmount(this.convertStringToBigDecimal(this.retreiveElementFromSegment(elements, 8), 2));
            dexAllowance.setAllowancePercent(this.convertStringToBigDecimal(this.retreiveElementFromSegment(elements, 9), 3));
            dexAllowance.setOptionNumber(this.retreiveElementFromSegment(elements, 10));
        } else {
            throwParserException(G72_ID, segmentIdentifier);
        }
    }

    /**
     * parse LE segment
     *
     * @param segment
     * @param dexTx
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseLoopTrailer(String segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(this.extractSegmentIdentifier(segment));
        List<String> elements = this.splitSegmentIntoDataElements(segment);

        String segmentIdentifier = this.retreiveElementFromSegment(elements, 0);
        if (!LOOP_TRAILER_ID.equals(segmentIdentifier)) {
            throwParserException(LOOP_TRAILER_ID, segmentIdentifier);
        }
    }

    /**
     * parse G84 segment (Summary Data used to check transmission)
     *
     * @param segment
     * @param dexTx
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseG84(String segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(this.extractSegmentIdentifier(segment));
        List<String> elements = this.splitSegmentIntoDataElements(segment);

        String segmentIdentifier = this.retreiveElementFromSegment(elements, 0);
        if (G84_ID.equals(segmentIdentifier)) {
            dexTx.setTransactionTotalQuantity(this.convertStringToBigDecimal(this.retreiveElementFromSegment(elements, 1), 3));
            dexTx.setTransactionTotalAmount(this.convertStringToBigDecimal(this.retreiveElementFromSegment(elements, 2), 2));
            dexTx.setTransactionTotalDepositAmount(this.convertStringToBigDecimal(this.retreiveElementFromSegment(elements, 3), 2));
        } else {
            throwParserException(G84_ID, segmentIdentifier);
        }
    }

    /**
     * parse G85 segment (Integrity Check)
     *
     * @param segment
     * @param dexTx
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseG85(String segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(this.extractSegmentIdentifier(segment));
        List<String> elements = this.splitSegmentIntoDataElements(segment);

        String segmentIdentifier = this.retreiveElementFromSegment(elements, 0);
        if (G85_ID.equals(segmentIdentifier)) {
            dexTx.setIntegrityCheckValue(this.retreiveElementFromSegment(elements, 1));
        } else {
            throwParserException(G85_ID, segmentIdentifier);
        }
    }

    /**
     * parse G86 segment (Signature)
     *
     * @param segment
     * @param dexTx
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseG86(String segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(this.extractSegmentIdentifier(segment));
        List<String> elements = this.splitSegmentIntoDataElements(segment);

        String segmentIdentifier = this.retreiveElementFromSegment(elements, 0);
        if (G86_ID.equals(segmentIdentifier)) {
            dexTx.setElectronicSignature(this.retreiveElementFromSegment(elements, 1));
            dexTx.setSignatureName(this.retreiveElementFromSegment(elements, 2));
        } else {
            throwParserException(G86_ID, segmentIdentifier);
        }
    }

    /**
     * parse SE segment
     * @param segment
     * @param dexTx
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseTransactionSetTrailer(String segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(this.extractSegmentIdentifier(segment));
        List<String> elements = this.splitSegmentIntoDataElements(segment);

        String segmentIdentifier = this.retreiveElementFromSegment(elements, 0);
        if (TRANSACTION_SET_TRAILER_ID.equals(segmentIdentifier)) {
            dexTx.setExpectedNumberOfSegments(this.convertStringToInteger(this.retreiveElementFromSegment(elements, 1)));
            dexTx.setTrailerControlNumber(this.retreiveElementFromSegment(elements, 2));
        } else {
            throwParserException(TRANSACTION_SET_TRAILER_ID, segmentIdentifier);
        }
    }

    /**
     * parse the DEX envelope trailer (DXE)
     * @param trailerSegment
     * @param dex
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseApplicationTrailer(String trailerSegment, Dex894 dex) {
        LOGGER.debug(this.extractSegmentIdentifier(trailerSegment));
        List<String> elements = this.splitSegmentIntoDataElements(trailerSegment);

        String segmentIdentifier = this.retreiveElementFromSegment(elements, 0);
        if (APPLICATION_TRAILER_ID.equals(segmentIdentifier)) {
            dex.setTrailerTransmissionControlNumber(this.retreiveElementFromSegment(elements, 1));
            dex.setNumberOfTransactions(this.convertStringToInteger(this.retreiveElementFromSegment(elements, 2)));
        } else {
            throwParserException(APPLICATION_TRAILER_ID, segmentIdentifier);
        }
    }

    protected void throwParserException(String expectedSegmentId, String actualSegmentId) {
        StringBuilder sb = new StringBuilder("expected ");
        sb.append(expectedSegmentId);
        sb.append(" segment but found ");
        sb.append(actualSegmentId);
        throw new X12ParserException(new X12ErrorDetail(actualSegmentId, null, sb.toString()));
    }

    protected String retreiveElementFromSegment(List<String> elements, int listIndex) {
        if (elements.size() >= listIndex + 1) {
            String value = elements.get(listIndex);
            return StringUtils.isEmpty(value) ? null : value;
        } else {
            return null;
        }
    }

//    /**
//     * moved to default method on X12Parser
//     */
//    protected List<String> splitSourceDataIntoSegments(String sourceData) {
//        return Arrays.asList(sourceData.split("\\r?\\n"));
//    }
//
//    /**
//     * moved to default method on X12Parser
//     */
//    protected List<String> splitSegmentIntoDataElements(String segment) {
//        return Arrays.asList(segment.split("\\*"));
//    }

    /**
     *
     * @param segmentIdx index denoting which segment/line in the DEX file to work on
     * @param dexSegments the list of segments/lines in the DEX file
     * @return segment identifier
     */
    protected String segmentIdentifier(int segmentIdx, List<String> dexSegments) {
        String segment = dexSegments.get(segmentIdx);
        return this.extractSegmentIdentifier(segment);
    }

//    protected String extractSegmentIdentifier(String segment) {
//        return segment.substring(0, segment.indexOf("*"));
//    }

    protected int findLastSegmentIndex(List<String> dexSegments) {
        return dexSegments.size() - 1;
    }

    protected BigDecimal convertStringToBigDecimal(String theString, int decimalPlaces) {
        BigDecimal returnValue = null;
        try {
            if (theString != null && theString.trim().length() > 0) {
                returnValue = new BigDecimal(theString).setScale(decimalPlaces, RoundingMode.HALF_UP);
            }
        } catch (NumberFormatException e) {
            throw new X12ParserException("Invalid numeric value");
        }
        return returnValue;
    }

    protected Integer convertStringToInteger(String theString) {
        Integer returnInteger = null;

        try {
            if (theString != null && theString.trim().length() > 0) {
                returnInteger = Integer.valueOf(theString);
            }
        } catch (NumberFormatException e) {
            throw new X12ParserException("Invalid numeric value");
        }

        return returnInteger;
    }
}
