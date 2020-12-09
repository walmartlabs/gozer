package com.walmartlabs.x12.common.segment;

/**
 * 
 * Purpose: To specify transportation instructions related to shipment payment terms
 */
public class FOBRelatedInstructions {

    public static final String IDENTIFIER = "FOB";

    // FOB01
    private String paymentCode;

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

}
