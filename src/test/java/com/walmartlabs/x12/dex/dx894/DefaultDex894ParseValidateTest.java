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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DefaultDex894ParseValidateTest {

    private DefaultDex894Parser dexParser;
    private DefaultDex894Validator dexValidator;

    @Before
    public void init() {
        dexParser = new DefaultDex894Parser();
        dexValidator = new DefaultDex894Validator();
    }


    @Test
    public void testParsingValidShipmentMultipleItems() throws IOException {
        byte[] dexBytes = Files.readAllBytes(Paths.get("src/test/resources/dex/894/dex.sample.2.txt"));
        Dex894 dex = dexParser.parse(new String(dexBytes));
        assertNotNull(dex);
        Set<X12ErrorDetail> errors = dexValidator.validate(dex);
        assertNotNull(errors);
        assertEquals(0, errors.size());

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
        // transaction data
        assertNotNull(dexTx.getTransactionData());
        assertTrue(dexTx.getTransactionData().startsWith("ST*894*148303793"));
        assertTrue(dexTx.getTransactionData().contains("G83*15*8*EA*001410008561****1.83\r\n"));
        assertTrue(dexTx.getTransactionData().endsWith("G86*D83037938029\r\n"));

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
        Dex894 dex = dexParser.parse(new String(dexBytes));
        assertNotNull(dex);
        Set<X12ErrorDetail> errors = dexValidator.validate(dex);
        assertNotNull(errors);
        assertEquals(0, errors.size());

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
        // transaction data
        assertNotNull(dexTx.getTransactionData());
        assertTrue(dexTx.getTransactionData().startsWith("ST*894*569145629"));
        assertTrue(dexTx.getTransactionData().endsWith("G86*D91456290976\r\n"));

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
        // transaction data
        assertNotNull(dexTx.getTransactionData());
        assertTrue(dexTx.getTransactionData().startsWith("ST*894*569145630"));
        assertTrue(dexTx.getTransactionData().endsWith("G86*C91456300976\r\n"));

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

    @Test
    public void testParsingShipment_Dex_with_G22() throws IOException {
        byte[] dexBytes = Files.readAllBytes(Paths.get("src/test/resources/dex/894/dex.sample.g22.txt"));
        Dex894 dex = dexParser.parse(new String(dexBytes));
        assertNotNull(dex);
        Set<X12ErrorDetail> errors = dexValidator.validate(dex);
        assertNotNull(errors);
        assertEquals(0, errors.size());

        // DXS segment
        assertEquals("8050880508", dex.getSenderCommId());
        assertEquals("DX", dex.getFunctionalId());
        assertEquals("004010UCS", dex.getVersion());
        assertEquals("113", dex.getHeaderTransmissionControlNumber());
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
        assertEquals("0101", dexTx.getHeaderControlNumber());
        // SE segment
        assertEquals("0101", dexTx.getTrailerControlNumber());
        assertEquals(new Integer(13), dexTx.getExpectedNumberOfSegments());
        assertEquals(new Integer(13), dexTx.getActualNumberOfSegments());
        // G82 segment
        assertEquals(InvoiceType.D, dexTx.getDebitCreditFlag());
        assertEquals("001701001701", dexTx.getSupplierNumber());
        assertEquals("051957769", dexTx.getReceiverDuns());
        assertEquals("5", dexTx.getReceiverLocation());
        assertEquals("123456789", dexTx.getSupplierDuns());
        assertEquals("073002", dexTx.getSupplierLocation());
        assertEquals("20180416", dexTx.getTransactionDate());
        // G85 segment
        assertEquals("8263", dexTx.getIntegrityCheckValue());
        // G86 segment
        assertEquals("0840", dexTx.getElectronicSignature());
        assertEquals(null, dexTx.getSignatureName());
        // transaction data
        assertNotNull(dexTx.getTransactionData());
        assertTrue(dexTx.getTransactionData().startsWith("ST*894*0101"));
        assertTrue(dexTx.getTransactionData().contains("G83*1*4*EA*007189983548****2.13*1*1/2 GAL TEST 1\r\n"));
        assertTrue(dexTx.getTransactionData().endsWith("G86*0840\r\n"));

        // items
        List<Dex894Item> dexItemList = dexTx.getItems();
        assertNotNull(dexItemList);
        assertEquals(2, dexItemList.size());


        // DEX transaction (1) item (1)
        Dex894Item dexItem = dexItemList.get(0);
        assertNotNull(dexItem);
        // G83 segment
        assertEquals("1", dexItem.getItemSequenceNumber());
        assertEquals("4.000", dexItem.getQuantity().toString());
        assertEquals(UnitMeasure.EA, dexItem.getUom());
        assertEquals("007189983548", dexItem.getUpc());
        assertEquals(null, dexItem.getConsumerProductQualifier());
        assertEquals(null, dexItem.getConsumerProductId());
        assertEquals(null, dexItem.getCaseUpc());
        assertEquals("2.13", dexItem.getItemListCost().toString());
        assertEquals(new Integer(1), dexItem.getPackCount());
        assertEquals("1/2 GAL TEST 1", dexItem.getItemDescription());
        assertEquals(null, dexItem.getCaseProductQualifier());
        assertEquals(null, dexItem.getCaseProductId());
        assertEquals(null, dexItem.getInnerPackCount());
        // G72 segment
        Dex894Allowance dexAllowance = dexItem.getAllowance();
        assertNotNull(dexAllowance);
        assertEquals("97", dexAllowance.getAllowanceCode());
        assertEquals("02", dexAllowance.getMethodOfHandlingCode());
        assertEquals(null, dexAllowance.getAllowanceNumber());
        assertEquals(null, dexAllowance.getExceptionNumber());
        assertEquals("-0.3500", dexAllowance.getAllowanceRate().toString());
        assertEquals(null, dexAllowance.getAllowanceQuantity());
        assertEquals(null, dexAllowance.getAllowanceUom());
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
        assertEquals("007189903720", dexItem.getUpc());
        assertEquals(null, dexItem.getConsumerProductQualifier());
        assertEquals(null, dexItem.getConsumerProductId());
        assertEquals(null, dexItem.getCaseUpc());
        assertEquals("2.25", dexItem.getItemListCost().toString());
        assertEquals(new Integer(1), dexItem.getPackCount());
        assertEquals("1/2 GAL TEST 2", dexItem.getItemDescription());
        assertEquals(null, dexItem.getCaseProductQualifier());
        assertEquals(null, dexItem.getCaseProductId());
        assertEquals(null, dexItem.getInnerPackCount());
        // G72 segment
        dexAllowance = dexItem.getAllowance();
        assertNull(dexAllowance);
    }

    @Test
    public void testParsingShipment_Dex4010_with_cases() throws IOException {
        byte[] dexBytes = Files.readAllBytes(Paths.get("src/test/resources/dex/894/dex.sample.4010.case.txt"));
        Dex894 dex = dexParser.parse(new String(dexBytes));
        assertNotNull(dex);
        Set<X12ErrorDetail> errors = dexValidator.validate(dex);
        assertNotNull(errors);
        assertEquals(0, errors.size());

        // DXS segment
        assertEquals("8050880508", dex.getSenderCommId());
        assertEquals("DX", dex.getFunctionalId());
        assertEquals("004010UCS", dex.getVersion());
        assertEquals("113", dex.getHeaderTransmissionControlNumber());
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
        assertEquals("0101", dexTx.getHeaderControlNumber());
        // SE segment
        assertEquals("0101", dexTx.getTrailerControlNumber());
        assertEquals(new Integer(9), dexTx.getExpectedNumberOfSegments());
        assertEquals(new Integer(9), dexTx.getActualNumberOfSegments());
        // G82 segment
        assertEquals(InvoiceType.D, dexTx.getDebitCreditFlag());
        assertEquals("001701001701", dexTx.getSupplierNumber());
        assertEquals("051957769", dexTx.getReceiverDuns());
        assertEquals("5", dexTx.getReceiverLocation());
        assertEquals("123456789", dexTx.getSupplierDuns());
        assertEquals("073002", dexTx.getSupplierLocation());
        assertEquals("20180416", dexTx.getTransactionDate());
        // G85 segment
        assertEquals("8263", dexTx.getIntegrityCheckValue());
        // G86 segment
        assertEquals("0840", dexTx.getElectronicSignature());
        assertEquals(null, dexTx.getSignatureName());
        // transaction data
        assertNotNull(dexTx.getTransactionData());
        assertTrue(dexTx.getTransactionData().startsWith("ST*894*0101"));
        assertTrue(dexTx.getTransactionData().contains("G83*1*2*CA*007800001180***007800001180*14*2*12z12P 7Up***\r\n"));
        assertTrue(dexTx.getTransactionData().endsWith("G86*0840\r\n"));

        // items
        List<Dex894Item> dexItemList = dexTx.getItems();
        assertNotNull(dexItemList);
        assertEquals(1, dexItemList.size());

        // DEX transaction (1) item (1)
        Dex894Item dexItem = dexItemList.get(0);
        assertNotNull(dexItem);
        // G83 segment
        assertEquals("1", dexItem.getItemSequenceNumber());
        assertEquals("2.000", dexItem.getQuantity().toString());
        assertEquals(UnitMeasure.CA, dexItem.getUom());
        assertEquals("007800001180", dexItem.getUpc());
        assertEquals(null, dexItem.getConsumerProductQualifier());
        assertEquals(null, dexItem.getConsumerProductId());
        assertEquals("007800001180", dexItem.getCaseUpc());
        assertEquals("14.00", dexItem.getItemListCost().toString());
        assertEquals(new Integer(2), dexItem.getPackCount());
        assertEquals("12z12P 7Up", dexItem.getItemDescription());
        assertEquals(null, dexItem.getCaseProductQualifier());
        assertEquals(null, dexItem.getCaseProductId());
        assertEquals(null, dexItem.getInnerPackCount());
        // G72 segment
        Dex894Allowance dexAllowance = dexItem.getAllowance();
        assertNull(dexAllowance);
    }

    @Test
    public void testParsingShipment_Dex5010_with_cases() throws IOException {
        byte[] dexBytes = Files.readAllBytes(Paths.get("src/test/resources/dex/894/dex.sample.5010.case.txt"));
        Dex894 dex = dexParser.parse(new String(dexBytes));
        assertNotNull(dex);
        Set<X12ErrorDetail> errors = dexValidator.validate(dex);
        assertNotNull(errors);
        // TODO: verify whether G8311 is required for CASE
        //assertEquals(0, errors.size());

        // DXS segment
        assertEquals("8050880508", dex.getSenderCommId());
        assertEquals("DX", dex.getFunctionalId());
        assertEquals("005010UCS", dex.getVersion());
        assertEquals("113", dex.getHeaderTransmissionControlNumber());
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
        assertEquals("0101", dexTx.getHeaderControlNumber());
        // SE segment
        assertEquals("0101", dexTx.getTrailerControlNumber());
        assertEquals(new Integer(9), dexTx.getExpectedNumberOfSegments());
        assertEquals(new Integer(9), dexTx.getActualNumberOfSegments());
        // G82 segment
        assertEquals(InvoiceType.D, dexTx.getDebitCreditFlag());
        assertEquals("001701001701", dexTx.getSupplierNumber());
        assertEquals("051957769", dexTx.getReceiverDuns());
        assertEquals("5", dexTx.getReceiverLocation());
        assertEquals("123456789", dexTx.getSupplierDuns());
        assertEquals("073002", dexTx.getSupplierLocation());
        assertEquals("20180416", dexTx.getTransactionDate());
        // G85 segment
        assertEquals("8263", dexTx.getIntegrityCheckValue());
        // G86 segment
        assertEquals("0840", dexTx.getElectronicSignature());
        assertEquals(null, dexTx.getSignatureName());
        // transaction data
        assertNotNull(dexTx.getTransactionData());
        assertTrue(dexTx.getTransactionData().startsWith("ST*894*0101"));
        assertTrue(dexTx.getTransactionData().contains("G83*1*1*CA**UK*00078000011807**14*2*12z12P 7Up***1\r\n"));
        assertTrue(dexTx.getTransactionData().endsWith("G86*0840\r\n"));

        // items
        List<Dex894Item> dexItemList = dexTx.getItems();
        assertNotNull(dexItemList);
        assertEquals(1, dexItemList.size());

        // DEX transaction (1) item (1)
        Dex894Item dexItem = dexItemList.get(0);
        assertNotNull(dexItem);
        // G83 segment
        assertEquals("1", dexItem.getItemSequenceNumber());
        assertEquals("1.000", dexItem.getQuantity().toString());
        assertEquals(UnitMeasure.CA, dexItem.getUom());
        assertEquals(null, dexItem.getUpc());
        assertEquals(ProductQualifier.UK, dexItem.getConsumerProductQualifier());
        assertEquals("00078000011807", dexItem.getConsumerProductId());
        assertEquals(null, dexItem.getCaseUpc());
        assertEquals("14.00", dexItem.getItemListCost().toString());
        assertEquals(new Integer(2), dexItem.getPackCount());
        assertEquals("12z12P 7Up", dexItem.getItemDescription());
        assertEquals(null, dexItem.getCaseProductQualifier());
        assertEquals(null, dexItem.getCaseProductId());
        assertEquals(new Integer(1), dexItem.getInnerPackCount());
        // G72 segment
        Dex894Allowance dexAllowance = dexItem.getAllowance();
        assertNull(dexAllowance);
    }
}
