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
 * Purpose: To specify the transportation details relative to commodity, weight,
 * and quantityElement
 *
 */
public class TD1CarrierDetail {

    public static final String IDENTIFIER = "TD1";

    // TD101
    private String rawPackagingCode;
    private String packagingCodePartOne;
    private String packagingCodePartTwo;
    // TD102
    private String ladingQuantity;

    // TD106
    private String weightQualifier;
    // TD107
    private String weight;
    // TD108
    private String unitOfMeasure;

    public String getRawPackagingCode() {
        return rawPackagingCode;
    }

    public void setRawPackagingCode(String rawPackagingCode) {
        this.rawPackagingCode = rawPackagingCode;
    }

    public String getPackagingCodePartOne() {
        return packagingCodePartOne;
    }

    public void setPackagingCodePartOne(String packagingCodePartOne) {
        this.packagingCodePartOne = packagingCodePartOne;
    }

    public String getPackagingCodePartTwo() {
        return packagingCodePartTwo;
    }

    public void setPackagingCodePartTwo(String packagingCodePartTwo) {
        this.packagingCodePartTwo = packagingCodePartTwo;
    }

    public String getLadingQuantity() {
        return ladingQuantity;
    }

    public void setLadingQuantity(String ladingQuantity) {
        this.ladingQuantity = ladingQuantity;
    }

    public String getWeightQualifier() {
        return weightQualifier;
    }

    public void setWeightQualifier(String weightQualifier) {
        this.weightQualifier = weightQualifier;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

}
