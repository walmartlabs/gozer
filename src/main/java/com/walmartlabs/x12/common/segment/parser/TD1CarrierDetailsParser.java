package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.TD1CarrierDetails;
import com.walmartlabs.x12.types.UnitMeasure;
import com.walmartlabs.x12.util.ConversionUtil;
import org.springframework.util.StringUtils;

public final class TD1CarrierDetailsParser {
    
    public static TD1CarrierDetails parse(X12Segment segment) {
        TD1CarrierDetails td1 = null;
        
        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (TD1CarrierDetails.CARRIER_DETAILS_IDENTIFIER.equals(segmentIdentifier)) {
                td1 = new TD1CarrierDetails();
                String packagingCode = segment.getElement(1);
                td1.setRawPackagingCode(packagingCode);
                if (!StringUtils.isEmpty(packagingCode)) {
                    if (packagingCode.length() == 3 || packagingCode.length() == 5) {
                        td1.setPackagingCodePartOne(packagingCode.substring(0, 3));
                        td1.setPackagingCodePartTwo(packagingCode.substring(3));
                    }
                }
                td1.setLadingQuantity(ConversionUtil.convertStringToBigDecimal(segment.getElement(2), 0));
                td1.setWeightQualifier(segment.getElement(6));
                td1.setWeight(ConversionUtil.convertStringToBigDecimal(segment.getElement(7), 4));
                td1.setUnitOfMeasureCode(UnitMeasure.convert(segment.getElement(8)));
             }
        }
        return td1;
    }
    
    private TD1CarrierDetailsParser() {
        // you can't make me
    }
}
