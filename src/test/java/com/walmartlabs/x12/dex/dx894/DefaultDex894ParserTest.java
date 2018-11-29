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
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DefaultDex894ParserTest {

    DefaultDex894Parser dexParser;

    @Before
    public void init() {
        dexParser = new DefaultDex894Parser();
    }

    @Test
    public void test_convertStringToInteger_Null() {
        assertEquals(null, dexParser.convertStringToInteger(null));
    }

    @Test
    public void test_convertStringToInteger_None() {
        assertEquals(null, dexParser.convertStringToInteger(""));
    }

    @Test
    public void test_convertStringToInteger_Number() {
        assertEquals(new Integer(1), dexParser.convertStringToInteger("1"));
    }

    @Test(expected = X12ParserException.class)
    public void test_convertStringToInteger_Alpha() {
        dexParser.convertStringToInteger("X");
    }

    @Test
    public void test_convertStringToBigDecimal_Null() {
        assertEquals(null, dexParser.convertStringToBigDecimal(null, 2));
    }

    @Test
    public void test_convertStringToBigDecimal_None() {
        assertEquals(null, dexParser.convertStringToBigDecimal("", 2));
    }

    @Test
    public void test_convertStringToBigDecimal_Number() {
        assertEquals("1.00", dexParser.convertStringToBigDecimal("1", 2).toString());
    }

    @Test
    public void test_convertStringToBigDecimal_Negative_Number() {
        assertEquals("-1.00", dexParser.convertStringToBigDecimal("-1", 2).toString());
    }

    @Test(expected = X12ParserException.class)
    public void test_convertStringToBigDecimal_Alpha() {
        dexParser.convertStringToBigDecimal("X", 2);
    }

    @Test
    public void testParsingShipment_null() throws IOException {
        String dexTransmission = null;
        assertNull(dexParser.parse(dexTransmission));
    }

    @Test
    public void testParsingShipment_empty() throws IOException {
        String dexTransmission = "";
        assertNull(dexParser.parse(dexTransmission));
    }

    @Test(expected = X12ParserException.class)
    public void testParsingShipment_invalid() throws IOException {
        String dexTransmission = "invalid";
        dexParser.parse(dexTransmission);
    }

    @Test(expected = X12ParserException.class)
    public void testParsingShipmentWithMissingDxe() throws IOException {
        byte[] dexBytes = Files.readAllBytes(Paths.get("src/test/resources/dex/894/dex.sample.missing.dxe.txt"));
        dexParser.parse(new String(dexBytes));
    }

    @Test(expected = X12ParserException.class)
    public void testParsingShipmentWithMismatchedTransactions() throws IOException {
        byte[] dexBytes = Files.readAllBytes(Paths.get("src/test/resources/dex/894/dex.sample.mismatched.st.txt"));
        dexParser.parse(new String(dexBytes));
    }

    @Test(expected = X12ParserException.class)
    public void testParsingInvalidSegments() throws IOException {
        byte[] dexBytes = Files.readAllBytes(Paths.get("src/test/resources/dex/894/dex.sample.invalid.segments.txt"));
        dexParser.parse(new String(dexBytes));
    }

    /**
     * TODO: G72 needs to be figured out
     */
    @Test
    public void testParsingMultipleG72() throws IOException {
        byte[] dexBytes = Files.readAllBytes(Paths.get("src/test/resources/dex/894/dex.sample.multiple.g72.txt"));
        Dex894 dex = dexParser.parse(new String(dexBytes));
        assertNotNull(dex);

        DefaultDex894Validator dexValidator = new DefaultDex894Validator();
        Set<X12ErrorDetail> errors = dexValidator.validate(dex, false);
        assertNotNull(errors);
        assertEquals(0, errors.size());

        // DXS segment
        assertEquals("9269850000", dex.getSenderCommId());
        assertEquals("DX", dex.getFunctionalId());
        assertEquals("004010UCS", dex.getVersion());
        assertEquals("3", dex.getHeaderTransmissionControlNumber());
        assertEquals("9254850000", dex.getReceiverCommId());
        assertEquals("P", dex.getTestIndicator());

        // DEX transactions
        List<Dex894TransactionSet> dexTxList = dex.getTransactions();
        assertNotNull(dexTxList);
        assertEquals(1, dexTxList.size());

        //
        // DEX transaction (1)
        //
        Dex894TransactionSet dexTx = dexTxList.get(0);
        assertNotNull(dexTx);
        // ST segment
        assertEquals("894", dexTx.getTransactionSetIdentifierCode());
        assertEquals("0002", dexTx.getHeaderControlNumber());
        // SE segment
        assertEquals("0002", dexTx.getTrailerControlNumber());
        assertEquals(new Integer(13), dexTx.getExpectedNumberOfSegments());
        assertEquals(new Integer(13), dexTx.getActualNumberOfSegments());
        // G82 segment
        assertEquals(InvoiceType.D, dexTx.getDebitCreditFlag());
        assertEquals("109152000116", dexTx.getSupplierNumber());
        assertEquals("051957769", dexTx.getReceiverDuns());
        assertEquals("0001", dexTx.getReceiverLocation());
        assertEquals("066563859", dexTx.getSupplierDuns());
        assertEquals("000000", dexTx.getSupplierLocation());
        assertEquals("20181129", dexTx.getTransactionDate());
        // G84 segment
        assertEquals("340.000", dexTx.getTransactionTotalQuantity().toString());
        assertEquals("120931.00", dexTx.getTransactionTotalAmount().toString());
        assertEquals(null, dexTx.getTransactionTotalDepositAmount());
        // G85 segment
        assertEquals("XXXX", dexTx.getIntegrityCheckValue());
        // G86 segment
        assertEquals("7898", dexTx.getElectronicSignature());
        assertEquals(null, dexTx.getSignatureName());
        // transaction data
        assertNotNull(dexTx.getTransactionData());
        assertTrue(dexTx.getTransactionData().startsWith("ST*894*0002"));
        assertTrue(dexTx.getTransactionData().contains("G83*25*12*EA*007192167217*AC*TMB ORG 12IN 12CT**3.8**OR PEP 12\r\n"));
        assertTrue(dexTx.getTransactionData().endsWith("G86*7898\r\n"));

        // items
        List<Dex894Item> dexItemList = dexTx.getItems();
        assertNotNull(dexItemList);
        assertEquals(2, dexItemList.size());

        // DEX transaction (1) item (1)
        Dex894Item dexItem = dexItemList.get(0);
        assertNotNull(dexItem);
        // G83 segment
        assertEquals("25", dexItem.getItemSequenceNumber());
        assertEquals("12.000", dexItem.getQuantity().toString());
        assertEquals(UnitMeasure.EA, dexItem.getUom());
        assertEquals("007192167217", dexItem.getUpc());
        assertEquals(ProductQualifier.UNKNOWN, dexItem.getConsumerProductQualifier());
        assertEquals("TMB ORG 12IN 12CT", dexItem.getConsumerProductId());
        assertEquals(null, dexItem.getCaseUpc());
        assertEquals("3.80", dexItem.getItemListCost().toString());
        assertEquals(null, dexItem.getPackCount());
        assertEquals("OR PEP 12", dexItem.getItemDescription());
        assertEquals(null, dexItem.getCaseProductQualifier());
        assertEquals(null, dexItem.getCaseProductId());
        assertEquals(null, dexItem.getInnerPackCount());

        // DEX transaction (1) item (2)
        dexItem = dexItemList.get(1);
        assertNotNull(dexItem);
        // G83 segment
        assertEquals("27", dexItem.getItemSequenceNumber());
        assertEquals("24.000", dexItem.getQuantity().toString());
        assertEquals(UnitMeasure.EA, dexItem.getUom());
        assertEquals("007465339393", dexItem.getUpc());
        assertEquals(ProductQualifier.UNKNOWN, dexItem.getConsumerProductQualifier());
        assertEquals("JCK PZA 12IN 12CT", dexItem.getConsumerProductId());
        assertEquals(null, dexItem.getCaseUpc());
        assertEquals("2.80", dexItem.getItemListCost().toString());
        assertEquals(null, dexItem.getPackCount());
        assertEquals("TC PEPSAUS 12", dexItem.getItemDescription());
        assertEquals(null, dexItem.getCaseProductQualifier());
        assertEquals(null, dexItem.getCaseProductId());
        assertEquals(null, dexItem.getInnerPackCount());
    }

}