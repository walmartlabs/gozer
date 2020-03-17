package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.TD1CarrierDetails;
import com.walmartlabs.x12.common.segment.parser.TD1CarrierDetailsParser;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.types.UnitMeasure;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class TD1CarrierDetailsParserTest {

    @Test
    public void test_parse_null_segment() {
        X12Segment segment = null;
        TD1CarrierDetails td1 = TD1CarrierDetailsParser.parse(segment);
        assertNull(td1);
    }

    @Test
    public void test_parse_empty_segment() {
        X12Segment segment = new X12Segment("");
        TD1CarrierDetails td1 = TD1CarrierDetailsParser.parse(segment);
        assertNull(td1);
    }

    @Test
    public void test_parse_segment() {
        X12Segment segment = new X12Segment("TD1*PLT94*1****G*31302*LB");
        TD1CarrierDetails td1 = TD1CarrierDetailsParser.parse(segment);
        assertNotNull(td1);
        assertEquals("PLT94", td1.getRawPackagingCode());
        assertEquals("PLT", td1.getPackagingCodePartOne());
        assertEquals("94", td1.getPackagingCodePartTwo());
        assertEquals("1", td1.getLadingQuantity().toString());
        assertEquals("G", td1.getWeightQualifier());
        assertEquals("31302.0000", td1.getWeight().toString());
        assertEquals(UnitMeasure.LB, td1.getUnitOfMeasureCode());
    }

    @Test
    public void test_parse_segment_bad_uom() {
        X12Segment segment = new X12Segment("TD1*PLT*1****G*31302*XX");
        TD1CarrierDetails td1 = TD1CarrierDetailsParser.parse(segment);
        assertNotNull(td1);
        assertEquals("PLT", td1.getRawPackagingCode());
        assertEquals("PLT", td1.getPackagingCodePartOne());
        assertEquals("", td1.getPackagingCodePartTwo());
        assertEquals("1", td1.getLadingQuantity().toString());
        assertEquals("G", td1.getWeightQualifier());
        assertEquals("31302.0000", td1.getWeight().toString());
        assertEquals(UnitMeasure.UNKNOWN, td1.getUnitOfMeasureCode());
    }

    @Test
    public void test_parse_segment_bad_packagingCode() {
        X12Segment segment = new X12Segment("TD1*PLT999*1****G*31302*LB");
        TD1CarrierDetails td1 = TD1CarrierDetailsParser.parse(segment);
        assertNotNull(td1);
        assertEquals("PLT999", td1.getRawPackagingCode());
        assertEquals(null, td1.getPackagingCodePartOne());
        assertEquals(null, td1.getPackagingCodePartTwo());
        assertEquals("1", td1.getLadingQuantity().toString());
        assertEquals("G", td1.getWeightQualifier());
        assertEquals("31302.0000", td1.getWeight().toString());
        assertEquals(UnitMeasure.LB, td1.getUnitOfMeasureCode());
    }

    @Test
    public void test_parse_segment_no_packagingCode() {
        X12Segment segment = new X12Segment("TD1**1****G*31302*LB");
        TD1CarrierDetails td1 = TD1CarrierDetailsParser.parse(segment);
        assertNotNull(td1);
        assertEquals(null, td1.getRawPackagingCode());
        assertEquals(null, td1.getPackagingCodePartOne());
        assertEquals(null, td1.getPackagingCodePartTwo());
        assertEquals("1", td1.getLadingQuantity().toString());
        assertEquals("G", td1.getWeightQualifier());
        assertEquals("31302.0000", td1.getWeight().toString());
        assertEquals(UnitMeasure.LB, td1.getUnitOfMeasureCode());
    }
    
    @Test(expected = X12ParserException.class)
    public void test_parse_segment_bad_quantity() {
        X12Segment segment = new X12Segment("TD1**X****G*31302*LB");
        TD1CarrierDetailsParser.parse(segment);
    }

}
