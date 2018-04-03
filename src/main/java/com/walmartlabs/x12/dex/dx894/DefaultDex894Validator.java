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
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultDex894Validator implements Dex894Validator {

    public static final int DEX_4010 = 4010;
    public static final int DEX_5010 = 5010;

    @Override
    public Set<X12ErrorDetail> validate(Dex894 dex) {
        Set<X12ErrorDetail> errors = new HashSet<>();
        if (dex != null) {
            // DXE validations
            errors.add(this.compareTransactionCounts(dex));
            errors.add(this.checkForDuplicateInvoiceNumbers(dex));

            List<Dex894TransactionSet> dexTxList = dex.getTransactions();
            for (Dex894TransactionSet dexTx : dexTxList) {
                errors.addAll(this.validateDexTransaction(dex.getVersionNumber(), dexTx));
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
            sb.append("expected ").append(dex.getNumberOfTransactions());
            sb.append(" but got ").append(actualTransactionCount);
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
                .collect(Collectors.groupingBy(suppNum -> suppNum, Collectors.counting()))
                .values().stream()
                .filter(count -> count > 1)
                .count();

            if (duplicatedInvoiceCount > 0) {
                detail = new X12ErrorDetail(DefaultDex894Parser.G82_ID, "G8202", "duplicate invoice numbers on DEX");
            }
        }
        return detail;
    }

    /**
     * validate each Transaction in the DEX transmission
     * @param dexVersion
     * @param dexTx
     * @return
     */
    protected Set<X12ErrorDetail> validateDexTransaction(Integer dexVersion, Dex894TransactionSet dexTx) {
        Set<X12ErrorDetail> errors = new HashSet<>();

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
                }
            }
        }

        return this.removeNullValues(errors);
    }


    /**
     * make sure G8202 has value
     */
    protected X12ErrorDetail checkSupplierNumber(Integer dexVersion, Dex894TransactionSet dexTx) {
        X12ErrorDetail detail = null;

        if (dexTx != null) {
            if (StringUtils.isEmpty(dexTx.getSupplierNumber())) {
                detail = new X12ErrorDetail(DefaultDex894Parser.G82_ID, "G8202", "missing supplier number");
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
                detail = new X12ErrorDetail(DefaultDex894Parser.G82_ID, "G8207", "missing supplier date");
            } else if (dexTx.getTransactionDate().length() != 8) {
                detail = new X12ErrorDetail(DefaultDex894Parser.G82_ID, "G8207", "date must YYYYMMDD");
                // TODO: verify it is a valid date
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
            sb.append("expected ").append(dexTx.getExpectedNumberOfSegments());
            sb.append(" but got ").append(dexTx.getActualNumberOfSegments());
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
            sb.append("mismatched transaction control numbers: header(").append(dexTx.getHeaderControlNumber());
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
                detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8302", "missing quantity");
            } else if (dexItem.getQuantity().signum() < 0) {
                detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8302", "quantity must be positive");
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
                detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8303", "missing/unknown unit of measure");
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
                        detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8304", "missing consumer UPC");
                    }
                } else {
                    if (dexItem.getConsumerProductQualifier() == null) {
                        detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8305", "missing consumer qualifier");
                    } else if (StringUtils.isEmpty(dexItem.getConsumerProductId())) {
                        detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8306", "missing consumer UPC");
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
                        detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8307", "missing case UPC");
                    }
                } else {
                    if (dexItem.getCaseProductQualifier() == null) {
                        detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8311", "missing case qualifier");
                    } else if (StringUtils.isEmpty(dexItem.getCaseProductId())) {
                        detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8312", "missing case UPC");
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
                    detail = new X12ErrorDetail(DefaultDex894Parser.G83_ID, "G8309", "missing case count");
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
