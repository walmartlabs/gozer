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

package com.walmartlabs.x12.rule;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.util.SourceToSegmentUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class UniqueDocumentX12RuleTest {


    private UniqueDocumentX12Rule rule;

    @Test(expected = IllegalArgumentException.class)
    public void test_verify_null() {
        List<X12Segment> segmentList = null;

        rule = new UniqueDocumentX12Rule("BSN", 2, 3);
        rule.verify(segmentList);
    }

    @Test
    public void test_verify_empty() {
        List<X12Segment> segmentList = new ArrayList<>();

        rule = new UniqueDocumentX12Rule("BSN", 2, 3);
        rule.verify(segmentList);
    }

    @Test
    public void test_no_duplicates_mix_document() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            // ASN #1
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            // ASN #2
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804191*20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            // PO with same document number as ASN
            .append("ST*850*0003")
            .append("\r\n")
            .append("BEG*00*SA*804191**20201022")
            .append("\r\n")
            .append("SE*1*0003")
            .append("\r\n")
            .append("GE*1*99")
            .append("\r\n")
            .append("IEA*1*000000049")
            .toString();

        List<X12Segment> segmentList = SourceToSegmentUtil.splitSourceDataIntoSegments(sourceData.trim());

        rule = new UniqueDocumentX12Rule("BSN", 2, 3);
        rule.verify(segmentList);
    }


    @Test(expected = X12ParserException.class)
    public void test_duplicates_mix_document() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            // ASN #1
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            // ASN #2
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804191*20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            // PO with same document number as ASN
            .append("ST*850*0003")
            .append("\r\n")
            .append("BEG*00*SA*804191**20201022")
            .append("\r\n")
            .append("SE*1*0003")
            // ASN #3 same as #1
            .append("ST*856*0004")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0004")
            .append("\r\n")
            .append("GE*1*99")
            .append("\r\n")
            .append("IEA*1*000000049")
            .toString();

        List<X12Segment> segmentList = SourceToSegmentUtil.splitSourceDataIntoSegments(sourceData.trim());

        rule = new UniqueDocumentX12Rule("BSN", 2, 3);
        rule.verify(segmentList);
    }

    @Test(expected = X12ParserException.class)
    public void test_duplicates_po_document() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            // PO #1
            .append("ST*850*0001")
            .append("\r\n")
            .append("BEG*00*SA*804190**20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            // PO #2
            .append("ST*856*0002")
            .append("\r\n")
            .append("BEG*00*SA*804191**20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            // PO #3 same as #1
            .append("ST*850*0003")
            .append("\r\n")
            .append("BEG*00*SA*804190**20201022")
            .append("\r\n")
            .append("SE*1*0003")
            .append("\r\n")
            .append("GE*1*99")
            .append("\r\n")
            .append("IEA*1*000000049")
            .toString();

        List<X12Segment> segmentList = SourceToSegmentUtil.splitSourceDataIntoSegments(sourceData.trim());

        rule = new UniqueDocumentX12Rule("BEG", 3, 4);
        rule.verify(segmentList);
    }

    @Test
    public void test_no_duplicates_no_document_number_or_date() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            // ASN #1
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            // ASN #2
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00**")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("GE*1*99")
            .append("\r\n")
            .append("IEA*1*000000049")
            .toString();

        List<X12Segment> segmentList = SourceToSegmentUtil.splitSourceDataIntoSegments(sourceData.trim());

        rule = new UniqueDocumentX12Rule("BSN", 2, 3);
        rule.verify(segmentList);
    }

    @Test(expected = X12ParserException.class)
    public void test_duplicates_no_document_number() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            // ASN #1
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00**20201022")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            // ASN #2
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00**20201022")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("GE*1*99")
            .append("\r\n")
            .append("IEA*1*000000049")
            .toString();

        List<X12Segment> segmentList = SourceToSegmentUtil.splitSourceDataIntoSegments(sourceData.trim());

        rule = new UniqueDocumentX12Rule("BSN", 2, 3);
        rule.verify(segmentList);
    }

    @Test
    public void test_no_duplicates_no_document_date() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            // ASN #1
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            // ASN #2
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804191*")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("GE*1*99")
            .append("\r\n")
            .append("IEA*1*000000049")
            .toString();

        List<X12Segment> segmentList = SourceToSegmentUtil.splitSourceDataIntoSegments(sourceData.trim());

        rule = new UniqueDocumentX12Rule("BSN", 2, 3);
        rule.verify(segmentList);
    }

    @Test(expected = X12ParserException.class)
    public void test_duplicates_no_document_date() {
        String sourceData = new StringBuilder()
            .append("ISA*01*0000000000*01*0000000000*ZZ*ABCDEFGHIJKLMNO*ZZ*123456789012345*101127*1719*U*00400*000000049*0*P*>")
            .append("\r\n")
            .append("GS*SH*4405197800*999999999*20111206*1045*99*X*004060")
            .append("\r\n")
            // ASN #1
            .append("ST*856*0001")
            .append("\r\n")
            .append("BSN*00*804190*")
            .append("\r\n")
            .append("SE*1*0001")
            .append("\r\n")
            // ASN #2
            .append("ST*856*0002")
            .append("\r\n")
            .append("BSN*00*804190*")
            .append("\r\n")
            .append("SE*1*0002")
            .append("\r\n")
            .append("GE*1*99")
            .append("\r\n")
            .append("IEA*1*000000049")
            .toString();

        List<X12Segment> segmentList = SourceToSegmentUtil.splitSourceDataIntoSegments(sourceData.trim());

        rule = new UniqueDocumentX12Rule("BSN", 2, 3);
        rule.verify(segmentList);
    }

}
