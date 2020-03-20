package com.walmartlabs.x12.asn856.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.asn856.segment.SN1ItemDetail;
import com.walmartlabs.x12.util.ConversionUtil;

public class SN1ItemDetailParser {

    /**
     * parse the segment
     * @param segment
     * @return
     */
    public static SN1ItemDetail parse(X12Segment segment) {
        SN1ItemDetail sn1 = null;

        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (SN1ItemDetail.IDENTIFIER.equals(segmentIdentifier)) {
                sn1 = new SN1ItemDetail();
                sn1.setNumberOfUnits(ConversionUtil.convertStringToBigDecimal(segment.getElement(2), 6));
                sn1.setUnitOfMeasurement(segment.getElement(3));
            }
        }
        return sn1;
    }

    private SN1ItemDetailParser() {
        // you can't make me
    }
}
