package com.walmartlabs.x12.dex.dx894;

import java.math.BigDecimal;

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
