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

public class DefaultDex894ParserDxsSegmentTest {

    DefaultDex894Parser dexParser;

    @Before
    public void init() {
        dexParser = new DefaultDex894Parser();
    }

    /*
     * DXS segment
     */
    @Test
    public void testParseApplicationHeader() {
        Dex894 dex = new Dex894();
        dexParser.parseApplicationHeader("DXS*9251230013*DX*004010UCS*1*9254850000", dex);
        assertEquals("9251230013", dex.getSenderCommId());
        assertEquals("DX", dex.getFunctionalId());
        assertEquals("004010UCS", dex.getVersion());
        assertEquals("1", dex.getHeaderTransmissionControlNumber());
        assertEquals("9254850000", dex.getReceiverCommId());
        assertEquals(null, dex.getTestIndicator());
    }

    @Test
    public void testParseApplicationHeaderWithTestIndicator() {
        Dex894 dex = new Dex894();
        dexParser.parseApplicationHeader("DXS*9251230013*DX*004010UCS*1*9254850000*P", dex);
        assertEquals("9251230013", dex.getSenderCommId());
        assertEquals("DX", dex.getFunctionalId());
        assertEquals("004010UCS", dex.getVersion());
        assertEquals("1", dex.getHeaderTransmissionControlNumber());
        assertEquals("9254850000", dex.getReceiverCommId());
        assertEquals("P", dex.getTestIndicator());
    }

    @Test
    public void testParseApplicationHeaderWithMissingSenderCommId() {
        Dex894 dex = new Dex894();
        dexParser.parseApplicationHeader("DXS**DX*004010UCS*1*9254850000", dex);
        assertEquals("", dex.getSenderCommId());
        assertEquals("DX", dex.getFunctionalId());
        assertEquals("004010UCS", dex.getVersion());
        assertEquals("1", dex.getHeaderTransmissionControlNumber());
        assertEquals("9254850000", dex.getReceiverCommId());
        assertEquals(null, dex.getTestIndicator());
    }

    @Test
    public void testParseApplicationHeaderWithMissingReceiverCommId() {
        Dex894 dex = new Dex894();
        dexParser.parseApplicationHeader("DXS*9251230013*DX*004010UCS*1", dex);
        assertEquals("9251230013", dex.getSenderCommId());
        assertEquals("DX", dex.getFunctionalId());
        assertEquals("004010UCS", dex.getVersion());
        assertEquals("1", dex.getHeaderTransmissionControlNumber());
        assertEquals(null, dex.getReceiverCommId());
        assertEquals(null, dex.getTestIndicator());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testParseApplicationHeaderInvalid() {
        Dex894 dex = new Dex894();
        dexParser.parseApplicationHeader("DXS*DX*004010UCS*1", dex);
    }

    /*
     * DXE segment
     */
    @Test
    public void testParseApplicationTrailer() {
        Dex894 dex = new Dex894();
        dexParser.parseApplicationTrailer("DXE*1*2", dex);
        assertEquals("1", dex.getTrailerTransmissionControlNumber());
        assertEquals(new Integer(2), dex.getNumberOfTransactions());
    }

    @Test
    public void testParseApplicationTrailerWithMissingControlNumber() {
        Dex894 dex = new Dex894();
        dexParser.parseApplicationTrailer("DXE**2", dex);
        assertEquals("", dex.getTrailerTransmissionControlNumber());
        assertEquals(new Integer(2), dex.getNumberOfTransactions());
    }

    @Test(expected = X12ParserException.class)
    public void testParseApplicationTrailerWithInvalidCount() {
        Dex894 dex = new Dex894();
        dexParser.parseApplicationTrailer("DXE*1*DX", dex);
    }

}
