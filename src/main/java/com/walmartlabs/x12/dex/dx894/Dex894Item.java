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

import com.walmartlabs.x12.types.ProductQualifier;
import com.walmartlabs.x12.types.UnitMeasure;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The 894 Base Record Transaction Set is comprised of items
 */
public class Dex894Item {
    /*
     * G83
     */
    // G8301: DSD Sequence Number
    private String itemSequenceNumber;
    // G8302: Quantity
    private BigDecimal quantity;
    // G8303: UOM
    private UnitMeasure uom;
    // G8304: 12 digit UPC
    private String upc;
    // G8305: Product Qualifier for G8306
    private ProductQualifier consumerProductQualifier;
    // G8306: Product Id
    private String consumerProductId;
    // G8307: 12 digit UPC Case Code
    private String caseUpc;
    // G8308: Item List Cost
    private BigDecimal itemListCost;
    // G8309: Pack
    private Integer packCount;
    // G8310: Cash Register Item Description
    private String itemDescription;
    // G8311: Product Qualifier for G8312
    private ProductQualifier caseProductQualifier;
    // G8312: Product Id
    private String caseProductId;
    // G8313: inner pack count
    private Integer innerPackCount;

    // G72: Allowance
    private List<Dex894Allowance> allowances;


    /**
     * add a DEX Allowance
     *
     * @param dexAllowance
     */
    public void addAllowance(Dex894Allowance dexAllowance) {
        if (allowances == null) {
            allowances = new ArrayList<>();
        }
        allowances.add(dexAllowance);
    }

    public String getItemSequenceNumber() {
        return itemSequenceNumber;
    }

    public void setItemSequenceNumber(String itemSequenceNumber) {
        this.itemSequenceNumber = itemSequenceNumber;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public UnitMeasure getUom() {
        return uom;
    }

    public void setUom(UnitMeasure uom) {
        this.uom = uom;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public ProductQualifier getConsumerProductQualifier() {
        return consumerProductQualifier;
    }

    public void setConsumerProductQualifier(ProductQualifier consumerProductQualifier) {
        this.consumerProductQualifier = consumerProductQualifier;
    }

    public String getConsumerProductId() {
        return consumerProductId;
    }

    public void setConsumerProductId(String consumerProductId) {
        this.consumerProductId = consumerProductId;
    }

    public String getCaseUpc() {
        return caseUpc;
    }

    public void setCaseUpc(String caseUpc) {
        this.caseUpc = caseUpc;
    }

    public BigDecimal getItemListCost() {
        return itemListCost;
    }

    public void setItemListCost(BigDecimal itemListCost) {
        this.itemListCost = itemListCost;
    }

    public Integer getPackCount() {
        return packCount;
    }

    public void setPackCount(Integer packCount) {
        this.packCount = packCount;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public ProductQualifier getCaseProductQualifier() {
        return caseProductQualifier;
    }

    public void setCaseProductQualifier(ProductQualifier caseProductQualifier) {
        this.caseProductQualifier = caseProductQualifier;
    }

    public String getCaseProductId() {
        return caseProductId;
    }

    public void setCaseProductId(String caseProductId) {
        this.caseProductId = caseProductId;
    }

    public Integer getInnerPackCount() {
        return innerPackCount;
    }

    public void setInnerPackCount(Integer innerPackCount) {
        this.innerPackCount = innerPackCount;
    }

    public List<Dex894Allowance> getAllowances() {
        return allowances;
    }

    public void setAllowances(List<Dex894Allowance> allowances) {
        this.allowances = allowances;
    }

}
