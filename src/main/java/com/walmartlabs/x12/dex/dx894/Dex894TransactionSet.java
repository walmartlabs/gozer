package com.walmartlabs.x12.dex.dx894;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The 894 Base Record Transaction Set is essentially an invoice.
 * Note: Debits and credits cannot mix on the same invoice
 */
public class Dex894TransactionSet {
    /*
     * ST
     */
    // ST01
    private String transactionSetIdentifierCode;
    // ST02
    private String headerControlNumber;

    /*
     * SE
     */
    // SE01
    private Integer expectedNumberOfSegments;
    // SE02
    private String trailerControlNumber;
    // used for validation routines
    // to cmp against expected segments
    private Integer actualNumberOfSegments;

    /*
     * G82
     */
    // G8201
    private String debitCreditFlag;
    // G8202
    private String supplierNumber;
    // G8203
    private String receiverDuns;
    // G8204
    private String receiverLocation;
    // G8205
    private String supplierDuns;
    // G8206
    private String supplierLocation;
    // G8207
    private String transactionDate;
    // G8209
    private String purchaseOrderNumber;
    // G8210
    private String purchaseOrderDate;

    /*
     * G83 items
     */
    private List<Dex894Item> items;

    /*
     * G84
     */
    // G8401
    private BigDecimal transactionTotalQuantity;
    // G8402
    private BigDecimal transactionTotalAmount;
    // G8403
    private BigDecimal transactionTotalDepositAmount;


    /*
     * G85
     */
    // G8501
    private String integrityCheckValue;

    /*
     * G86
     */
    // G8601
    private String electronicSignature;
    // G8602
    private String signatureName;

    /**
     * add a DEX item
     * @param dexItem
     */
    public void addItem(Dex894Item dexItem) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(dexItem);
    }

    public String getTransactionSetIdentifierCode() {
        return transactionSetIdentifierCode;
    }

    public void setTransactionSetIdentifierCode(String transactionSetIdentifierCode) {
        this.transactionSetIdentifierCode = transactionSetIdentifierCode;
    }

    public String getHeaderControlNumber() {
        return headerControlNumber;
    }

    public void setHeaderControlNumber(String headerControlNumber) {
        this.headerControlNumber = headerControlNumber;
    }

    public String getTrailerControlNumber() {
        return trailerControlNumber;
    }

    public void setTrailerControlNumber(String trailerControlNumber) {
        this.trailerControlNumber = trailerControlNumber;
    }

    public String getSupplierNumber() {
        return supplierNumber;
    }

    public void setSupplierNumber(String supplierNumber) {
        this.supplierNumber = supplierNumber;
    }

    public String getDebitCreditFlag() {
        return debitCreditFlag;
    }

    public void setDebitCreditFlag(String debitCreditFlag) {
        this.debitCreditFlag = debitCreditFlag;
    }

    public String getReceiverDuns() {
        return receiverDuns;
    }

    public void setReceiverDuns(String receiverDuns) {
        this.receiverDuns = receiverDuns;
    }

    public String getReceiverLocation() {
        return receiverLocation;
    }

    public void setReceiverLocation(String receiverLocation) {
        this.receiverLocation = receiverLocation;
    }

    public String getSupplierDuns() {
        return supplierDuns;
    }

    public void setSupplierDuns(String supplierDuns) {
        this.supplierDuns = supplierDuns;
    }

    public String getSupplierLocation() {
        return supplierLocation;
    }

    public void setSupplierLocation(String supplierLocation) {
        this.supplierLocation = supplierLocation;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    public String getPurchaseOrderDate() {
        return purchaseOrderDate;
    }

    public void setPurchaseOrderDate(String purchaseOrderDate) {
        this.purchaseOrderDate = purchaseOrderDate;
    }

    public List<Dex894Item> getItems() {
        return items;
    }

    public void setItems(List<Dex894Item> items) {
        this.items = items;
    }

    public String getIntegrityCheckValue() {
        return integrityCheckValue;
    }

    public void setIntegrityCheckValue(String integrityCheckValue) {
        this.integrityCheckValue = integrityCheckValue;
    }

    public String getElectronicSignature() {
        return electronicSignature;
    }

    public void setElectronicSignature(String electronicSignature) {
        this.electronicSignature = electronicSignature;
    }

    public String getSignatureName() {
        return signatureName;
    }

    public void setSignatureName(String signatureName) {
        this.signatureName = signatureName;
    }

    public Integer getExpectedNumberOfSegments() {
        return expectedNumberOfSegments;
    }

    public void setExpectedNumberOfSegments(Integer expectedNumberOfSegments) {
        this.expectedNumberOfSegments = expectedNumberOfSegments;
    }

    public Integer getActualNumberOfSegments() {
        return actualNumberOfSegments;
    }

    public void setActualNumberOfSegments(Integer actualNumberOfSegments) {
        this.actualNumberOfSegments = actualNumberOfSegments;
    }

    public BigDecimal getTransactionTotalQuantity() {
        return transactionTotalQuantity;
    }

    public void setTransactionTotalQuantity(BigDecimal transactionTotalQuantity) {
        this.transactionTotalQuantity = transactionTotalQuantity;
    }

    public BigDecimal getTransactionTotalAmount() {
        return transactionTotalAmount;
    }

    public void setTransactionTotalAmount(BigDecimal transactionTotalAmount) {
        this.transactionTotalAmount = transactionTotalAmount;
    }

    public BigDecimal getTransactionTotalDepositAmount() {
        return transactionTotalDepositAmount;
    }

    public void setTransactionTotalDepositAmount(BigDecimal transactionTotalDepositAmount) {
        this.transactionTotalDepositAmount = transactionTotalDepositAmount;
    }

}
