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
package com.walmartlabs.x12.dex.dx894;

import com.walmartlabs.x12.types.UnitMeasure;

import java.math.BigDecimal;

public class Dex894Allowance {

    /*
     * G72
     */
    // G7201: Allowance or Charge Code
    private String allowanceCode;
    // G7202: Method of Handling Code
    private String methodOfHandlingCode;
    // G7203: Allowance or Charge number
    private String allowanceNumber;
    // G7204: exception number
    private String exceptionNumber;
    // G7205: allowance or charge rate
    private BigDecimal allowanceRate;
    // G7206: allowance quantity
    private BigDecimal allowanceQuantity;
    // G7207: UOM
    private UnitMeasure allowanceUom;
    // G7208: Allowance Amount
    private BigDecimal allowanceAmount;
    // G7209: Allowance percent
    private BigDecimal allowancePercent;
    // G7210: Dollar basis for percent
    private BigDecimal dollarBasis;
    // G7211: option number
    private String optionNumber;


    public String getAllowanceCode() {
        return allowanceCode;
    }

    public void setAllowanceCode(String allowanceCode) {
        this.allowanceCode = allowanceCode;
    }

    public String getMethodOfHandlingCode() {
        return methodOfHandlingCode;
    }

    public void setMethodOfHandlingCode(String methodOfHandlingCode) {
        this.methodOfHandlingCode = methodOfHandlingCode;
    }

    public String getAllowanceNumber() {
        return allowanceNumber;
    }

    public void setAllowanceNumber(String allowanceNumber) {
        this.allowanceNumber = allowanceNumber;
    }

    public String getExceptionNumber() {
        return exceptionNumber;
    }

    public void setExceptionNumber(String exceptionNumber) {
        this.exceptionNumber = exceptionNumber;
    }

    public BigDecimal getAllowanceRate() {
        return allowanceRate;
    }

    public void setAllowanceRate(BigDecimal allowanceRate) {
        this.allowanceRate = allowanceRate;
    }

    public BigDecimal getAllowanceQuantity() {
        return allowanceQuantity;
    }

    public void setAllowanceQuantity(BigDecimal allowanceQuantity) {
        this.allowanceQuantity = allowanceQuantity;
    }

    public UnitMeasure getAllowanceUom() {
        return allowanceUom;
    }

    public void setAllowanceUom(UnitMeasure allowanceUom) {
        this.allowanceUom = allowanceUom;
    }

    public BigDecimal getAllowanceAmount() {
        return allowanceAmount;
    }

    public void setAllowanceAmount(BigDecimal allowanceAmount) {
        this.allowanceAmount = allowanceAmount;
    }

    public BigDecimal getAllowancePercent() {
        return allowancePercent;
    }

    public void setAllowancePercent(BigDecimal allowancePercent) {
        this.allowancePercent = allowancePercent;
    }

    public BigDecimal getDollarBasis() {
        return dollarBasis;
    }

    public void setDollarBasis(BigDecimal dollarBasis) {
        this.dollarBasis = dollarBasis;
    }

    public String getOptionNumber() {
        return optionNumber;
    }

    public void setOptionNumber(String optionNumber) {
        this.optionNumber = optionNumber;
    }

}
