package com.walmartlabs.x12.common.segment;

/**
 * 
 * Purpose: To describe marking, packaging, loading, and unloading requirements
 *
 */
public class PKGPackaging {

    public static final String IDENTIFIER = "PKG";

    // PKG01
    private String itemDescriptionType;

    // PKG02
    private String packagingCharacteristicCode;

    // PKG03
    private String agencyQualifierCode;

    // PKG04
    private String packagingDescriptionCode;

    public String getItemDescriptionType() {
        return itemDescriptionType;
    }

    public void setItemDescriptionType(String itemDescriptionType) {
        this.itemDescriptionType = itemDescriptionType;
    }

    public String getPackagingCharacteristicCode() {
        return packagingCharacteristicCode;
    }

    public void setPackagingCharacteristicCode(String packagingCharacteristicCode) {
        this.packagingCharacteristicCode = packagingCharacteristicCode;
    }

    public String getAgencyQualifierCode() {
        return agencyQualifierCode;
    }

    public void setAgencyQualifierCode(String agencyQualifierCode) {
        this.agencyQualifierCode = agencyQualifierCode;
    }

    public String getPackagingDescriptionCode() {
        return packagingDescriptionCode;
    }

    public void setPackagingDescriptionCode(String packagingDescriptionCode) {
        this.packagingDescriptionCode = packagingDescriptionCode;
    }

}
