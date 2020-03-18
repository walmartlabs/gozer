package com.walmartlabs.x12.common.segment;

/**
 * Purpose: To specify the geographic place of the named party
 *
 */
public class N4GeographicLocation {

    public static final String PARTY_GEOGRAPHIC_IDENTIFIER = "N4";

    // N401
    private String cityName;
    // N402
    private String stateOrProvinceCode;
    // N403
    private String postalCode;
    // N404
    private String countryCode;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getStateOrProvinceCode() {
        return stateOrProvinceCode;
    }

    public void setStateOrProvinceCode(String stateOrProvinceCode) {
        this.stateOrProvinceCode = stateOrProvinceCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}
