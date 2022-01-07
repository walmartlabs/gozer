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
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.types.InvoiceType;
import com.walmartlabs.x12.types.ProductQualifier;
import com.walmartlabs.x12.types.UnitMeasure;
import com.walmartlabs.x12.util.ConversionUtil;
import com.walmartlabs.x12.util.SourceToSegmentUtil;
import com.walmartlabs.x12.util.X12ParsingUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.IntStream;

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
public class DefaultDex894Parser implements X12Parser<Dex894> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultDex894Parser.class);

    public static final String DEX_HEADER_ID = "DXS";
    public static final String DEX_TRAILER_ID = "DXE";
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

        if (StringUtils.isNotEmpty(sourceData)) {
            dex = new Dex894();
            List<X12Segment> segmentLines = SourceToSegmentUtil.splitSourceDataIntoSegments(sourceData);

            if (!this.isValidEnvelope(segmentLines)) {
                throw new X12ParserException("invalid envelope");
            } else {
                try {
                    int lastSegmentIndex = this.findLastSegmentIndex(segmentLines);
                    // application header
                    this.parseApplicationHeader(segmentLines.get(0), dex);

                    // parse transactions...
                    int idx = 1;
                    do {
                        Dex894TransactionSet dexTx = new Dex894TransactionSet();
                        idx = this.parseDexTransaction(idx, segmentLines, dexTx);
                        dex.addTransaction(dexTx);
                    } while (idx < lastSegmentIndex);

                    // application trailer
                    this.parseApplicationTrailer(segmentLines.get(lastSegmentIndex), dex);
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new X12ParserException("Invalid DEX message: missing mandatory fields");
                } catch (StringIndexOutOfBoundsException e) {
                    throw new X12ParserException("Invalid DEX message: unexpected segments");
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
    protected boolean isValidEnvelope(List<X12Segment> dexSegments) {
        return X12ParsingUtil.isValidEnvelope(dexSegments, DEX_HEADER_ID, DEX_TRAILER_ID);
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
    protected int parseDexTransaction(final int startingIdx, final List<X12Segment> dexSegments, final Dex894TransactionSet dexTx) {
        LOGGER.debug("parseDexTransaction:" + startingIdx);
        X12Segment segment = dexSegments.get(startingIdx);
        String segmentId = segment.getIdentifier();

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
                segment = dexSegments.get(segmentIdx);
                segmentId = segment.getIdentifier();
            } while (TRANSACTION_SET_HEADER_ID.equals(segmentId));

            // next set of lines after the transaction loop can vary
            segment = dexSegments.get(segmentIdx);
            segmentId = segment.getIdentifier();

            // G84 line (conditional)
            if (G84_ID.equals(segmentId)) {
                this.parseG84(segment, dexTx);
                // update next segment & segment id
                segment = dexSegments.get(++segmentIdx);
                segmentId = segment.getIdentifier();
            }

            // G86 line (optional)
            if (G86_ID.equals(segmentId)) {
                this.parseG86(segment, dexTx);
                // update next segment & segment id
                segment = dexSegments.get(++segmentIdx);
            }

            // store the entire transaction (ST thru G86)
            // for possible integrity verification
            StringBuilder transactionData = new StringBuilder();
            IntStream.range(startingIdx, segmentIdx)
                .forEach(i -> {
                    transactionData.append(dexSegments.get(i)).append("\r\n");
                });
            dexTx.setTransactionData(transactionData.toString());

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
    protected int parseDexTransactionLoop(final int startingIdx, final List<X12Segment> dexSegments, final Dex894TransactionSet dexTx) {
        LOGGER.debug("parseDexTransactionLoop:" + startingIdx);
        X12Segment segment = dexSegments.get(startingIdx);
        String segmentId = segment.getIdentifier();

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
                segmentId = segment.getIdentifier();

                if (G83_ID.equals(segmentId)) {
                    segmentIdx = this.parseDexItem(segmentIdx, dexSegments, dexTx);
                } else if (LOOP_TRAILER_ID.equals(segmentId)) {
                    this.parseLoopTrailer(segment, dexTx);
                    // increase the index
                    segmentIdx++;
                } else {
                    // unexpected segment
                    this.handleUnexpectedSegment(G83_ID, segmentId);
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
    protected int parseDexItem(final int startingIdx, final List<X12Segment> dexSegments, final Dex894TransactionSet dexTx) {
        LOGGER.debug("parseDexItem:" + startingIdx);

        int segmentIdx = startingIdx;
        X12Segment segment = dexSegments.get(segmentIdx);

        // G83 marks a new DEX item
        Dex894Item dexItem = new Dex894Item();
        this.parseG83(segment, dexItem);

        // check next segments
        segment = dexSegments.get(++segmentIdx);
        String segmentId = segment.getIdentifier();

        // G22 pricing (optional)
        if (G22_ID.equals(segmentId)) {
            this.parseG22(segment, dexItem);
            // update next segment & segment id
            segment = dexSegments.get(++segmentIdx);
            segmentId = segment.getIdentifier();
        }

        // G72 allowance (optional)
        while (G72_ID.equals(segmentId)) {
            this.parseG72(segment, dexItem);
            // update next segment & segment id
            segment = dexSegments.get(++segmentIdx);
            segmentId = segment.getIdentifier();
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
    protected void parseApplicationHeader(X12Segment headerSegment, Dex894 dex) {
        LOGGER.debug(headerSegment.getIdentifier());

        String segmentIdentifer = headerSegment.getIdentifier();
        if (DEX_HEADER_ID.equals(segmentIdentifer)) {
            dex.setSenderCommId(headerSegment.getElement(1));
            dex.setFunctionalId(headerSegment.getElement(2));
            dex.setVersion(headerSegment.getElement(3));
            this.parseVersion(dex);
            dex.setHeaderTransmissionControlNumber(headerSegment.getElement(4));
            dex.setReceiverCommId(headerSegment.getElement(5));
            dex.setTestIndicator(headerSegment.getElement(6));
        } else {
            handleUnexpectedSegment(DEX_HEADER_ID, segmentIdentifer);
        }
    }

    protected void parseVersion(Dex894 dex) {
        Integer version = X12ParsingUtil.parseVersion(dex.getVersion());
        if (version == null) {
            throw new X12ParserException(new X12ErrorDetail(DefaultDex894Parser.DEX_HEADER_ID, "DXS03", "Invalid version format"));
        } else {
            dex.setVersionNumber(version);
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
    protected void parseTransactionSetHeader(X12Segment segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (TRANSACTION_SET_HEADER_ID.equals(segmentIdentifier)) {
            dexTx.setTransactionSetIdentifierCode(segment.getElement(1));
            dexTx.setHeaderControlNumber(segment.getElement(2));
        } else {
            handleUnexpectedSegment(TRANSACTION_SET_HEADER_ID, segmentIdentifier);
        }

    }

    /**
     * parse the G82 segment (Base Record Identifier)
     * @param segment
     * @param dexTx
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseG82(X12Segment segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (G82_ID.equals(segmentIdentifier)) {
            dexTx.setDebitCreditFlag(InvoiceType.convert(segment.getElement(1)));
            dexTx.setSupplierNumber(segment.getElement(2));
            dexTx.setReceiverDuns(segment.getElement(3));
            dexTx.setReceiverLocation(segment.getElement(4));
            dexTx.setSupplierDuns(segment.getElement(5));
            dexTx.setSupplierLocation(segment.getElement(6));
            dexTx.setTransactionDate(segment.getElement(7));
            dexTx.setPurchaseOrderNumber(segment.getElement(8));
            dexTx.setPurchaseOrderDate(segment.getElement(9));
        } else {
            handleUnexpectedSegment(G82_ID, segmentIdentifier);
        }
    }

    /**
     * parse the LS segement
     * @param segment
     * @param dexTx
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseLoopHeader(X12Segment segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (!LOOP_HEADER_ID.equals(segmentIdentifier)) {
            handleUnexpectedSegment(LOOP_HEADER_ID, segmentIdentifier);
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
    protected void parseG83(X12Segment segment, Dex894Item dexItem) {
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (G83_ID.equals(segmentIdentifier)) {
            // this will do a simple parsing of the G83 elements
            // a separate utility will need to determine the retail selling unit
            dexItem.setItemSequenceNumber(segment.getElement(1));
            dexItem.setQuantity(ConversionUtil.convertStringToBigDecimal(segment.getElement(2), 3));        
            dexItem.setUom(UnitMeasure.convert(segment.getElement(3)));
            dexItem.setUpc(segment.getElement(4));
            dexItem.setConsumerProductQualifier(ProductQualifier.convert(segment.getElement(5)));
            dexItem.setConsumerProductId(segment.getElement(6));
            dexItem.setCaseUpc(segment.getElement(7));
            dexItem.setItemListCost(ConversionUtil.convertStringToBigDecimal(segment.getElement(8), 4));
            dexItem.setPackCount(ConversionUtil.convertStringToInteger(segment.getElement(9)));
            dexItem.setItemDescription(segment.getElement(10));
            dexItem.setCaseProductQualifier(ProductQualifier.convert(segment.getElement(11)));
            dexItem.setCaseProductId(segment.getElement(12));
            dexItem.setInnerPackCount(ConversionUtil.convertStringToInteger(segment.getElement(13)));
        } else {
            handleUnexpectedSegment(G83_ID, segmentIdentifier);
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
    protected void parseG22(X12Segment segment, Dex894Item dexItem) {
        LOGGER.debug(segment.getIdentifier());
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
    protected void parseG72(X12Segment segment, Dex894Item dexItem) {
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (G72_ID.equals(segmentIdentifier)) {
            Dex894Allowance dexAllowance = new Dex894Allowance();
            dexItem.addAllowance(dexAllowance);
            dexAllowance.setAllowanceCode(segment.getElement(1));
            dexAllowance.setMethodOfHandlingCode(segment.getElement(2));
            dexAllowance.setAllowanceNumber(segment.getElement(3));
            dexAllowance.setExceptionNumber(segment.getElement(4));
            dexAllowance.setAllowanceRate(ConversionUtil.convertStringToBigDecimal(segment.getElement(5), 4));
            dexAllowance.setAllowanceQuantity(ConversionUtil.convertStringToBigDecimal(segment.getElement(6), 3));
            dexAllowance.setAllowanceUom(UnitMeasure.convert(segment.getElement(7)));
            dexAllowance.setAllowanceAmount(ConversionUtil.convertStringToBigDecimal(segment.getElement(8), 2));
            dexAllowance.setAllowancePercent(ConversionUtil.convertStringToBigDecimal(segment.getElement(9), 3));
            dexAllowance.setOptionNumber(segment.getElement(10));
        } else {
            handleUnexpectedSegment(G72_ID, segmentIdentifier);
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
    protected void parseLoopTrailer(X12Segment segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (!LOOP_TRAILER_ID.equals(segmentIdentifier)) {
            handleUnexpectedSegment(LOOP_TRAILER_ID, segmentIdentifier);
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
    protected void parseG84(X12Segment segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (G84_ID.equals(segmentIdentifier)) {
            dexTx.setTransactionTotalQuantity(ConversionUtil.convertStringToBigDecimal(segment.getElement(1), 3));
            dexTx.setTransactionTotalAmount(ConversionUtil.convertStringToBigDecimal(segment.getElement(2), 2));
            dexTx.setTransactionTotalDepositAmount(ConversionUtil.convertStringToBigDecimal(segment.getElement(3), 2));
        } else {
            handleUnexpectedSegment(G84_ID, segmentIdentifier);
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
    protected void parseG85(X12Segment segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (G85_ID.equals(segmentIdentifier)) {
            dexTx.setIntegrityCheckValue(segment.getElement(1));
        } else {
            handleUnexpectedSegment(G85_ID, segmentIdentifier);
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
    protected void parseG86(X12Segment segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (G86_ID.equals(segmentIdentifier)) {
            dexTx.setElectronicSignature(segment.getElement(1));
            dexTx.setSignatureName(segment.getElement(2));
        } else {
            handleUnexpectedSegment(G86_ID, segmentIdentifier);
        }
    }

    /**
     * parse SE segment
     * @param segment
     * @param dexTx
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseTransactionSetTrailer(X12Segment segment, Dex894TransactionSet dexTx) {
        LOGGER.debug(segment.getIdentifier());

        String segmentIdentifier = segment.getIdentifier();
        if (TRANSACTION_SET_TRAILER_ID.equals(segmentIdentifier)) {
            dexTx.setExpectedNumberOfSegments(ConversionUtil.convertStringToInteger(segment.getElement(1)));
            dexTx.setTrailerControlNumber(segment.getElement(2));
        } else {
            handleUnexpectedSegment(TRANSACTION_SET_TRAILER_ID, segmentIdentifier);
        }
    }

    /**
     * parse the DEX envelope trailer (DXE)
     * @param trailerSegment
     * @param dex
     * @throws X12ParserException if the DEX segment is invalid
     * @throws ArrayIndexOutOfBoundsException if a mandatory element is missing
     */
    protected void parseApplicationTrailer(X12Segment trailerSegment, Dex894 dex) {
        LOGGER.debug(trailerSegment.getIdentifier());

        String segmentIdentifier = trailerSegment.getIdentifier();
        if (DEX_TRAILER_ID.equals(segmentIdentifier)) {
            dex.setTrailerTransmissionControlNumber(trailerSegment.getElement(1));
            dex.setNumberOfTransactions(ConversionUtil.convertStringToInteger(trailerSegment.getElement(2)));
        } else {
            handleUnexpectedSegment(DEX_TRAILER_ID, segmentIdentifier);
        }
    }

    protected int findLastSegmentIndex(List<X12Segment> dexSegments) {
        return dexSegments.size() - 1;
    }

}
