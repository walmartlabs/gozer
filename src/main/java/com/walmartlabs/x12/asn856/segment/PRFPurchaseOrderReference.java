package com.walmartlabs.x12.asn856.segment;

/**
 * 
 * Purpose: To provide reference to a specific purchase order 
 *
 */
public class PRFPurchaseOrderReference {

    public static final String IDENTIFIER = "PRF";

    // PRF01
    private String purchaseOrderNumber;

    // PRF04
    // Date expressed as CCYYMMDD
    private String date;

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
