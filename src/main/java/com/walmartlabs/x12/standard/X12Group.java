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
package com.walmartlabs.x12.standard;

import com.walmartlabs.x12.X12TransactionSet;

import java.util.ArrayList;
import java.util.List;

public class X12Group {

    // Header Data Elements
    // GS01
    private String functionalCodeId;
    // GS02
    private String applicationSenderCode;
    // GS03
    private String applicationReceiverCode;
    // GS04
    private String date;
    // GS05
    private String time;
    // GS06
    private String headerGroupControlNumber;
    // GS07
    private String responsibleAgencyCode;
    // GS08
    private String version;

    // ST...SE loops within the group
    private List<X12TransactionSet> transactions;

    // Trailer Data Elements
    // GE01
    private Integer numberOfTransactions;
    // GE02
    private String trailerGroupControlNumber;

    public void addTransactionSet(X12TransactionSet tx) {
        if (transactions == null) {
            transactions = new ArrayList<>();
        }
        transactions.add(tx);
    }

    public String getFunctionalCodeId() {
        return functionalCodeId;
    }

    public void setFunctionalCodeId(String functionalCodeId) {
        this.functionalCodeId = functionalCodeId;
    }

    public String getApplicationSenderCode() {
        return applicationSenderCode;
    }

    public void setApplicationSenderCode(String applicationSenderCode) {
        this.applicationSenderCode = applicationSenderCode;
    }

    public String getApplicationReceiverCode() {
        return applicationReceiverCode;
    }

    public void setApplicationReceiverCode(String applicationReceiverCode) {
        this.applicationReceiverCode = applicationReceiverCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHeaderGroupControlNumber() {
        return headerGroupControlNumber;
    }

    public void setHeaderGroupControlNumber(String headerGroupControlNumber) {
        this.headerGroupControlNumber = headerGroupControlNumber;
    }

    public String getResponsibleAgencyCode() {
        return responsibleAgencyCode;
    }

    public void setResponsibleAgencyCode(String responsibleAgencyCode) {
        this.responsibleAgencyCode = responsibleAgencyCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<X12TransactionSet> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<X12TransactionSet> transactions) {
        this.transactions = transactions;
    }

    public Integer getNumberOfTransactions() {
        return numberOfTransactions;
    }

    public void setNumberOfTransactions(Integer numberOfTransactions) {
        this.numberOfTransactions = numberOfTransactions;
    }

    public String getTrailerGroupControlNumber() {
        return trailerGroupControlNumber;
    }

    public void setTrailerGroupControlNumber(String trailerGroupControlNumber) {
        this.trailerGroupControlNumber = trailerGroupControlNumber;
    }

}
