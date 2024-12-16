package com.walmartlabs.x12.common.segment;

/**
 * Purpose: To identify a person or office to whom administrative communications should be directed
 */
public class PERAdministrativeCommunication {

    public static final String IDENTIFIER = "PER";

    // PER01, Code identifying the major duty or responsibility of the person or group named
    private String contactFunctionCode;

    // PER02, Free-form name
    private String freeFormName;

    // PER03/05/07, Code identifying the type of communication number
    private String communicationNumberQualifier;

    // PER04/06/08, Complete communications number including country or area code when applicable
    private String communicationNumber;


    public String getContactFunctionCode() {
        return contactFunctionCode;
    }

    public void setContactFunctionCode(String contactFunctionCode) {
        this.contactFunctionCode = contactFunctionCode;
    }

    public String getFreeFormName() {
        return freeFormName;
    }

    public void setFreeFormName(String freeFormName) {
        this.freeFormName = freeFormName;
    }

    public String getCommunicationNumberQualifier() {
        return communicationNumberQualifier;
    }

    public void setCommunicationNumberQualifier(String communicationNumberQualifier) {
        this.communicationNumberQualifier = communicationNumberQualifier;
    }

    public String getCommunicationNumber() {
        return communicationNumber;
    }

    public void setCommunicationNumber(String communicationNumber) {
        this.communicationNumber = communicationNumber;
    }
}
