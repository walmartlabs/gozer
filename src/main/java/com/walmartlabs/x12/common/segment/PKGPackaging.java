/**
Copyright (c) 2018-present, Walmart, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
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
