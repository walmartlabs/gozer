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
 * Purpose: To describe a product or process in coded or free-form format
 *
 */
public class PIDProductIdentification {

    public static final String IDENTIFIER = "PID";

    // PID01
    private String itemDescriptionType;

    // PID02
    private String characteristicCode;

    // PID05
    private String description;

    public String getItemDescriptionType() {
        return itemDescriptionType;
    }

    public void setItemDescriptionType(String itemDescriptionType) {
        this.itemDescriptionType = itemDescriptionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCharacteristicCode() {
        return characteristicCode;
    }

    public void setCharacteristicCode(String characteristicCode) {
        this.characteristicCode = characteristicCode;
    }

}
