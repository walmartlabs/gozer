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

import com.walmartlabs.x12.exceptions.X12ParserException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DefaultDex894ParserTest {

    DefaultDex894Parser dexParser;

    @Before
    public void init() {
        dexParser = new DefaultDex894Parser();
    }

    @Test
    public void test_token_parser_ThreeCharacter() {
        assertEquals("DXS", dexParser.segmentIdentifier("DXS*9251230013*DX*004010UCS*1*9254850000"));
    }

    @Test
    public void test_token_parser_TwoCharacter() {
        assertEquals("ST", dexParser.segmentIdentifier("ST*9251230013*DX*004010UCS*1*9254850000"));
    }

    @Test
    public void test_token_parser_None() {
        assertEquals("", dexParser.segmentIdentifier("*ST*9251230013*DX*004010UCS*1*9254850000"));
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

    @Test
    public void testParsingValidShipmentMultipleItems() throws IOException {
        byte[] dexBytes = Files.readAllBytes(Paths.get("src/test/resources/dex/894/dex.sample.2.txt"));

        List<String> dexLines = dexParser.splitDexIntoSegments(new String(dexBytes));
        System.out.println(dexLines);

        Dex894 dex = dexParser.parse(new String(dexBytes));
        assertNotNull(dex);

        // DXS segment
        assertEquals("9251230013", dex.getSenderCommId());
        assertEquals("DX", dex.getFunctionalId());
        assertEquals("004010UCS", dex.getVersion());
        assertEquals("1", dex.getHeaderTransmissionControlNumber());
        assertEquals("9254850000", dex.getReceiverCommId());
        assertEquals(null, dex.getTestIndicator());

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
        assertEquals("148303793", dexTx.getHeaderControlNumber());
        // SE segment
        assertEquals("148303793", dexTx.getTrailerControlNumber());
        assertEquals(new Integer(42), dexTx.getExpectedNumberOfSegments());
        assertEquals(new Integer(42), dexTx.getActualNumberOfSegments());
        // G82 segment
        assertEquals(InvoiceType.D, dexTx.getDebitCreditFlag());
        assertEquals("148303793", dexTx.getSupplierNumber());
        assertEquals("051957769", dexTx.getReceiverDuns());
        assertEquals("6966", dexTx.getReceiverLocation());
        assertEquals("001184472", dexTx.getSupplierDuns());
        assertEquals("0013", dexTx.getSupplierLocation());
        assertEquals("20171109", dexTx.getTransactionDate());
        // G84 segment
        assertEquals("143.000", dexTx.getTransactionTotalQuantity().toString());
        assertEquals("41040.00", dexTx.getTransactionTotalAmount().toString());
        assertEquals(null, dexTx.getTransactionTotalDepositAmount());
        // G85 segment
        assertEquals("A238", dexTx.getIntegrityCheckValue());
        // G86 segment
        assertEquals("D83037938029", dexTx.getElectronicSignature());
        assertEquals(null, dexTx.getSignatureName());

        // items
        List<Dex894Item> dexItemList = dexTx.getItems();
        assertNotNull(dexItemList);
        assertEquals(19, dexItemList.size());

        // DEX transaction (1) item (1)
        Dex894Item dexItem = dexItemList.get(0);
        assertNotNull(dexItem);
        // G83 segment
        assertEquals("1", dexItem.getItemSequenceNumber());
        assertEquals("4.000", dexItem.getQuantity().toString());
        assertEquals(UnitMeasure.EA, dexItem.getUom());
        assertEquals("001410008782", dexItem.getUpc());
        assertEquals(null, dexItem.getConsumerProductQualifier());
        assertEquals(null, dexItem.getConsumerProductId());
        assertEquals(null, dexItem.getCaseUpc());
        assertEquals("4.80", dexItem.getItemListCost().toString());
        assertEquals(null, dexItem.getPackCount());
        assertEquals(null, dexItem.getItemDescription());
        assertEquals(null, dexItem.getCaseProductQualifier());
        assertEquals(null, dexItem.getCaseProductId());
        assertEquals(null, dexItem.getInnerPackCount());
        // G72 segment
        Dex894Allowance dexAllowance = dexItem.getAllowance();
        assertNotNull(dexAllowance);
        assertEquals("090", dexAllowance.getAllowanceCode());
        assertEquals("02", dexAllowance.getMethodOfHandlingCode());
        assertEquals(null, dexAllowance.getAllowanceNumber());
        assertEquals(null, dexAllowance.getExceptionNumber());
        assertEquals("-1.0300", dexAllowance.getAllowanceRate().toString());
        assertEquals("4.000", dexAllowance.getAllowanceQuantity().toString());
        assertEquals(UnitMeasure.EA, dexAllowance.getAllowanceUom());
        assertEquals(null, dexAllowance.getAllowanceAmount());
        assertEquals(null, dexAllowance.getAllowancePercent());
        assertEquals(null, dexAllowance.getDollarBasis());
        assertEquals(null, dexAllowance.getOptionNumber());

        // DEX transaction (1) item (2)
        dexItem = dexItemList.get(1);
        assertNotNull(dexItem);
        // G83 segment
        assertEquals("2", dexItem.getItemSequenceNumber());
        assertEquals("4.000", dexItem.getQuantity().toString());
        assertEquals(UnitMeasure.EA, dexItem.getUom());
        assertEquals("001410008783", dexItem.getUpc());
        assertEquals(null, dexItem.getConsumerProductQualifier());
        assertEquals(null, dexItem.getConsumerProductId());
        assertEquals(null, dexItem.getCaseUpc());
        assertEquals("4.80", dexItem.getItemListCost().toString());
        assertEquals(null, dexItem.getPackCount());
        assertEquals(null, dexItem.getItemDescription());
        assertEquals(null, dexItem.getCaseProductQualifier());
        assertEquals(null, dexItem.getCaseProductId());
        assertEquals(null, dexItem.getInnerPackCount());
        // G72 segment
        dexAllowance = dexItem.getAllowance();
        assertNotNull(dexAllowance);
        assertEquals("090", dexAllowance.getAllowanceCode());
        assertEquals("02", dexAllowance.getMethodOfHandlingCode());
        assertEquals(null, dexAllowance.getAllowanceNumber());
        assertEquals(null, dexAllowance.getExceptionNumber());
        assertEquals("-1.0300", dexAllowance.getAllowanceRate().toString());
        assertEquals("4.000", dexAllowance.getAllowanceQuantity().toString());
        assertEquals(UnitMeasure.EA, dexAllowance.getAllowanceUom());
        assertEquals(null, dexAllowance.getAllowanceAmount());
        assertEquals(null, dexAllowance.getAllowancePercent());
        assertEquals(null, dexAllowance.getDollarBasis());
        assertEquals(null, dexAllowance.getOptionNumber());

        // DEX transaction (1) item (9) -- sampling
        dexItem = dexItemList.get(8);
        assertNotNull(dexItem);
        // G83 segment
        assertEquals("9", dexItem.getItemSequenceNumber());
        assertEquals("6.000", dexItem.getQuantity().toString());
        assertEquals(UnitMeasure.EA, dexItem.getUom());
        assertEquals("001410008609", dexItem.getUpc());
        assertEquals(null, dexItem.getConsumerProductQualifier());
        assertEquals(null, dexItem.getConsumerProductId());
        assertEquals(null, dexItem.getCaseUpc());
        assertEquals("3.83", dexItem.getItemListCost().toString());
        assertEquals(null, dexItem.getPackCount());
        assertEquals(null, dexItem.getItemDescription());
        assertEquals(null, dexItem.getCaseProductQualifier());
        assertEquals(null, dexItem.getCaseProductId());
        assertEquals(null, dexItem.getInnerPackCount());
        // G72 segment
        dexAllowance = dexItem.getAllowance();
        assertNull(dexAllowance);

        // DEX transaction (1) item (12) -- sampling
        dexItem = dexItemList.get(11);
        assertNotNull(dexItem);
        // G83 segment
        assertEquals("12", dexItem.getItemSequenceNumber());
        assertEquals("8.000", dexItem.getQuantity().toString());
        assertEquals(UnitMeasure.EA, dexItem.getUom());
        assertEquals("001410008548", dexItem.getUpc());
        assertEquals(null, dexItem.getConsumerProductQualifier());
        assertEquals(null, dexItem.getConsumerProductId());
        assertEquals(null, dexItem.getCaseUpc());
        assertEquals("1.83", dexItem.getItemListCost().toString());
        assertEquals(null, dexItem.getPackCount());
        assertEquals(null, dexItem.getItemDescription());
        assertEquals(null, dexItem.getCaseProductQualifier());
        assertEquals(null, dexItem.getCaseProductId());
        assertEquals(null, dexItem.getInnerPackCount());
        // G72 segment
        dexAllowance = dexItem.getAllowance();
        assertNotNull(dexAllowance);
        assertEquals("090", dexAllowance.getAllowanceCode());
        assertEquals("02", dexAllowance.getMethodOfHandlingCode());
        assertEquals(null, dexAllowance.getAllowanceNumber());
        assertEquals(null, dexAllowance.getExceptionNumber());
        assertEquals("-0.2700", dexAllowance.getAllowanceRate().toString());
        assertEquals("8.000", dexAllowance.getAllowanceQuantity().toString());
        assertEquals(UnitMeasure.EA, dexAllowance.getAllowanceUom());
        assertEquals(null, dexAllowance.getAllowanceAmount());
        assertEquals(null, dexAllowance.getAllowancePercent());
        assertEquals(null, dexAllowance.getDollarBasis());
        assertEquals(null, dexAllowance.getOptionNumber());

        // DXE segment
        assertEquals("1", dex.getTrailerTransmissionControlNumber());
        assertEquals(new Integer(1), dex.getNumberOfTransactions());
    }


    @Test
    public void testParsingValidShipment() throws IOException {
        byte[] dexBytes = Files.readAllBytes(Paths.get("src/test/resources/dex/894/dex.sample.1.txt"));

        List<String> dexLines = dexParser.splitDexIntoSegments(new String(dexBytes));
        System.out.println(dexLines);

        Dex894 dex = dexParser.parse(new String(dexBytes));
        assertNotNull(dex);

        // DXS segment
        assertEquals("9251230013", dex.getSenderCommId());
        assertEquals("DX", dex.getFunctionalId());
        assertEquals("004010UCS", dex.getVersion());
        assertEquals("1", dex.getHeaderTransmissionControlNumber());
        assertEquals("9254850000", dex.getReceiverCommId());
        assertEquals(null, dex.getTestIndicator());

        // DEX transactions
        List<Dex894TransactionSet> dexTxList = dex.getTransactions();
        assertNotNull(dexTxList);
        assertEquals(2, dexTxList.size());

        //
        // DEX transaction (1)
        //
        Dex894TransactionSet dexTx = dexTxList.get(0);
        assertNotNull(dexTx);
        // ST segment
        assertEquals("894", dexTx.getTransactionSetIdentifierCode());
        assertEquals("569145629", dexTx.getHeaderControlNumber());
        // SE segment
        assertEquals("569145629", dexTx.getTrailerControlNumber());
        assertEquals(new Integer(10), dexTx.getExpectedNumberOfSegments());
        assertEquals(new Integer(10), dexTx.getActualNumberOfSegments());
        // G82 segment
        assertEquals(InvoiceType.D, dexTx.getDebitCreditFlag());
        assertEquals("569145629", dexTx.getSupplierNumber());
        assertEquals("051957769", dexTx.getReceiverDuns());
        assertEquals("002703", dexTx.getReceiverLocation());
        assertEquals("001184472", dexTx.getSupplierDuns());
        assertEquals("0000", dexTx.getSupplierLocation());
        assertEquals("20170822", dexTx.getTransactionDate());
        // G84 segment
        assertEquals("48.000", dexTx.getTransactionTotalQuantity().toString());
        assertEquals("7488.00", dexTx.getTransactionTotalAmount().toString());
        assertEquals(null, dexTx.getTransactionTotalDepositAmount());
        // G85 segment
        assertEquals("8EC2", dexTx.getIntegrityCheckValue());
        // G86 segment
        assertEquals("D91456290976", dexTx.getElectronicSignature());
        assertEquals(null, dexTx.getSignatureName());

        // items
        List<Dex894Item> dexItemList = dexTx.getItems();
        assertNotNull(dexItemList);
        assertEquals(1, dexItemList.size());

        // DEX transaction (1) item (1)
        Dex894Item dexItem = dexItemList.get(0);
        assertNotNull(dexItem);
        // G83 segment
        assertEquals("1", dexItem.getItemSequenceNumber());
        assertEquals("48.000", dexItem.getQuantity().toString());
        assertEquals(UnitMeasure.EA, dexItem.getUom());
        assertEquals("001410008547", dexItem.getUpc());
        assertEquals(null, dexItem.getConsumerProductQualifier());
        assertEquals(null, dexItem.getConsumerProductId());
        assertEquals(null, dexItem.getCaseUpc());
        assertEquals("1.83", dexItem.getItemListCost().toString());
        assertEquals(null, dexItem.getPackCount());
        assertEquals(null, dexItem.getItemDescription());
        assertEquals(null, dexItem.getCaseProductQualifier());
        assertEquals(null, dexItem.getCaseProductId());
        assertEquals(null, dexItem.getInnerPackCount());
        // G72 segment
        Dex894Allowance dexAllowance = dexItem.getAllowance();
        assertNotNull(dexAllowance);
        assertEquals("090", dexAllowance.getAllowanceCode());
        assertEquals("02", dexAllowance.getMethodOfHandlingCode());
        assertEquals(null, dexAllowance.getAllowanceNumber());
        assertEquals(null, dexAllowance.getExceptionNumber());
        assertEquals("-0.2700", dexAllowance.getAllowanceRate().toString());
        assertEquals("48.000", dexAllowance.getAllowanceQuantity().toString());
        assertEquals(UnitMeasure.EA, dexAllowance.getAllowanceUom());
        assertEquals(null, dexAllowance.getAllowanceAmount());
        assertEquals(null, dexAllowance.getAllowancePercent());
        assertEquals(null, dexAllowance.getDollarBasis());
        assertEquals(null, dexAllowance.getOptionNumber());

        //
        // DEX transaction (2)
        //
        dexTx = dexTxList.get(1);
        assertNotNull(dexTx);
        // ST segment
        assertEquals("894", dexTx.getTransactionSetIdentifierCode());
        assertEquals("569145630", dexTx.getHeaderControlNumber());
        // SE segment
        assertEquals("569145630", dexTx.getTrailerControlNumber());
        assertEquals(new Integer(10), dexTx.getExpectedNumberOfSegments());
        assertEquals(new Integer(10), dexTx.getActualNumberOfSegments());
        // G82 segment
        assertEquals(InvoiceType.D, dexTx.getDebitCreditFlag());
        assertEquals("569145630", dexTx.getSupplierNumber());
        assertEquals("051957769", dexTx.getReceiverDuns());
        assertEquals("002703", dexTx.getReceiverLocation());
        assertEquals("001184472", dexTx.getSupplierDuns());
        assertEquals("0000", dexTx.getSupplierLocation());
        assertEquals("20170822", dexTx.getTransactionDate());
        // G84 segment
        assertEquals("2.000", dexTx.getTransactionTotalQuantity().toString());
        assertEquals("856.00", dexTx.getTransactionTotalAmount().toString());
        assertEquals(null, dexTx.getTransactionTotalDepositAmount());
        // G85 segment
        assertEquals("284B", dexTx.getIntegrityCheckValue());
        // G86 segment
        assertEquals("C91456300976", dexTx.getElectronicSignature());
        assertEquals(null, dexTx.getSignatureName());

        // items
        dexItemList = dexTx.getItems();
        assertNotNull(dexItemList);
        assertEquals(1, dexItemList.size());

        // DEX transaction (2) item (1)
        dexItem = dexItemList.get(0);
        assertNotNull(dexItem);
        // G83 segment
        assertEquals("1", dexItem.getItemSequenceNumber());
        assertEquals("2.000", dexItem.getQuantity().toString());
        assertEquals(UnitMeasure.EA, dexItem.getUom());
        assertEquals("001410004616", dexItem.getUpc());
        assertEquals(null, dexItem.getConsumerProductQualifier());
        assertEquals(null, dexItem.getConsumerProductId());
        assertEquals(null, dexItem.getCaseUpc());
        assertEquals("5.17", dexItem.getItemListCost().toString());
        assertEquals(null, dexItem.getPackCount());
        assertEquals(null, dexItem.getItemDescription());
        assertEquals(null, dexItem.getCaseProductQualifier());
        assertEquals(null, dexItem.getCaseProductId());
        assertEquals(null, dexItem.getInnerPackCount());
        // G72 segment
        dexAllowance = dexItem.getAllowance();
        assertNotNull(dexAllowance);
        assertEquals("090", dexAllowance.getAllowanceCode());
        assertEquals("02", dexAllowance.getMethodOfHandlingCode());
        assertEquals(null, dexAllowance.getAllowanceNumber());
        assertEquals(null, dexAllowance.getExceptionNumber());
        assertEquals("-0.8900", dexAllowance.getAllowanceRate().toString());
        assertEquals("2.000", dexAllowance.getAllowanceQuantity().toString());
        assertEquals(UnitMeasure.EA, dexAllowance.getAllowanceUom());
        assertEquals(null, dexAllowance.getAllowanceAmount());
        assertEquals(null, dexAllowance.getAllowancePercent());
        assertEquals(null, dexAllowance.getDollarBasis());
        assertEquals(null, dexAllowance.getOptionNumber());

        // DXE segment
        assertEquals("1", dex.getTrailerTransmissionControlNumber());
        assertEquals(new Integer(2), dex.getNumberOfTransactions());
    }

}