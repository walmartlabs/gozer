package com.walmartlabs.x12.rule;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.StandardX12Parser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

public class TrailerSegmentCountX12RuleTest {
    
    @Rule 
    public ExpectedException exception = ExpectedException.none();
    
    private TrailerSegmentCountX12Rule rule;
    
    @Before
    public void init() {
        rule = new TrailerSegmentCountX12Rule();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_verify_null() {
        List<X12Segment> segmentList = null;
        rule.verify(segmentList);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void test_verify_empty() {
        List<X12Segment> segmentList = null;
        rule.verify(segmentList);
    }

    @Test
    public void test_one_group_correct() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804191*20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("ST*850*0003")
            .append("\r\n")
            .append("BEG*00*SA*804191**20201022")
            .append("\r\n")
            .append("SE*1*0003")
            .append("\r\n")            
            .append("GE*3*99")
            .append("\r\n")
            .append("IEA*1*000000049")
            .toString();

        List<X12Segment> segmentList = new StandardX12Parser().splitSourceDataIntoSegments(sourceData.trim());
        rule.verify(segmentList);
    }
    
    @Test
    public void test_two_group_correct() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804191*20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")            
            .append("GE*2*99")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*100*X*004060")
            .append("\r\n")
            .append("ST*850*0003")
            .append("\r\n")
            .append("BEG*00*SA*804191**20201022")
            .append("\r\n")
            .append("SE*1*0003")
            .append("\r\n")            
            .append("GE*1*100")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();

        List<X12Segment> segmentList = new StandardX12Parser().splitSourceDataIntoSegments(sourceData.trim());
        rule.verify(segmentList);
    }
    
    @Test
    public void test_one_group_missing_group_count() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804191*20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("ST*850*0003")
            .append("\r\n")
            .append("BEG*00*SA*804191**20201022")
            .append("\r\n")
            .append("SE*1*0003")
            .append("\r\n")            
            .append("GE*3*99")
            .append("\r\n")
            // IEA missing group count
            .append("IEA**000000049")
            .toString();
        
        exception.expect(X12ParserException.class);
        exception.expectMessage("incorrect number of groups on IEA trailer");

        List<X12Segment> segmentList = new StandardX12Parser().splitSourceDataIntoSegments(sourceData.trim());
        rule.verify(segmentList);
    }
    
    @Test
    public void test_one_group_alpha_group_count() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804191*20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("ST*850*0003")
            .append("\r\n")
            .append("BEG*00*SA*804191**20201022")
            .append("\r\n")
            .append("SE*1*0003")
            .append("\r\n")            
            .append("GE*3*99")
            .append("\r\n")
            // IEA says it expects A groups
            .append("IEA*A*000000049")
            .toString();
        
        exception.expect(X12ParserException.class);
        exception.expectMessage("Invalid numeric value");

        List<X12Segment> segmentList = new StandardX12Parser().splitSourceDataIntoSegments(sourceData.trim());
        rule.verify(segmentList);
    }
    
    @Test
    public void test_two_group_mismatch_control_numbers() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01")
            .append("\r\n")
            // Group 99
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804191*20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            // end for Group 100 (should be 99)
            .append("GE*2*100")
            .append("\r\n")
            // Group 100
            .append("GS*SH*4405197800*999999999*20111206*1045*100*X*004060")
            .append("\r\n")
            .append("ST*850*0003")
            .append("\r\n")
            .append("BEG*00*SA*804191**20201022")
            .append("\r\n")
            .append("SE*1*0003")
            .append("\r\n")
            // end for Group 99 (should be 100)
            .append("GE*1*99")
            .append("\r\n")
            .append("IEA*2*000000049")
            .toString();
        
        exception.expect(X12ParserException.class);
        exception.expectMessage("groups seem to be misaligned");

        List<X12Segment> segmentList = new StandardX12Parser().splitSourceDataIntoSegments(sourceData.trim());
        rule.verify(segmentList);
    }
    
    @Test
    public void test_one_group_incorrect_group_count() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804191*20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("ST*850*0003")
            .append("\r\n")
            .append("BEG*00*SA*804191**20201022")
            .append("\r\n")
            .append("SE*1*0003")
            .append("\r\n")            
            .append("GE*3*99")
            .append("\r\n")
            // IEA says it expects 10 groups
            // but only have 1 group
            .append("IEA*10*000000049")
            .toString();

        exception.expect(X12ParserException.class);
        exception.expectMessage("incorrect number of groups on IEA trailer");
        
        List<X12Segment> segmentList = new StandardX12Parser().splitSourceDataIntoSegments(sourceData.trim());
        rule.verify(segmentList);
    }
    
    @Test
    public void test_one_group_missing_isa() {
        String sourceData = new StringBuilder()
            // because ISA is missing
            // the segment delimiter can't be determined
            // that causes an error in parsing the IEA segment
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804191*20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("ST*850*0003")
            .append("\r\n")
            .append("BEG*00*SA*804191**20201022")
            .append("\r\n")
            .append("SE*1*0003")
            .append("\r\n")            
            .append("GE*3*99")
            .append("\r\n")
            .append("IEA*1*000000049")
            .toString();

        exception.expect(X12ParserException.class);
        exception.expectMessage("missing IEA segment");
        
        List<X12Segment> segmentList = new StandardX12Parser().splitSourceDataIntoSegments(sourceData.trim());
        rule.verify(segmentList);
    }
    
    @Test
    public void test_one_group_missing_iea() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804191*20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("ST*850*0003")
            .append("\r\n")
            .append("BEG*00*SA*804191**20201022")
            .append("\r\n")
            .append("SE*1*0003")
            .append("\r\n")            
            .append("GE*3*99")
            .append("\r\n")
            .toString();

        exception.expect(X12ParserException.class);
        exception.expectMessage("missing IEA segment");
        
        List<X12Segment> segmentList = new StandardX12Parser().splitSourceDataIntoSegments(sourceData.trim());
        rule.verify(segmentList);
    }
    
    @Test
    public void test_one_group_missing_gs() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ")
            .append("\r\n")
            // should be a GS
            .append("XX*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804191*20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("ST*850*0003")
            .append("\r\n")
            .append("BEG*00*SA*804191**20201022")
            .append("\r\n")
            .append("SE*1*0003")
            .append("\r\n")            
            .append("GE*3*99")
            .append("\r\n")
            .append("IEA*1*000000049")
            .toString();

        exception.expect(X12ParserException.class);
        exception.expectMessage("groups seem to be misaligned");
        
        List<X12Segment> segmentList = new StandardX12Parser().splitSourceDataIntoSegments(sourceData.trim());
        rule.verify(segmentList);
    }
    
    @Test
    public void test_one_group_missing_ge() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804191*20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("ST*850*0003")
            .append("\r\n")
            .append("BEG*00*SA*804191**20201022")
            .append("\r\n")
            .append("SE*1*0003")
            .append("\r\n")
            // no GE 
            .append("IEA*1*000000049")
            .toString();

        exception.expect(X12ParserException.class);
        exception.expectMessage("incorrect number of groups on IEA trailer");
        
        List<X12Segment> segmentList = new StandardX12Parser().splitSourceDataIntoSegments(sourceData.trim());
        rule.verify(segmentList);
    }
    

    @Test
    public void test_one_group_alpha_transaction_count() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804191*20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("ST*850*0003")
            .append("\r\n")
            .append("BEG*00*SA*804191**20201022")
            .append("\r\n")
            .append("SE*1*0003")
            .append("\r\n")  
            // transaction count is A instead of number
            .append("GE*A*99")
            .append("\r\n")
            .append("IEA*1*000000049")
            .toString();

        exception.expect(X12ParserException.class);
        exception.expectMessage("Invalid numeric value");
        
        List<X12Segment> segmentList = new StandardX12Parser().splitSourceDataIntoSegments(sourceData.trim());
        rule.verify(segmentList);
    }
    
    @Test
    public void test_one_group_missing_transaction_count() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804191*20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("ST*850*0003")
            .append("\r\n")
            .append("BEG*00*SA*804191**20201022")
            .append("\r\n")
            .append("SE*1*0003")
            .append("\r\n")  
            // transaction count is missing
            .append("GE**99")
            .append("\r\n")
            .append("IEA*1*000000049")
            .toString();

        exception.expect(X12ParserException.class);
        exception.expectMessage("incorrect number of transactions on group");
        
        List<X12Segment> segmentList = new StandardX12Parser().splitSourceDataIntoSegments(sourceData.trim());
        rule.verify(segmentList);
    }

}
