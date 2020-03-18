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
 * Purpose: To specify transportation details relating to the equipment used by
 * the carrierLoop
 *
 */
public class TD3CarrierDetails {

    public static final String CARRIER_DETAILS_IDENTIFIER = "TD3";

    // TD301
    private String equipmentDescriptionCode;
    // TD302
    private String equipmentInitial;
    // TD303
    private String equipmentNumber;

    // TD309
    private String sealNumber;

    public String getEquipmentDescriptionCode() {
        return equipmentDescriptionCode;
    }

    public void setEquipmentDescriptionCode(String equipmentDescriptionCode) {
        this.equipmentDescriptionCode = equipmentDescriptionCode;
    }

    public String getEquipmentInitial() {
        return equipmentInitial;
    }

    public void setEquipmentInitial(String equipmentInitial) {
        this.equipmentInitial = equipmentInitial;
    }

    public String getEquipmentNumber() {
        return equipmentNumber;
    }

    public void setEquipmentNumber(String equipmentNumber) {
        this.equipmentNumber = equipmentNumber;
    }

    public String getSealNumber() {
        return sealNumber;
    }

    public void setSealNumber(String sealNumber) {
        this.sealNumber = sealNumber;
    }

}
