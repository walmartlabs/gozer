/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.walmartlabs.x12.dex.dx894;

import com.walmartlabs.x12.exceptions.X12ParserException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultDex894ParserTransactionTest {

    DefaultDex894Parser dexParser;

    @Before
    public void init() {
        dexParser = new DefaultDex894Parser();
    }

    /*
     * ST segement
     */
    @Test
    public void testParseTransactionSetHeader() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseTransactionSetHeader("ST*894*569145629", dexTx);
        assertEquals("894", dexTx.getTransactionSetIdentifierCode());
        assertEquals("569145629", dexTx.getHeaderControlNumber());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testParseTransactionSetHeaderMissingControlNumber() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseTransactionSetHeader("ST*894*", dexTx);
    }

    @Test
    public void testParseTransactionSetHeaderMissingIdCode() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseTransactionSetHeader("ST**569145629", dexTx);
        assertEquals("", dexTx.getTransactionSetIdentifierCode());
        assertEquals("569145629", dexTx.getHeaderControlNumber());
    }


    @Test(expected = X12ParserException.class)
    public void testParseTransactionSetHeaderWrongSegmentIdentifier() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseTransactionSetHeader("XX*894*569145629", dexTx);
    }

    /*
     * G82 segement
     */
    @Test
    public void testParseG82() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseG82("G82*D*8327063806*051957769*004615*182737015*PL1124*20171116", dexTx);
        assertEquals("D", dexTx.getDebitCreditFlag());
        assertEquals("8327063806", dexTx.getSupplierNumber());
        assertEquals("051957769", dexTx.getReceiverDuns());
        assertEquals("004615", dexTx.getReceiverLocation());
        assertEquals("182737015", dexTx.getSupplierDuns());
        assertEquals("PL1124", dexTx.getSupplierLocation());
        assertEquals("20171116", dexTx.getTransactionDate());
        assertEquals(null, dexTx.getPurchaseOrderNumber());
        assertEquals(null, dexTx.getPurchaseOrderDate());
    }

    @Test
    public void testParseG82WithPurchaseOrder() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseG82("G82*D*8327063806*051957769*004615*182737015*PL1124*20171116*PO123*20171004", dexTx);
        assertEquals("D", dexTx.getDebitCreditFlag());
        assertEquals("8327063806", dexTx.getSupplierNumber());
        assertEquals("051957769", dexTx.getReceiverDuns());
        assertEquals("004615", dexTx.getReceiverLocation());
        assertEquals("182737015", dexTx.getSupplierDuns());
        assertEquals("PL1124", dexTx.getSupplierLocation());
        assertEquals("20171116", dexTx.getTransactionDate());
        assertEquals("PO123", dexTx.getPurchaseOrderNumber());
        assertEquals("20171004", dexTx.getPurchaseOrderDate());
    }

    @Test(expected = X12ParserException.class)
    public void testParseG82WrongSegmentId() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseG82("G89*D*8327063806*051957769*004615*182737015*PL1124*20171116", dexTx);
    }

    /*
     * G83 segement
     */
    @Test
    public void testParseG83SimpleItem() {
        Dex894Item dexItem = new Dex894Item();
        dexParser.parseG83("G83*1*48*EA*001410008547****1.83", dexItem);
        assertEquals("1", dexItem.getItemSequenceNumber());
        assertEquals("48.000", dexItem.getQuantity().toString());
        assertEquals(UnitMeasure.EA, dexItem.getUom());
        assertEquals("001410008547", dexItem.getUpc());
        assertEquals(null, dexItem.getConsumerProductQualifier());
        assertEquals("", dexItem.getConsumerProductId());
        assertEquals("", dexItem.getCaseUpc());
        assertEquals("1.83", dexItem.getItemListCost().toString());
        assertEquals(null, dexItem.getPackCount());
        assertEquals(null, dexItem.getItemDescription());
        assertEquals(null, dexItem.getCaseProductQualifier());
        assertEquals(null, dexItem.getCaseProductId());
        assertEquals(null, dexItem.getInnerPackCount());
    }

    @Test
    public void testParseG83WithDescription() {
        Dex894Item dexItem = new Dex894Item();
        dexParser.parseG83("G83*1*48*EA*001410008547****1.83**DESCRIPTION", dexItem);
        assertEquals("1", dexItem.getItemSequenceNumber());
        assertEquals("48.000", dexItem.getQuantity().toString());
        assertEquals(UnitMeasure.EA, dexItem.getUom());
        assertEquals("001410008547", dexItem.getUpc());
        assertEquals(null, dexItem.getConsumerProductQualifier());
        assertEquals("", dexItem.getConsumerProductId());
        assertEquals("", dexItem.getCaseUpc());
        assertEquals("1.83", dexItem.getItemListCost().toString());
        assertEquals(null, dexItem.getPackCount());
        assertEquals("DESCRIPTION", dexItem.getItemDescription());
        assertEquals(null, dexItem.getCaseProductQualifier());
        assertEquals(null, dexItem.getCaseProductId());
        assertEquals(null, dexItem.getInnerPackCount());
    }

    @Test(expected = X12ParserException.class)
    public void testParseG83WrongSegmentId() {
        Dex894Item dexItem = new Dex894Item();
        dexParser.parseG83("XX*1*48*EA*001410008547****1.83", dexItem);
    }

    /*
     * G85 segement
     */
    @Test
    public void testParseG85() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseG85("G85*A238", dexTx);
        assertEquals("A238", dexTx.getIntegrityCheckValue());
    }

    @Test(expected = X12ParserException.class)
    public void testParseG856WrongSegmentId() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseG85("XX*A238", dexTx);
    }

    /*
     * G86 segement
     */
    @Test
    public void testParseG86() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseG86("G86*C91456300976", dexTx);
        assertEquals("C91456300976", dexTx.getElectronicSignature());
    }

    @Test(expected = X12ParserException.class)
    public void testParseG86WrongSegmentId() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseG86("XX*C91456300976", dexTx);
    }

    /*
     * SE segement
     */
    @Test
    public void testParseTransactionSetTrailer() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseTransactionSetTrailer("SE*10*569145629", dexTx);
        assertEquals(new Integer(10), dexTx.getExpectedNumberOfSegments());
        assertEquals("569145629", dexTx.getTrailerControlNumber());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testParseTransactionSetTrailerMissingControlNumber() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseTransactionSetTrailer("SE*10*", dexTx);
    }

    @Test
    public void testParseTransactionSetTrailerMissingSegmentCount() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseTransactionSetTrailer("SE**569145629", dexTx);
        assertEquals(null, dexTx.getExpectedNumberOfSegments());
        assertEquals("569145629", dexTx.getTrailerControlNumber());
    }

    @Test(expected = X12ParserException.class)
    public void testParseTransactionSetTrailerWrongSegmentCount() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseTransactionSetTrailer("SE*XX*569145629", dexTx);
    }

    @Test(expected = X12ParserException.class)
    public void testParseTransactionSetTrailerWrongSegmentIdentifier() {
        Dex894TransactionSet dexTx = new Dex894TransactionSet();
        dexParser.parseTransactionSetTrailer("XX*10*569145629", dexTx);
    }

}
