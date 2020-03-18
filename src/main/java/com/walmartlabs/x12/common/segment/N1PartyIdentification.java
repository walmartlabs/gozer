package com.walmartlabs.x12.common.segment;

/**
 * 
 * Purpose: To identify a party by type of organization, name, and code
 *
 */
public class N1PartyIdentification {

    public static final String PARTY_IDENTIFICATION_IDENTIFIER = "N1";

    // N101
    private String entityIdentifierCode;
    // N102
    private String name;
    // N103
    private String identificationCodeQualifier;
    // N104
    private String identificationCode;

    // N3 Party Identification
    N3PartyLocation n3;
    
    // N4 Party Geographic Identification
    N4GeographicLocation n4;
    
    public String getEntityIdentifierCode() {
        return entityIdentifierCode;
    }

    public void setEntityIdentifierCode(String entityIdentifierCode) {
        this.entityIdentifierCode = entityIdentifierCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public N3PartyLocation getN3() {
        return n3;
    }

    public void setN3(N3PartyLocation n3) {
        this.n3 = n3;
    }

    public N4GeographicLocation getN4() {
        return n4;
    }

    public void setN4(N4GeographicLocation n4) {
        this.n4 = n4;
    }
    
}
