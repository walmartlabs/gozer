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

import com.walmartlabs.x12.AbstractX12TransactionSet;
import com.walmartlabs.x12.types.InvoiceType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * The 894 Base Record Transaction Set is essentially an invoice.
 * Note: Debits and credits cannot mix on the same invoice
 */
public class Dex894TransactionSet extends AbstractX12TransactionSet {

    // used for validation routines
    // to cmp against expected segments
    private Integer actualNumberOfSegments;

    /*
     * G82
     */
    // G8201
    private InvoiceType debitCreditFlag;
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
    private String transactionData;

    /*
     * G86
     */
    // G8601
    private String electronicSignature;
    // G8602
    private String signatureName;

    /**
     * add a DEX item
     *
     * @param dexItem
     */
    public void addItem(Dex894Item dexItem) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(dexItem);
    }

    public String getSupplierNumber() {
        return supplierNumber;
    }

    public void setSupplierNumber(String supplierNumber) {
        this.supplierNumber = supplierNumber;
    }

    public InvoiceType getDebitCreditFlag() {
        return debitCreditFlag;
    }

    public void setDebitCreditFlag(InvoiceType debitCreditFlag) {
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

    public String getTransactionData() {
        return transactionData;
    }

    public void setTransactionData(String transactionData) {
        this.transactionData = transactionData;
    }
}
