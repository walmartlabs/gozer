package com.walmartlabs.x12.common.segment;

/**
 * 
 * Purpose: To specify pertinent dates and times
 *
 */
public class REFReferenceInformation {

    public static final String IDENTIFIER = "REF";

    // REF01
    private String referenceIdentificationQualifier;

    // REF02
    private String referenceIdentification;

    public String getReferenceIdentificationQualifier() {
        return referenceIdentificationQualifier;
    }

    public void setReferenceIdentificationQualifier(String referenceIdentificationQualifier) {
        this.referenceIdentificationQualifier = referenceIdentificationQualifier;
    }

    public String getReferenceIdentification() {
        return referenceIdentification;
    }

    public void setReferenceIdentification(String referenceIdentification) {
        this.referenceIdentification = referenceIdentification;
    }

}
