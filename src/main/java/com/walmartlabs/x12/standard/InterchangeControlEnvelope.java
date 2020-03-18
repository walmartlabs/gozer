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

/**
 * PURPOSE: To indicate the start of an EDI interchange and assign a control number
 */
public class InterchangeControlEnvelope {
    //
    // Interchange control header
    //
    // ISA01
    private String authorizationInformationQualifier;
    // ISA02
    private String authorizationInformation;
    // ISA03
    private String securityInformationQualifier;
    // ISA04
    private String securityInformation;
    // ISA05
    private String interchangeIdQualifier;
    // ISA06
    private String interchangeSenderId;
    // ISA07
    private String interchangeIdQualifierTwo;
    // ISA08
    private String interchangeReceiverId;
    // ISA09
    private String interchangeDate;
    // ISA10
    private String interchangeTime;
    // ISA11
    private String interchangeControlStandardId;
    // ISA12
    private String interchangeControlVersion;
    // ISA13
    private String interchangeControlNumber;
    // ISA14
    private String acknowledgementRequested;
    // ISA15
    private String usageIndicator;
    // ISA16
    private String elementSeparator;

    //
    // Interchange control trailers
    //
    // ISE02
    private Integer numberOfGroups;
    // ISE02
    private String trailerInterchangeControlNumber;

    public String getAuthorizationInformationQualifier() {
        return authorizationInformationQualifier;
    }

    public void setAuthorizationInformationQualifier(String authorizationInformationQualifier) {
        this.authorizationInformationQualifier = authorizationInformationQualifier;
    }

    public String getAuthorizationInformation() {
        return authorizationInformation;
    }

    public void setAuthorizationInformation(String authorizationInformation) {
        this.authorizationInformation = authorizationInformation;
    }

    public String getSecurityInformationQualifier() {
        return securityInformationQualifier;
    }

    public void setSecurityInformationQualifier(String securityInformationQualifier) {
        this.securityInformationQualifier = securityInformationQualifier;
    }

    public String getSecurityInformation() {
        return securityInformation;
    }

    public void setSecurityInformation(String securityInformation) {
        this.securityInformation = securityInformation;
    }

    public String getInterchangeIdQualifier() {
        return interchangeIdQualifier;
    }

    public void setInterchangeIdQualifier(String interchangeIdQualifier) {
        this.interchangeIdQualifier = interchangeIdQualifier;
    }

    public String getInterchangeSenderId() {
        return interchangeSenderId;
    }

    public void setInterchangeSenderId(String interchangeSenderId) {
        this.interchangeSenderId = interchangeSenderId;
    }

    public String getInterchangeIdQualifierTwo() {
        return interchangeIdQualifierTwo;
    }

    public void setInterchangeIdQualifierTwo(String interchangeIdQualifierTwo) {
        this.interchangeIdQualifierTwo = interchangeIdQualifierTwo;
    }

    public String getInterchangeReceiverId() {
        return interchangeReceiverId;
    }

    public void setInterchangeReceiverId(String interchangeReceiverId) {
        this.interchangeReceiverId = interchangeReceiverId;
    }

    public String getInterchangeDate() {
        return interchangeDate;
    }

    public void setInterchangeDate(String interchangeDate) {
        this.interchangeDate = interchangeDate;
    }

    public String getInterchangeTime() {
        return interchangeTime;
    }

    public void setInterchangeTime(String interchangeTime) {
        this.interchangeTime = interchangeTime;
    }

    public String getInterchangeControlStandardId() {
        return interchangeControlStandardId;
    }

    public void setInterchangeControlStandardId(String interchangeControlStandardId) {
        this.interchangeControlStandardId = interchangeControlStandardId;
    }

    public String getInterchangeControlVersion() {
        return interchangeControlVersion;
    }

    public void setInterchangeControlVersion(String interchangeControlVersion) {
        this.interchangeControlVersion = interchangeControlVersion;
    }

    public String getInterchangeControlNumber() {
        return interchangeControlNumber;
    }

    public void setInterchangeControlNumber(String interchangeControlNumber) {
        this.interchangeControlNumber = interchangeControlNumber;
    }

    public String getAcknowledgementRequested() {
        return acknowledgementRequested;
    }

    public void setAcknowledgementRequested(String acknowledgementRequested) {
        this.acknowledgementRequested = acknowledgementRequested;
    }

    public String getUsageIndicator() {
        return usageIndicator;
    }

    public void setUsageIndicator(String usageIndicator) {
        this.usageIndicator = usageIndicator;
    }

    public String getElementSeparator() {
        return elementSeparator;
    }

    public void setElementSeparator(String elementSeparator) {
        this.elementSeparator = elementSeparator;
    }

    public Integer getNumberOfGroups() {
        return numberOfGroups;
    }

    public void setNumberOfGroups(Integer numberOfGroups) {
        this.numberOfGroups = numberOfGroups;
    }

    public String getTrailerInterchangeControlNumber() {
        return trailerInterchangeControlNumber;
    }

    public void setTrailerInterchangeControlNumber(String trailerInterchangeControlNumber) {
        this.trailerInterchangeControlNumber = trailerInterchangeControlNumber;
    }

}
