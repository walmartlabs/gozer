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
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DefaultDex894ValidatorTest {

    DefaultDex894Validator dexValidator;

    @Before
    public void init() {
        dexValidator = new DefaultDex894Validator();
    }

    @Test
    public void test_validate() {
        Dex894 dex = new Dex894();
        dex.setNumberOfTransactions(5);
        dex.setTransactions(this.generateTransactions(5));

        Set<X12ErrorDetail> errorSet = dexValidator.validate(dex);
        assertNotNull(errorSet);
        assertEquals(0, errorSet.size());
    }

    @Test
    public void test_validate_transactionCountWrong() {
        Dex894 dex = new Dex894();
        dex.setNumberOfTransactions(2);
        dex.setTransactions(this.generateTransactions(5));

        Set<X12ErrorDetail> errorSet = dexValidator.validate(dex);
        assertNotNull(errorSet);
        assertEquals(1, errorSet.size());
        assertEquals("DXE", errorSet.stream().findFirst().get().getSegmentId());
    }

    @Test
    public void test_validate_segmentCountWrong() {
        Dex894 dex = new Dex894();
        dex.setNumberOfTransactions(5);
        dex.setTransactions(this.generateTransactions(5));
        dex.getTransactions().get(2).setActualNumberOfSegments(2);
        dex.getTransactions().get(2).setExpectedNumberOfSegments(8);

        Set<X12ErrorDetail> errorSet = dexValidator.validate(dex);
        assertNotNull(errorSet);
        assertEquals(1, errorSet.size());
        assertEquals("SE", errorSet.stream().findFirst().get().getSegmentId());
    }

    @Test
    public void test_compareTransactionSegmentCounts() {
        Dex894TransactionSet dexTx = this.generateTransactions(1).get(0);

        X12ErrorDetail ed = dexValidator.compareTransactionSegmentCounts(dexTx);
        assertNull(ed);
    }

    @Test
    public void test_compareTransactionSegmentCounts_expected_lessThan_Actual() {
        Dex894TransactionSet dexTx = this.generateTransactions(1).get(0);
        dexTx.setExpectedNumberOfSegments(5);
        dexTx.setActualNumberOfSegments(10);
        X12ErrorDetail ed = dexValidator.compareTransactionSegmentCounts(dexTx);

        assertNotNull(ed);
        assertEquals("SE", ed.getSegmentId());
    }

    @Test
    public void test_compareTransactionSegmentCounts_expected_moreThan_Actual() {
        Dex894TransactionSet dexTx = this.generateTransactions(1).get(0);
        dexTx.setExpectedNumberOfSegments(10);
        dexTx.setActualNumberOfSegments(5);
        X12ErrorDetail ed = dexValidator.compareTransactionSegmentCounts(dexTx);

        assertNotNull(ed);
        assertEquals("SE", ed.getSegmentId());
    }

    @Test
    public void test_compareTransactionSegmentCounts_zero() {
        Dex894TransactionSet dexTx = this.generateTransactions(1).get(0);
        dexTx.setExpectedNumberOfSegments(0);
        dexTx.setActualNumberOfSegments(0);
        X12ErrorDetail ed = dexValidator.compareTransactionSegmentCounts(dexTx);

        assertNull(ed);
    }

    @Test
    public void test_compareTransactionSegmentCounts_null() {
        Dex894TransactionSet dexTx = this.generateTransactions(1).get(0);
        dexTx.setExpectedNumberOfSegments(null);
        dexTx.setActualNumberOfSegments(null);
        X12ErrorDetail ed = dexValidator.compareTransactionSegmentCounts(dexTx);

        assertNull(ed);
    }

    @Test
    public void test_compareTransactionCounts() {
        Dex894 dex = new Dex894();
        dex.setNumberOfTransactions(5);
        dex.setTransactions(this.generateTransactions(5));

        X12ErrorDetail ed = dexValidator.compareTransactionCounts(dex);
        assertNull(ed);
    }

    @Test
    public void test_compareTransactionCounts_expected_lessThan_Actual() {
        Dex894 dex = new Dex894();
        dex.setNumberOfTransactions(2);
        dex.setTransactions(this.generateTransactions(5));

        X12ErrorDetail ed = dexValidator.compareTransactionCounts(dex);
        assertNotNull(ed);
        assertEquals("DXE", ed.getSegmentId());
    }

    @Test
    public void test_compareTransactionCounts_expected_moreThan_Actual() {
        Dex894 dex = new Dex894();
        dex.setNumberOfTransactions(12);
        dex.setTransactions(this.generateTransactions(5));

        X12ErrorDetail ed = dexValidator.compareTransactionCounts(dex);
        assertNotNull(ed);
        assertEquals("DXE", ed.getSegmentId());
    }

    @Test
    public void test_compareTransactionCounts_zero() {
        Dex894 dex = new Dex894();
        dex.setNumberOfTransactions(0);
        dex.setTransactions(this.generateTransactions(0));

        X12ErrorDetail ed = dexValidator.compareTransactionCounts(dex);
        assertNull(ed);
    }

    @Test
    public void test_compareTransactionCounts_noList() {
        Dex894 dex = new Dex894();
        dex.setNumberOfTransactions(0);
        dex.setTransactions(null);

        X12ErrorDetail ed = dexValidator.compareTransactionCounts(dex);
        assertNull(ed);
    }

    @Test
    public void test_checkForDuplicateInvoiceNumbers() {
        Dex894 dex = new Dex894();
        dex.setNumberOfTransactions(3);
        dex.setTransactions(this.generateTransactions(3));

        X12ErrorDetail ed = dexValidator.checkForDuplicateInvoiceNumbers(dex);
        assertNull(ed);
    }

    @Test
    public void test_checkForDuplicateInvoiceNumbersWithDuplicate() {
        Dex894 dex = new Dex894();
        dex.setNumberOfTransactions(5);
        dex.setTransactions(this.generateTransactions(5));

        // create dupe scenario
        String supplierNumber = dex.getTransactions().get(4).getSupplierNumber();
        dex.getTransactions().get(2).setSupplierNumber(supplierNumber);

        X12ErrorDetail ed = dexValidator.checkForDuplicateInvoiceNumbers(dex);
        assertNotNull(ed);
        assertEquals("G82", ed.getSegmentId());
        assertEquals("duplicate invoice numbers on DEX", ed.getMessage());
    }

    @Test
    public void test_checkForDuplicateInvoiceNumbers_noList() {
        Dex894 dex = new Dex894();
        dex.setNumberOfTransactions(0);
        dex.setTransactions(null);

        X12ErrorDetail ed = dexValidator.checkForDuplicateInvoiceNumbers(dex);
        assertNull(ed);
    }

    protected List<Dex894TransactionSet> generateTransactions(int numTx) {
        List<Dex894TransactionSet> dexTxList = new ArrayList<>();

        for (int i = 0; i < numTx; i++) {
            Dex894TransactionSet dexTx = new Dex894TransactionSet();
            dexTx.setSupplierNumber("invoice-" + i);
            dexTx.setExpectedNumberOfSegments(10);
            dexTx.setActualNumberOfSegments(10);
            dexTxList.add(dexTx);
        }

        return dexTxList;
    }
}
