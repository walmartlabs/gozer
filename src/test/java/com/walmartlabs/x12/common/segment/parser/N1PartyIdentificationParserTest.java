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

package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.SegmentIterator;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.N1PartyIdentification;
import com.walmartlabs.x12.common.segment.N3PartyLocation;
import com.walmartlabs.x12.common.segment.N4GeographicLocation;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class N1PartyIdentificationParserTest {

    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        N1PartyIdentification n1 = N1PartyIdentificationParser.parse(segment);
        assertNull(n1);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        N1PartyIdentification n1 = N1PartyIdentificationParser.parse(segment);
        assertNull(n1);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("N1*ST*REGIONAL DISTRIBUTION CENTER 6285*UL*0078742090955");
        N1PartyIdentification n1 = N1PartyIdentificationParser.parse(segment);
        assertNotNull(n1);
        assertEquals("ST", n1.getEntityIdentifierCode());
        assertEquals("REGIONAL DISTRIBUTION CENTER 6285", n1.getName());
        assertEquals("UL", n1.getIdentificationCodeQualifier());
        assertEquals("0078742090955", n1.getIdentificationCode().toString());
    }

    @Test
    public void test_parse_segment_bad_identifier() {
        X12Segment segment = new X12Segment("XX*ST*REGIONAL DISTRIBUTION CENTER 6285*UL*0078742090955");
        N1PartyIdentification n1 = N1PartyIdentificationParser.parse(segment);
        assertNull(n1);
    }

    @Test
    public void test_parse_handleN1Loop_null() {
        SegmentIterator iterator = null;
        X12Segment n1Segment = null;

        N1PartyIdentification n1 = N1PartyIdentificationParser.handleN1Loop(n1Segment, iterator);
        assertNull(n1);
    }

    @Test
    public void test_parse_handleN1Loop_one_loop_ends() {
        List<X12Segment> segments = this.getN1LoopOne();
        SegmentIterator iterator = new SegmentIterator(segments);
        X12Segment n1Segment = iterator.next();

        N1PartyIdentification n1 = N1PartyIdentificationParser.handleN1Loop(n1Segment, iterator);
        assertNotNull(n1);
        assertEquals("ST", n1.getEntityIdentifierCode());
        assertEquals("REGIONAL DISTRIBUTION CENTER 6285", n1.getName());
        assertEquals("UL", n1.getIdentificationCodeQualifier());
        assertEquals("0078742090955", n1.getIdentificationCode().toString());

        N3PartyLocation n3 = n1.getN3();
        assertNotNull(n3);
        assertEquals("868 W. PETERS ROAD", n3.getAddressInfoOne());
        assertEquals(null, n3.getAddressInfoTwo());

        N4GeographicLocation n4 = n1.getN4();
        assertNotNull(n4);
        assertEquals("CASA GRANDE", n4.getCityName());
        assertEquals("AZ", n4.getStateOrProvinceCode());
        assertEquals("85193", n4.getPostalCode());
        assertEquals(null, n4.getCountryCode());

        assertFalse(iterator.hasNext());
    }

    @Test
    public void test_parse_handleN1Loop_one_loop_more() {
        List<X12Segment> segments = this.getN1LoopOne();
        segments.add(new X12Segment("N1*SF*RESER'S FINE FOODS, INC.*UL*0090266420000"));
        SegmentIterator iterator = new SegmentIterator(segments);
        X12Segment n1Segment = iterator.next();

        N1PartyIdentification n1 = N1PartyIdentificationParser.handleN1Loop(n1Segment, iterator);
        assertNotNull(n1);
        assertEquals("ST", n1.getEntityIdentifierCode());
        assertEquals("REGIONAL DISTRIBUTION CENTER 6285", n1.getName());
        assertEquals("UL", n1.getIdentificationCodeQualifier());
        assertEquals("0078742090955", n1.getIdentificationCode().toString());

        N3PartyLocation n3 = n1.getN3();
        assertNotNull(n3);
        assertEquals("868 W. PETERS ROAD", n3.getAddressInfoOne());
        assertEquals(null, n3.getAddressInfoTwo());

        N4GeographicLocation n4 = n1.getN4();
        assertNotNull(n4);
        assertEquals("CASA GRANDE", n4.getCityName());
        assertEquals("AZ", n4.getStateOrProvinceCode());
        assertEquals("85193", n4.getPostalCode());
        assertEquals(null, n4.getCountryCode());

        assertTrue(iterator.hasNext());
        X12Segment nextOne = iterator.next();
        assertNotNull(nextOne);
        assertEquals("N1*SF*RESER'S FINE FOODS, INC.*UL*0090266420000", nextOne.toString());
    }

    @Test
    public void test_parse_handleN1Loop_one_N1_only() {
        List<X12Segment> segments = new ArrayList<>();
        segments.add(new X12Segment("N1*ST*REGIONAL DISTRIBUTION CENTER 6285*UL*0078742090955B"));
        segments.add(new X12Segment("N1*SF*RESER'S FINE FOODS, INC.*UL*0090266420000"));
        SegmentIterator iterator = new SegmentIterator(segments);
        X12Segment topSegment = iterator.next();

        N1PartyIdentification n1 = N1PartyIdentificationParser.handleN1Loop(topSegment, iterator);
        assertNotNull(n1);
        assertEquals("ST", n1.getEntityIdentifierCode());
        assertEquals("REGIONAL DISTRIBUTION CENTER 6285", n1.getName());
        assertEquals("UL", n1.getIdentificationCodeQualifier());
        assertEquals("0078742090955B", n1.getIdentificationCode().toString());

        N3PartyLocation n3 = n1.getN3();
        assertNull(n3);

        N4GeographicLocation n4 = n1.getN4();
        assertNull(n4);

        assertTrue(iterator.hasNext());
        X12Segment nextOne = iterator.next();
        assertNotNull(nextOne);
        assertEquals("N1*SF*RESER'S FINE FOODS, INC.*UL*0090266420000", nextOne.toString());
    }

    @Test
    public void test_parse_handleN1Loop_one_not_N1() {
        List<X12Segment> segments = new ArrayList<>();
        segments.add(new X12Segment("TD1*PLT94*1****G*31302*LB"));
        segments.addAll(this.getN1LoopOne());
        SegmentIterator iterator = new SegmentIterator(segments);
        X12Segment topSegment = iterator.next();

        N1PartyIdentification n1 = N1PartyIdentificationParser.handleN1Loop(topSegment, iterator);
        assertNull(n1);

        assertTrue(iterator.hasNext());
        X12Segment nextOne = iterator.next();
        assertNotNull(nextOne);
        assertEquals("N1*ST*REGIONAL DISTRIBUTION CENTER 6285*UL*0078742090955", nextOne.toString());
    }

    private List<X12Segment> getN1LoopOne() {
        List<X12Segment> segments = new ArrayList<>();
        X12Segment segment = new X12Segment("N1*ST*REGIONAL DISTRIBUTION CENTER 6285*UL*0078742090955");
        segments.add(segment);
        segment = new X12Segment("N3*868 W. PETERS ROAD");
        segments.add(segment);
        segment = new X12Segment("N4*CASA GRANDE*AZ*85193");
        segments.add(segment);

        return segments;
    }

}
