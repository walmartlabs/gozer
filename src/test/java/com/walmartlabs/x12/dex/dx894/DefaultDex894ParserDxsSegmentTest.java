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

import com.walmartlabs.x12.X12Segment;
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
        X12Segment segment = new X12Segment("DXS*9251230013*DX*004010UCS*1*9254850000");
        dexParser.parseApplicationHeader(segment, dex);
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
        X12Segment segment = new X12Segment("DXS*9251230013*DX*004010UCS*1*9254850000*P");
        dexParser.parseApplicationHeader(segment, dex);
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
        X12Segment segment = new X12Segment("DXS**DX*004010UCS*1*9254850000");
        dexParser.parseApplicationHeader(segment, dex);
        assertEquals(null, dex.getSenderCommId());
        assertEquals("DX", dex.getFunctionalId());
        assertEquals("004010UCS", dex.getVersion());
        assertEquals("1", dex.getHeaderTransmissionControlNumber());
        assertEquals("9254850000", dex.getReceiverCommId());
        assertEquals(null, dex.getTestIndicator());
    }

    @Test
    public void testParseApplicationHeaderWithMissingReceiverCommId() {
        Dex894 dex = new Dex894();
        X12Segment segment = new X12Segment("DXS*9251230013*DX*004010UCS*1");
        dexParser.parseApplicationHeader(segment, dex);
        assertEquals("9251230013", dex.getSenderCommId());
        assertEquals("DX", dex.getFunctionalId());
        assertEquals("004010UCS", dex.getVersion());
        assertEquals("1", dex.getHeaderTransmissionControlNumber());
        assertEquals(null, dex.getReceiverCommId());
        assertEquals(null, dex.getTestIndicator());
    }

    @Test
    public void testParseApplicationHeaderInvalid() {
        Dex894 dex = new Dex894();
        X12Segment segment = new X12Segment("DXS*DX*004010UCS*1");
        dexParser.parseApplicationHeader(segment, dex);
        assertEquals("DX", dex.getSenderCommId());
        assertEquals("004010UCS", dex.getFunctionalId());
        assertEquals("1", dex.getVersion());
        assertEquals(null, dex.getHeaderTransmissionControlNumber());
        assertEquals(null, dex.getReceiverCommId());
        assertEquals(null, dex.getTestIndicator());
    }

    /*
     * DXE segment
     */
    @Test
    public void testParseApplicationTrailer() {
        Dex894 dex = new Dex894();
        X12Segment segment = new X12Segment("DXE*1*2");
        dexParser.parseApplicationTrailer(segment, dex);
        assertEquals("1", dex.getTrailerTransmissionControlNumber());
        assertEquals(new Integer(2), dex.getNumberOfTransactions());
    }

    @Test
    public void testParseApplicationTrailerWithMissingControlNumber() {
        Dex894 dex = new Dex894();
        X12Segment segment = new X12Segment("DXE**2");
        dexParser.parseApplicationTrailer(segment, dex);
        assertEquals(null, dex.getTrailerTransmissionControlNumber());
        assertEquals(new Integer(2), dex.getNumberOfTransactions());
    }

    @Test(expected = X12ParserException.class)
    public void testParseApplicationTrailerWithInvalidCount() {
        Dex894 dex = new Dex894();
        X12Segment segment = new X12Segment("DXE*1*DX");
        dexParser.parseApplicationTrailer(segment, dex);
    }

}
