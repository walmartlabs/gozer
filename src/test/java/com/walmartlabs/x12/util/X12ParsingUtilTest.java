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

package com.walmartlabs.x12.util;

import com.walmartlabs.x12.X12Segment;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class X12ParsingUtilTest {

    @Test
    public void test_isValidEnvelope() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("TOP*1");
        segmentList.add(segment);
        segment = new X12Segment("MIDDLE*2");
        segmentList.add(segment);
        segment = new X12Segment("BOTTOM*3");
        segmentList.add(segment);

        assertTrue(X12ParsingUtil.isValidEnvelope(segmentList, "TOP", "BOTTOM"));
    }

    @Test
    public void test_isValidEnvelope_fails_missing_bottom() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("TOP*1");
        segmentList.add(segment);
        segment = new X12Segment("BOTTOM*2");
        segmentList.add(segment);
        segment = new X12Segment("MIDDLE*3");
        segmentList.add(segment);

        assertFalse(X12ParsingUtil.isValidEnvelope(segmentList, "TOP", "BOTTOM"));
    }

    @Test
    public void test_isValidEnvelope_fails_missing_top() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("HEAD*1");
        segmentList.add(segment);
        segment = new X12Segment("MIDDLE*2");
        segmentList.add(segment);
        segment = new X12Segment("BOTTOM*3");
        segmentList.add(segment);

        assertFalse(X12ParsingUtil.isValidEnvelope(segmentList, "TOP", "BOTTOM"));
    }

    @Test
    public void test_isValidEnvelope_fails_missing_both() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("HEAD*1");
        segmentList.add(segment);
        segment = new X12Segment("MIDDLE*2");
        segmentList.add(segment);
        segment = new X12Segment("ANOTHER*3");
        segmentList.add(segment);

        assertFalse(X12ParsingUtil.isValidEnvelope(segmentList, "TOP", "BOTTOM"));
    }

    @Test
    public void test_isValidEnvelope_null() {
        List<X12Segment> segmentList = null;
        assertFalse(X12ParsingUtil.isValidEnvelope(segmentList, "TOP", "BOTTOM"));
    }

    @Test
    public void test_isValidEnvelope_empty() {
        List<X12Segment> segmentList = Collections.emptyList();
        assertFalse(X12ParsingUtil.isValidEnvelope(segmentList, "TOP", "BOTTOM"));
    }

    @Test
    public void test_verifyTransactionSetType() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("ST*856*0001");
        segmentList.add(segment);
        segment = new X12Segment("BSN*00****0001");
        segmentList.add(segment);
        segment = new X12Segment("SE*1*0001");
        segmentList.add(segment);

        assertTrue(X12ParsingUtil.verifyTransactionSetType(segmentList, "856"));
    }

    @Test
    public void test_verifyTransactionSetType_wrong_type() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("ST*856*0001");
        segmentList.add(segment);
        segment = new X12Segment("BSN*00****0001");
        segmentList.add(segment);
        segment = new X12Segment("SE*1*0001");
        segmentList.add(segment);

        assertFalse(X12ParsingUtil.verifyTransactionSetType(segmentList, "999"));
    }

    @Test
    public void test_verifyTransactionSetType_partial_header() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("ST");
        segmentList.add(segment);
        segment = new X12Segment("BSN*00****0001");
        segmentList.add(segment);
        segment = new X12Segment("SE*1*0001");
        segmentList.add(segment);

        assertFalse(X12ParsingUtil.verifyTransactionSetType(segmentList, "999"));
    }

    @Test
    public void test_verifyTransactionSetType_wrong_first_line() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("XX*856*0001");
        segmentList.add(segment);
        segment = new X12Segment("BSN*00****0001");
        segmentList.add(segment);
        segment = new X12Segment("SE*1*0001");
        segmentList.add(segment);

        assertFalse(X12ParsingUtil.verifyTransactionSetType(segmentList, "856"));
    }

    @Test
    public void test_verifyTransactionSetType_null_type() {
        List<X12Segment> segmentList = new ArrayList<>();
        X12Segment segment = new X12Segment("ST*856*0001");
        segmentList.add(segment);
        segment = new X12Segment("BSN*00****0001");
        segmentList.add(segment);
        segment = new X12Segment("SE*1*0001");
        segmentList.add(segment);

        assertFalse(X12ParsingUtil.verifyTransactionSetType(segmentList, null));
    }

    @Test
    public void test_verifyTransactionSetType_null() {
        List<X12Segment> segmentList = null;
        assertFalse(X12ParsingUtil.verifyTransactionSetType(segmentList, "856"));
    }

    @Test
    public void test_verifyTransactionSetType_empty() {
        List<X12Segment> segmentList = Collections.emptyList();
        assertFalse(X12ParsingUtil.verifyTransactionSetType(segmentList, "856"));
    }

    @Test
    public void test_parseVersion() {
        assertNull(X12ParsingUtil.parseVersion(null));
        assertNull(X12ParsingUtil.parseVersion(""));
        assertEquals(new Integer(4010), X12ParsingUtil.parseVersion("004010UCS"));
        assertEquals(new Integer(5010), X12ParsingUtil.parseVersion("005010UCS"));
        assertEquals(new Integer(4010), X12ParsingUtil.parseVersion("4010"));
        assertEquals(new Integer(4010), X12ParsingUtil.parseVersion("004010"));
        assertEquals(new Integer(4010), X12ParsingUtil.parseVersion("4010UCS"));
    }

    @Test
    public void test_parseVersion_error_text() {
        assertNull(X12ParsingUtil.parseVersion("VERSION"));
    }

    @Test
    public void test_parseVersion_error_incorrect_ending() {
        assertNull(X12ParsingUtil.parseVersion("004010VERSION"));
    }

}
