package com.walmartlabs.x12.common.segment;

/**
 * 
 * Purpose: To specify the carrier and sequence of routing and provide transit
 * time information
 *
 */
public class TD5CarrierDetails {

    public static final String CARRIER_DETAILS_IDENTIFIER = "TD5";

    // TD501
    private String routingSequenceCode;
    // TD502
    private String identificationCodeQualifier;
    // TD503
    private String identificationCode;
    // TD504
    private String transportationMethodTypeCode;
    // TD505
    private String routingDescription;

    public String getRoutingSequenceCode() {
        return routingSequenceCode;
    }

    public void setRoutingSequenceCode(String routingSequenceCode) {
        this.routingSequenceCode = routingSequenceCode;
    }

    public String getRoutingDescription() {
        return routingDescription;
    }

    public void setRoutingDescription(String routingDescription) {
        this.routingDescription = routingDescription;
    }

    public String getIdentificationCodeQualifier() {
        return identificationCodeQualifier;
    }

    public void setIdentificationCodeQualifier(String identificationCodeQualifier) {
        this.identificationCodeQualifier = identificationCodeQualifier;
    }

    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public String getTransportationMethodTypeCode() {
        return transportationMethodTypeCode;
    }

    public void setTransportationMethodTypeCode(String transportationMethodTypeCode) {
        this.transportationMethodTypeCode = transportationMethodTypeCode;
    }

}
