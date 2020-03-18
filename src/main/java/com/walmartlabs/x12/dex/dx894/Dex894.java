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

import com.walmartlabs.x12.X12Document;

import java.util.ArrayList;
import java.util.List;

public class Dex894 implements X12Document {
    // DEX Header Data Elements
    // DXS01
    private String senderCommId;
    // DXS02
    private String functionalId;
    // DXS03
    private String version;
    private Integer versionNumber;
    // DXS04
    private String headerTransmissionControlNumber;
    // DXS05
    private String receiverCommId;
    // DXS06
    private String testIndicator;

    // DEX Transactions
    private List<Dex894TransactionSet> transactions;

    // DEX Trailer Data Elements
    // DXE01
    private String trailerTransmissionControlNumber;
    // DXE02
    private Integer numberOfTransactions;


    /**
     * add a DEX transaction
     * @param dexTx
     */
    public void addTransaction(Dex894TransactionSet dexTx) {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        transactions.add(dexTx);
    }

    public String getHeaderTransmissionControlNumber() {
        return headerTransmissionControlNumber;
    }

    public void setHeaderTransmissionControlNumber(String headerTransmissionControlNumber) {
        this.headerTransmissionControlNumber = headerTransmissionControlNumber;
    }

    public String getTrailerTransmissionControlNumber() {
        return trailerTransmissionControlNumber;
    }

    public void setTrailerTransmissionControlNumber(String trailerTransmissionControlNumber) {
        this.trailerTransmissionControlNumber = trailerTransmissionControlNumber;
    }

    public String getSenderCommId() {
        return senderCommId;
    }

    public void setSenderCommId(String senderCommId) {
        this.senderCommId = senderCommId;
    }

    public String getFunctionalId() {
        return functionalId;
    }

    public void setFunctionalId(String functionalId) {
        this.functionalId = functionalId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReceiverCommId() {
        return receiverCommId;
    }

    public void setReceiverCommId(String receiverCommId) {
        this.receiverCommId = receiverCommId;
    }

    public String getTestIndicator() {
        return testIndicator;
    }

    public void setTestIndicator(String testIndicator) {
        this.testIndicator = testIndicator;
    }

    public Integer getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public void setNumberOfTransactions(Integer numberOfTransactions) {
        this.numberOfTransactions = numberOfTransactions;
    }

    public List<Dex894TransactionSet> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Dex894TransactionSet> transactions) {
        this.transactions = transactions;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

}
