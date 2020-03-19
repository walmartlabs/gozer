package com.walmartlabs.x12.asn856.segment;

/**
 * 
 * Purpose: To indicate identifying marks and numbers for shipping containers 
 */
public class MANMarkNumber {

    public static final String IDENTIFIER = "MAN";
    
    // MAN01
    private String qualifier;
    
    // MAN02
    private String number;

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
    
    
}
