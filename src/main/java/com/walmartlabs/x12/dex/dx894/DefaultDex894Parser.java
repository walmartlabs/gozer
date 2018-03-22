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

import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
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
 * ------------ G83 (Item Detail)
 * ------------ G72
 *
 * ------------ G83
 * ------------ G72
 * -------- LE
 * ---- G84 (Transaction summary)
 * ---- G86 (electronic signature)
 * ---- G85 (integrity check)
 * -- SE
 *
 */
public class DefaultDex894Parser implements Dex894Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDex894Parser.class);

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
            List<String> dexLines = this.splitDexIntoSegments(sourceData);

            if (!this.isValidEnvelope(dexLines)) {
                throw new X12ParserException("invalid DEX envelope");
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
                    throw new X12ParserException("invalid DEX message: missing mandatory fields");
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
        String segmentId = this.segmentIdentifier(segment);

        int segmentIdx = startingIdx;
        if (!TRANSACTION_SET_HEADER_ID.equals(segmentId)) {
            throw new X12ParserException("expected start of DEX transaction");
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
            segmentId = this.segmentIdentifier(segment);

            // G84 line (conditional)
            if (G84_ID.equals(segmentId)) {
                this.parseG84(segment, dexTx);
                // update next segment & segment id
                segment = dexSegments.get(++segmentIdx);
                segmentId = this.segmentIdentifier(segment);
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
        String segmentId = this.segmentIdentifier(segment);

        int segmentIdx = startingIdx;
        if (!LOOP_HEADER_ID.equals(segmentId)) {
            throw new X12ParserException("expected start of DEX transaction loop");
        } else {
            // LS line
            this.parseLoopHeader(segment, dexTx);

            // process all of the items between LS and LE
            // stop when the last line processed is an LE
            segmentIdx++;
            do {
                // get the segment
                segment = dexSegments.get(segmentIdx);
                segmentId = this.segmentIdentifier(segment);

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

        // check next segment
        // for G72 segment
        segment = dexSegments.get(++segmentIdx);
        String segmentId = this.segmentIdentifier(segment);
        if (G72_ID.equals(segmentId)) {
            this.parseG72(segment, dexItem);
            // increase the index
            segmentIdx++;
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
        LOGGER.debug(this.segmentIdentifier(headerSegment));
        List<String> elements = this.splitSegment(headerSegment);
        if (APPLICATION_HEADER_ID.equals(elements.get(0))) {
            dex.setSenderCommId(elements.get(1));
            dex.setFunctionalId(elements.get(2));
            dex.setVersion(elements.get(3));
            dex.setHeaderTransmissionControlNumber(elements.get(4));
            if (elements.size() >= 6) {
                // optional
                dex.setReceiverCommId(elements.get(5));
            }
            if (elements.size() >= 7) {
                // optional
                dex.setTestIndicator(elements.get(6));
            }
        } else {
            throwParserException(APPLICATION_HEADER_ID, elements.get(0));
        }
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
        LOGGER.debug(this.segmentIdentifier(segment));
        List<String> elements = this.splitSegment(segment);
        if (TRANSACTION_SET_HEADER_ID.equals(elements.get(0))) {
            dexTx.setTransactionSetIdentifierCode(elements.get(1));
            dexTx.setHeaderControlNumber(elements.get(2));
        } else {
            throwParserException(TRANSACTION_SET_HEADER_ID, elements.get(0));
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
        LOGGER.debug(this.segmentIdentifier(segment));
        List<String> elements = this.splitSegment(segment);
        if (G82_ID.equals(elements.get(0))) {
            dexTx.setDebitCreditFlag(elements.get(1));
            dexTx.setSupplierNumber(elements.get(2));
            dexTx.setReceiverDuns(elements.get(3));
            dexTx.setReceiverLocation(elements.get(4));
            dexTx.setSupplierDuns(elements.get(5));
            dexTx.setSupplierLocation(elements.get(6));
            dexTx.setTransactionDate(elements.get(7));
            if (elements.size() >= 9) {
                // optional
                dexTx.setPurchaseOrderNumber(elements.get(8));
            }
            if (elements.size() >= 10) {
                // optional
                dexTx.setPurchaseOrderDate(elements.get(9));
            }
        } else {
            throwParserException(G82_ID, elements.get(0));
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
        LOGGER.debug(this.segmentIdentifier(segment));
        List<String> elements = this.splitSegment(segment);
        if (!LOOP_HEADER_ID.equals(elements.get(0))) {
            throwParserException(LOOP_HEADER_ID, elements.get(0));
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
        LOGGER.debug(this.segmentIdentifier(segment));
        List<String> elements = this.splitSegment(segment);
        if (G83_ID.equals(elements.get(0))) {
            // this will do a simple parsing of the G83 elements
            // a separate utility will need to determine the retail selling unit
            dexItem.setItemSequenceNumber(elements.get(1));
            dexItem.setQuantity(this.convertStringToBigDecimal(elements.get(2), 3));
            dexItem.setUom(UnitMeasure.convertUnitMeasure(elements.get(3)));
            if (elements.size() >= 5) {
                // G8304: UPC Consumer Package Code
                // no longer used starting w/ version 5010
                dexItem.setUpc(elements.get(4));
            }
            if (elements.size() >= 6) {
                // G8305: Product Qualifier for G8306
                String g8305 = elements.get(5);
                if (g8305 != null && g8305.trim().length() > 0) {
                    dexItem.setConsumerProductQualifier(ProductQualifier.convertyProductQualifier(elements.get(5)));
                }
            }
            if (elements.size() >= 7) {
                // G8306: Product Id
                dexItem.setConsumerProductId(elements.get(6));
            }
            if (elements.size() >= 8) {
                // G8307: 12 digit UPC Case Code
                // no longer used starting w/ version 5010
                dexItem.setCaseUpc(elements.get(7));
            }
            if (elements.size() >= 9) {
                // G8308: Item List Cost
                dexItem.setItemListCost(this.convertStringToBigDecimal(elements.get(8), 2));
            }
            if (elements.size() >= 10) {
                // G8309: Pack
                dexItem.setPackCount(this.convertStringToInteger(elements.get(9)));
            }
            if (elements.size() >= 11) {
                // G8310: Cash Register Item Description
                dexItem.setItemDescription(elements.get(10));
            }
            if (elements.size() >= 12) {
                // G8311: Product Qualifier for G8312
                dexItem.setCaseProductQualifier(ProductQualifier.convertyProductQualifier(elements.get(11)));
            }
            if (elements.size() >= 13) {
                // G8312: Product Id
                dexItem.setCaseProductId(elements.get(12));
            }
            if (elements.size() >= 14) {
                // G8313: Inner Pack
                dexItem.setInnerPackCount(this.convertStringToInteger(elements.get(13)));
            }
        } else {
            throwParserException(G83_ID, elements.get(0));
        }
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
        LOGGER.debug(this.segmentIdentifier(segment));
        // TODO: G72 parsing
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
        LOGGER.debug(this.segmentIdentifier(segment));
        List<String> elements = this.splitSegment(segment);
        if (!LOOP_TRAILER_ID.equals(elements.get(0))) {
            throwParserException(LOOP_TRAILER_ID, elements.get(0));
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
        LOGGER.debug(this.segmentIdentifier(segment));
        // TODO: G84 parsing
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
        LOGGER.debug(this.segmentIdentifier(segment));
        List<String> elements = this.splitSegment(segment);
        if (G85_ID.equals(elements.get(0))) {
            dexTx.setIntegrityCheckValue(elements.get(1));
        } else {
            throwParserException(G85_ID, elements.get(0));
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
        LOGGER.debug(this.segmentIdentifier(segment));
        List<String> elements = this.splitSegment(segment);
        if (G86_ID.equals(elements.get(0))) {
            dexTx.setElectronicSignature(elements.get(1));
            if (elements.size() >= 3) {
                // optional
                dexTx.setSignatureName(elements.get(2));
            }
        } else {
            throwParserException(G86_ID, elements.get(0));
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
        LOGGER.debug(this.segmentIdentifier(segment));
        List<String> elements = this.splitSegment(segment);
        if (TRANSACTION_SET_TRAILER_ID.equals(elements.get(0))) {
            dexTx.setExpectedNumberOfSegments(this.convertStringToInteger(elements.get(1)));
            dexTx.setTrailerControlNumber(elements.get(2));
        } else {
            throwParserException(TRANSACTION_SET_TRAILER_ID, elements.get(0));
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
        LOGGER.debug(this.segmentIdentifier(trailerSegment));
        List<String> elements = this.splitSegment(trailerSegment);
        if (APPLICATION_TRAILER_ID.equals(elements.get(0))) {
            dex.setTrailerTransmissionControlNumber(elements.get(1));
            dex.setNumberOfTransactions(this.convertStringToInteger(elements.get(2)));
        } else {
            throwParserException(APPLICATION_TRAILER_ID, elements.get(0));
        }
    }

    protected void throwParserException(String expectedSegmentId, String actualSegmentId) {
        StringBuilder sb = new StringBuilder("expected ");
        sb.append(expectedSegmentId);
        sb.append(" segment but found ");
        sb.append(actualSegmentId);
        throw new X12ParserException(new X12ErrorDetail(actualSegmentId, null, sb.toString()));
    }

    protected List<String> splitDexIntoSegments(String sourceData) {
        return Arrays.asList(sourceData.split("\\r?\\n"));
    }

    protected List<String> splitSegment(String segment) {
        return Arrays.asList(segment.split("\\*"));
    }

    protected String segmentIdentifier(int segmentIdx, List<String> dexSegments) {
        String segment = dexSegments.get(segmentIdx);
        return this.segmentIdentifier(segment);
    }

    protected String segmentIdentifier(String segment) {
        return segment.substring(0, segment.indexOf("*"));
    }

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
            throw new X12ParserException("invalid numeric value");
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
            throw new X12ParserException("invalid numeric value");
        }

        return returnInteger;
    }
}
