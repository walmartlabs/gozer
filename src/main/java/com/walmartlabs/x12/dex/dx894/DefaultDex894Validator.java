package com.walmartlabs.x12.dex.dx894;

import com.walmartlabs.x12.exceptions.X12ErrorDetail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultDex894Validator implements Dex894Validator {

    @Override
    public Set<X12ErrorDetail> validate(Dex894 dex) {
        Set<X12ErrorDetail> errors = new HashSet<>();
        if (dex != null) {
            // DXE validations
            errors.add(this.compareTransactionCounts(dex));

            List<Dex894TransactionSet> dexTxList = dex.getTransactions();
            for (Dex894TransactionSet dexTx : dexTxList) {
                errors.addAll(this.validateDexTransaction(dexTx));
            }
        }
        // remove all of the null details
        return errors.stream()
            .filter(detail -> detail != null)
            .collect(Collectors.toSet());
    }

    protected Set<X12ErrorDetail> validateDexTransaction(Dex894TransactionSet dexTx) {
        Set<X12ErrorDetail> errors = new HashSet<>();
        // SE validations
        errors.add(this.compareTransactionSegmentCounts(dexTx));
        return errors;
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
     * compare the actual number of DEX transactions segments w/ the expected count
     */
    protected X12ErrorDetail compareTransactionSegmentCounts(Dex894TransactionSet dexTx) {
        X12ErrorDetail detail = null;
        if (dexTx.getExpectedNumberOfSegments() != dexTx.getActualNumberOfSegments()) {
            StringBuilder sb = new StringBuilder();
            sb.append("expected ").append(dexTx.getExpectedNumberOfSegments());
            sb.append(" but got ").append(dexTx.getActualNumberOfSegments());
            detail = new X12ErrorDetail(DefaultDex894Parser.TRANSACTION_SET_TRAILER_ID, "", sb.toString());
        }
        return detail;
    }

}
