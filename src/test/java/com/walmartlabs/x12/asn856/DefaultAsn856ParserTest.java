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
package com.walmartlabs.x12.asn856;

import com.walmartlabs.x12.common.InterchangeControlHeader;
import com.walmartlabs.x12.common.X12Group;
import com.walmartlabs.x12.common.X12TransactionSet;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DefaultAsn856ParserTest {

    DefaultAsn856Parser asnParser;

    @Before
    public void init() {
        asnParser = new DefaultAsn856Parser();
    }

    @Test
    public void test_Parsing_SourceIsNull() throws IOException {
        String sourceData = null;
        Asn856 asn = asnParser.parse(sourceData);
        assertNull(asn);
    }

    @Test
    public void test_Parsing_SourceIsEmpty() throws IOException {
        String sourceData = "";
        Asn856 asn = asnParser.parse(sourceData);
        assertNull(asn);
    }

    @Test
    public void test_Parsing_Asn856() throws IOException {
        byte[] asnBytes = Files.readAllBytes(Paths.get("src/test/resources/asn856/asn856.txt"));
        Asn856 asn = asnParser.parse(new String(asnBytes));
        assertNotNull(asn);

        // ISA segment
        InterchangeControlHeader isa = asn.getInterchangeControlHeader();
        assertNotNull(isa);
        assertEquals("01", isa.getAuthorizationInformationQualifier());
        assertEquals("0000000000", isa.getAuthorizationInformation());
        assertEquals("01", isa.getSecurityInformationQualifier());
        assertEquals("0000000000", isa.getSecurityInformation());
        assertEquals("ZZ", isa.getInterchangeIdQualifier());
        assertEquals("ABCDEFGHIJKLMNO", isa.getInterchangeSenderId());
        assertEquals("ZZ", isa.getInterchangeIdQualifier_2());
        assertEquals("123456789012345", isa.getInterchangeReceiverId());
        assertEquals("101127", isa.getInterchangeDate());
        assertEquals("1719", isa.getInterchangeTime());
        assertEquals("U", isa.getInterchangeControlStandardId());
        assertEquals("00400", isa.getInterchangeControlVersion());
        assertEquals("000003438", isa.getInterchangeControlNumber());
        assertEquals("0", isa.getAcknowledgementRequested());
        assertEquals("P", isa.getUsageIndicator());
        assertEquals(">", isa.getElementSeparator());

        // TODO: quick test
        List<X12Group> groups = asn.getGroups();
        assertNotNull(groups);
        assertEquals(1, groups.size());

        List<X12TransactionSet> txForGroupOne = asn.getGroups().get(0).getTransactions();
        assertNotNull(txForGroupOne);
        assertEquals(1, txForGroupOne.size());

        AsnTransactionSet asnTx = (AsnTransactionSet) txForGroupOne.get(0);
        assertEquals("TEST", asnTx.getSampleAsnOnly());
    }

}
