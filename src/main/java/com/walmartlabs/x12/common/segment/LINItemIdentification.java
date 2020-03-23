package com.walmartlabs.x12.common.segment;

/**
 * 
 * Purpose: To specify basic item identification data
 *
 */
public class LINItemIdentification {

    public static final String IDENTIFIER = "LIN";

    private String productIdQualifier;
    private String productId;

    public String getProductIdQualifier() {
        return productIdQualifier;
    }

    public void setProductIdQualifier(String productIdQualifier) {
        this.productIdQualifier = productIdQualifier;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

}
