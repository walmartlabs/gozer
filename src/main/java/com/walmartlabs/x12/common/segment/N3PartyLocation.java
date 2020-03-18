package com.walmartlabs.x12.common.segment;

/**
 * Purpose: To specify the location of the named party
 *
 */
public class N3PartyLocation {

    public static final String PARTY_LOCATION_IDENTIFIER = "N3";

    // N301
    private String addressInfoOne;
    // N302
    private String addressInfoTwo;

    public String getAddressInfoOne() {
        return addressInfoOne;
    }

    public void setAddressInfoOne(String addressInfoOne) {
        this.addressInfoOne = addressInfoOne;
    }

    public String getAddressInfoTwo() {
        return addressInfoTwo;
    }

    public void setAddressInfoTwo(String addressInfoTwo) {
        this.addressInfoTwo = addressInfoTwo;
    }

}
