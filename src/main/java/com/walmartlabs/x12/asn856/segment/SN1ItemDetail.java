package com.walmartlabs.x12.asn856.segment;

import java.math.BigDecimal;

/**
 * 
 * Purpose: To specify line-item detail relative to shipment
 *
 */
public class SN1ItemDetail {

    public static final String IDENTIFIER = "SN1";

    // SN102
    private BigDecimal numberOfUnits;

    // SN103
    private String unitOfMeasurement;

    public BigDecimal getNumberOfUnits() {
        return numberOfUnits;
    }

    public void setNumberOfUnits(BigDecimal numberOfUnits) {
        this.numberOfUnits = numberOfUnits;
    }

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }

}
