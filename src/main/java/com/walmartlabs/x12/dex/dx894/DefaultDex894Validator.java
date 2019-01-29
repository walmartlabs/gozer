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

import com.walmartlabs.x12.X12Validator;
import com.walmartlabs.x12.crc.CyclicRedundancyCheck;
import com.walmartlabs.x12.crc.DefaultCrc16;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultDex894Validator implements X12Validator<Dex894> {

    public static final int DEX_CRC_VALUE_MIN_SIZE = 4;
    public static final int DEX_4010 = 4010;
    public static final int DEX_5010 = 5010;

    public CyclicRedundancyCheck crc16;

    public DefaultDex894Validator() {
        this.crc16 = new DefaultCrc16();
    }

    public DefaultDex894Validator(CyclicRedundancyCheck crc16) {
        this.crc16 = crc16;
    }

    @Override
    public Set<X12ErrorDetail> validate(Dex894 dex, boolean performCrcCheck) {

        Set<X12ErrorDetail> errors = new HashSet<>();
        if (dex != null) {
            // DXE validations
            errors.add(this.compareTransactionCounts(dex));
            errors.add(this.checkForDuplicateInvoiceNumbers(dex));

            List<Dex894TransactionSet> dexTxList = dex.getTransactions();
            for (Dex894TransactionSet dexTx : dexTxList) {
                errors.addAll(this.validateDexTransaction(dex.getVersionNumber(), dexTx, performCrcCheck));
            }
        }
        return this.removeNullValues(errors);
    }

    /**
     * compare the actual number of DEX transactions/invoices w/ the expected count
     */
    protected X12ErrorDetail compareTransactionCounts(Dex894 dex) {
        X12ErrorDetail detail = null;
        int actualTransactionCount = (dex.getTransactions() != null ? dex.getTransactions().size() : 0);
        if (dex.getNumberOfTransactions() != actualTransactionCount) {
            StringBuilder sb = new StringBuilder();
            sb.append("Expected ").append(dex.getNumberOfTransactions()).append(" transactions");
            sb.append(" but got ").append(actualTransactionCount).append(" transactions");
            detail = new X12ErrorDetail(DefaultDex894Parser.APPLICATION_TRAILER_ID, "", sb.toString());
        }
        return detail;
    }

    /**
     * insure the G8202 supplier number is not duplicated
     * within the DEX transmission
     */
    protected X12ErrorDetail checkForDuplicateInvoiceNumbers(Dex894 dex) {
        X12ErrorDetail detail = null;
        List<Dex894TransactionSet> dexTxList = dex.getTransactions();
        if (dexTxList != null) {
            long duplicatedInvoiceCount = dexTxList.stream()
                .map(dexTx -> dexTx.getSupplierNumber())
                .filter(suppNum -> suppNum != null)
                .collect(Collectors.groupingBy(suppNum -> suppNum, Collectors.counting()))
                .values().stream()
                .filter(count -> count > 1)
                .count();

            if (duplicatedInvoiceCount > 0) {
                detail = new X12ErrorDetail(DefaultDex894Parser.G82_ID, "G8202", "Duplicate invoice numbers on DEX");
            }
        }
        return detail;
    }

    /**
     * validate each Transaction in the DEX transmission
     * @param dexVersion
     * @param dexTx
     * @param performCrcCheck
     * @return
     */
    protected Set<X12ErrorDetail> validateDexTransaction(Integer dexVersion, Dex894TransactionSet dexTx, boolean performCrcCheck) {
        Set<X12ErrorDetail> errors = new HashSet<>();

        // integrity check
        if (performCrcCheck) {
            errors.add(this.checkTransactionIntegrity(dexVersion, dexTx));
        }

        // SE validations
        errors.add(this.checkSupplierNumber(dexVersion, dexTx));
        errors.add(this.checkSupplierDate(dexVersion, dexTx));
        errors.add(this.compareTransactionSegmentCounts(dexVersion, dexTx));
        errors.add(this.compareTransactionControlNumbers(dexVersion, dexTx));
        errors.addAll(this.validateItems(dexVersion, dexTx));

        return this.removeNullValues(errors);
    }

    /**
     * validate each Item in the DEX Transaction
     * @param dexVersion
     * @param dexTx
     * @return
     */
    protected Set<X12ErrorDetail> validateItems(Integer dexVersion, Dex894TransactionSet dexTx) {
        Set<X12ErrorDetail> errors = new HashSet<>();

        if (dexTx != null) {
            List<Dex894Item> dexItems = dexTx.getItems();
            if (dexItems != null) {
                for (Dex894Item dexItem : dexItems) {
                    errors.add(this.checkQuantity(dexVersion, dexItem));
                    errors.add(this.checkUnitMeasure(dexVersion, dexItem));
                    errors.add(this.checkItemIdentifier(dexVersion, dexItem));
                    errors.add(this.checkCaseUpc(dexVersion, dexItem));
                    errors.add(this.checkCaseCount(dexVersion, dexItem));
                    errors.addAll(this.validateAllowance(dexVersion, dexItem.getAllowance()));
                }
            }
        }

        return this.removeNullValues(errors);
    }

    /**
     * validate allowance/charge for the DEX Item
     * @param dexVersion
     * @param dexAllowance
     * @return
     */
    protected Set<X12ErrorDetail> validateAllowance(Integer dexVersion, Dex894Allowance dexAllowance) {
        Set<X12ErrorDetail> errors = new HashSet<>();

        if (dexAllowance != null) {
            errors.add(this.checkAllowanceCode(dexVersion, dexAllowance));
            errors.add(this.checkMethodHandlingCode(dexVersion, dexAllowance));
            errors.add(this.checkAllowanceAmount(dexVersion, dexAllowance));
        }

        return this.removeNullValues(errors);
    }

    /**
     * mandatory attribute that identifies the type of allowance or charge that is to apply.
     * @param dexVersion
     * @param dexAllowance
     * @return
     */
    protected X12ErrorDetail checkAllowanceCode(Integer dexVersion, Dex894Allowance dexAllowance) {
        X12ErrorDetail detail = null;

        if (StringUtils.isEmpty(dexAllowance.getAllowanceCode())) {
            detail = new X12ErrorDetail(DefaultDex894Parser.G72_ID, "G7201", "Missing allowance code");
        }

        return detail;
    }

    /**
     * mandatory attribute that indicates the method of handling for the allowance or charge.
     * @param dexVersion
     * @param dexAllowance
     * @return
     */
    protected X12ErrorDetail checkMethodHandlingCode(Integer dexVersion, Dex894Allowance dexAllowance) {
        X12ErrorDetail detail = null;

        if (StringUtils.isEmpty(dexAllowance.getMethodOfHandlingCode())) {
            detail = new X12ErrorDetail(DefaultDex894Parser.G72_ID, "G7202", "Missing method of handling code");
        }

        return detail;
    }

    /**
     * Allowances or charges can be sent as a rate, amount, or percent, and
     * are specified using data elements G7205, G7208 or G7209,
     * respectively. Only use one of these elements in each occurrence of a
     * G72 data segment. The choice of which data element to use depends
     * on how to express the allowance or charge.
     *
     * @param dexVersion
     * @param dexAllowance
     * @return
     */
    protected X12ErrorDetail checkAllowanceAmount(Integer dexVersion, Dex894Allowance dexAllowance) {
        X12ErrorDetail detail = null;

        if (dexAllowance.getAllowanceAmount() == null &&
                dexAllowance.getAllowancePercent() == null &&
                dexAllowance.getAllowanceRate() == null) {

            detail = new X12ErrorDetail(DefaultDex894Parser.G72_ID, "G7205", "Must have allowance rate, percent, or amount");

        }
        return detail;
    }

    /**
     * make sure the transaction (ST - G86) matches integrity check value on G85
     */
    protected X12ErrorDetail checkTransactionIntegrity(Integer dexVersion, Dex894TransactionSet dexTx) {
        X12ErrorDetail detail = null;

        if (dexTx != null && crc16 != null) {
            if (! crc16.verifyBlockOfText(dexTx.getIntegrityCheckValue(), dexTx.getTransactionData(), DEX_CRC_VALUE_MIN_SIZE)) {
                detail = new X12ErrorDetail(DefaultDex894Parser.G85_ID, "G8501", "CRC Integrity Check does not match");
            }
        }

        return detail;
    }

    /**
     * make sure G8202 has value
     */
    protected X12ErrorDetail checkSupplierNumber(Integer dexVersion, Dex894TransactionSet dexTx) {
        X12ErrorDetail detail = null;

        if (dexTx != null) {
            if (StringUtils.isEmpty(dexTx.getSupplierNumber())) {
                detail = new X12ErrorDetail(DefaultDex894Parser.G82_ID, "G8202", "Missing supplier number");
            }
        }

        return detail;
    }

    /**
     * make sure G8207 has value
     */
    protected X12ErrorDetail checkSupplierDate(Integer dexVersion, Dex894TransactionSet dexTx) {
        X12ErrorDetail detail = null;

        if (dexTx != null) {
            if (StringUtils.isEmpty(dexTx.getTransactionDate())) {
                detail = new X12ErrorDetail(DefaultDex894Parser.G82_ID, "G8207", "Missing supplier date");
            } else if (dexTx.getTransactionDate().length() != 8) {
                detail = new X12ErrorDetail(DefaultDex894Parser.G82_ID, "G8207", "Date must be in YYYYMMDD format");
            }
        }

        return detail;
    }

    /**
     * compare the actual number of DEX transactions segments w/ the expected count
     */
    protected X12ErrorDetail compareTransactionSegmentCounts(Integer dexVersion, Dex894TransactionSet dexTx) {
        X12ErrorDetail detail = null;

        if (dexTx.getExpectedNumberOfSegments() != null && dexTx.getActualNumberOfSegments() != null
                && !dexTx.getExpectedNumberOfSegments().equals(dexTx.getActualNumberOfSegments())) {
            StringBuilder sb = new StringBuilder();
            sb.append("Expected ").append(dexTx.getExpectedNumberOfSegments()).append(" segments in a transaction");
            sb.append(" but got ").append(dexTx.getActualNumberOfSegments()).append(" segments in a transaction");
            detail = new X12ErrorDetail(DefaultDex894Parser.TRANSACTION_SET_TRAILER_ID, "", sb.toString());
        }

        return detail;
    }

    /**
     * compare the DEX transaction control numbers on ST and SE segments
     */
    protected X12ErrorDetail compareTransactionControlNumbers(Integer dexVersion, Dex894TransactionSet dexTx) {
        X12ErrorDetail detail = null;

        if (dexTx.getHeaderControlNumber() != null && dexTx.getTrailerControlNumber() != null
                && !dexTx.getHeaderControlNumber().equals(dexTx.getTrailerControlNumber())) {
            StringBuilder sb = new StringBuilder();
            sb.append("Mismatched transaction control numbers: header(").append(dexTx.getHeaderControlNumber());
            sb.append(") and trailer(").append(dexTx.getTrailerControlNumber()).append(")");
            detail = new X12ErrorDetail(DefaultDex894Parser.TRANSACTION_SET_TRAILER_ID, "", sb.toString());
        }

        return detail;
    }


    /**
     * make sure G8302 is a valid value
     *
     */
    protected X12ErrorDetail checkQuantity(Integer dexVersion, Dex894Item dexItem) {
        X12ErrorDetail detail = null;

        if (dexItem != null) {
            if (dexItem.getQuantity() == null) {
                detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8302", "Missing quantity");
            } else if (dexItem.getQuantity().signum() < 0) {
                detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8302", "Quantity must be positive");
            }
        }

        return detail;
    }

    /**
     * make sure G8303 is a valid value
     *
     */
    protected X12ErrorDetail checkUnitMeasure(Integer dexVersion, Dex894Item dexItem) {
        X12ErrorDetail detail = null;

        if (dexItem != null) {
            if (dexItem.getUom() == null || UnitMeasure.UNKNOWN.equals(dexItem.getUom())) {
                detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8303", "Missing/unknown unit of measure");
            }
        }

        return detail;
    }

    /**
     * if G8303 is not CA then make sure we have:
     * version 4010 = G8304
     * version 5010 = G8305 and G8306
     */
    protected X12ErrorDetail checkItemIdentifier(Integer dexVersion, Dex894Item dexItem) {
        X12ErrorDetail detail = null;
        if (dexItem != null) {
            if (!UnitMeasure.CA.equals(dexItem.getUom())) {
                if (dexVersion <= DEX_4010) {
                    if (StringUtils.isEmpty(dexItem.getUpc())) {
                        detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8304", "Missing consumer UPC");
                    }
                } else {
                    if (dexItem.getConsumerProductQualifier() == null) {
                        detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8305", "Missing consumer qualifier");
                    } else if (StringUtils.isEmpty(dexItem.getConsumerProductId())) {
                        detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8306", "Missing consumer UPC");
                    }
                }
            }
        }
        return detail;
    }

    /**
     * if G8303 is CA then make sure we have:
     * version 4010 = G8307
     * version 5010 = G8311 & G8312
     */
    protected X12ErrorDetail checkCaseUpc(Integer dexVersion, Dex894Item dexItem) {
        X12ErrorDetail detail = null;
        if (dexItem != null) {
            if (UnitMeasure.CA.equals(dexItem.getUom())) {
                if (dexVersion <= DEX_4010) {
                    if (StringUtils.isEmpty(dexItem.getCaseUpc())) {
                        detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8307", "Missing case UPC");
                    }
                } else {
                    if (dexItem.getCaseProductQualifier() == null) {
                        detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8311", "Missing case qualifier");
                    } else if (StringUtils.isEmpty(dexItem.getCaseProductId())) {
                        detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8312", "Missing case UPC");
                    }
                }
            }
        }
        return detail;
    }

    /**
     * if G8303 is CA then make sure we have G8309
     */
    protected X12ErrorDetail checkCaseCount(Integer dexVersion, Dex894Item dexItem) {
        X12ErrorDetail detail = null;
        if (dexItem != null) {
            if (UnitMeasure.CA.equals(dexItem.getUom())) {
                if (dexItem.getPackCount() == null) {
                    detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8309", "Missing case count");
                }
            }
        }
        return detail;
    }

    protected Set<X12ErrorDetail> removeNullValues(Set<X12ErrorDetail> errors) {
        return errors.stream()
                .filter(detail -> detail != null)
                .collect(Collectors.toSet());
    }

}
