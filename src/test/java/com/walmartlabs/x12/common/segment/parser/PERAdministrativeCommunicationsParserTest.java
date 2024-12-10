package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.PERAdministrativeCommunication;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class PERAdministrativeCommunicationsParserTest {

    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        List<PERAdministrativeCommunication> perList = PERAdministrativeCommunicationsParser.parse(segment);
        assertNotNull(perList);
        assertEquals(0, perList.size());
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        List<PERAdministrativeCommunication> perList = PERAdministrativeCommunicationsParser.parse(segment);
        assertNotNull(perList);
        assertEquals(0, perList.size());
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("PER*PY*FSMA CONTACT*TE*+18884636332*EA*FoodSafetyPlanBuilder@fda.hhs.gov");
        List<PERAdministrativeCommunication> perList = PERAdministrativeCommunicationsParser.parse(segment);
        assertNotNull(perList);
        assertEquals(2, perList.size());

        PERAdministrativeCommunication perAdministrativeCommunication = perList.get(0);
        assertEquals("PY", perAdministrativeCommunication.getContactFunctionCode());
        assertEquals("FSMA CONTACT", perAdministrativeCommunication.getFreeFormName());
        assertEquals("TE", perAdministrativeCommunication.getCommunicationNumberQualifier());
        assertEquals("+18884636332", perAdministrativeCommunication.getCommunicationNumber());

        perAdministrativeCommunication = perList.get(1);
        assertEquals("PY", perAdministrativeCommunication.getContactFunctionCode());
        assertEquals("FSMA CONTACT", perAdministrativeCommunication.getFreeFormName());
        assertEquals("EA", perAdministrativeCommunication.getCommunicationNumberQualifier());
        assertEquals("FoodSafetyPlanBuilder@fda.hhs.gov", perAdministrativeCommunication.getCommunicationNumber());
    }

    @Test
    public void test_parse_segment_ur() {
        X12Segment segment = new X12Segment("PER*PY*FSMA CONTACT*UR*localhost:8080");
        List<PERAdministrativeCommunication> perList = PERAdministrativeCommunicationsParser.parse(segment);
        assertNotNull(perList);
        assertEquals(1, perList.size());

        PERAdministrativeCommunication perAdministrativeCommunication = perList.get(0);
        assertEquals("PY", perAdministrativeCommunication.getContactFunctionCode());
        assertEquals("FSMA CONTACT", perAdministrativeCommunication.getFreeFormName());
        assertEquals("UR", perAdministrativeCommunication.getCommunicationNumberQualifier());
        assertEquals("localhost:8080", perAdministrativeCommunication.getCommunicationNumber());
    }

    @Test
    public void test_parse_segment_not_balanced() {
        X12Segment segment = new X12Segment("PER*PY*FSMA CONTACT*TE*+18884636332*EA");
        List<PERAdministrativeCommunication> perList = PERAdministrativeCommunicationsParser.parse(segment);
        assertNotNull(perList);
        assertEquals(1, perList.size());

        PERAdministrativeCommunication perAdministrativeCommunication = perList.get(0);
        assertEquals("PY", perAdministrativeCommunication.getContactFunctionCode());
        assertEquals("FSMA CONTACT", perAdministrativeCommunication.getFreeFormName());
        assertEquals("TE", perAdministrativeCommunication.getCommunicationNumberQualifier());
        assertEquals("+18884636332", perAdministrativeCommunication.getCommunicationNumber());
    }

}