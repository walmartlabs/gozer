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
 * Purpose: To specify the carrier and sequence of routing and provide transit
 * time information
 *
 */
public class TD5CarrierDetail {

    public static final String IDENTIFIER = "TD5";

    // TD501
    private String routingSequenceCode;
    // TD502
    private String identificationCodeQualifier;
    // TD503
    private String identificationCode;
    // TD504
    private String transportationMethodTypeCode;
    // TD505
    private String routingDescription;

    public String getRoutingSequenceCode() {
        return routingSequenceCode;
    }

    public void setRoutingSequenceCode(String routingSequenceCode) {
        this.routingSequenceCode = routingSequenceCode;
    }

    public String getRoutingDescription() {
        return routingDescription;
    }

    public void setRoutingDescription(String routingDescription) {
        this.routingDescription = routingDescription;
    }

    public String getIdentificationCodeQualifier() {
        return identificationCodeQualifier;
    }

    public void setIdentificationCodeQualifier(String identificationCodeQualifier) {
        this.identificationCodeQualifier = identificationCodeQualifier;
    }

    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public String getTransportationMethodTypeCode() {
        return transportationMethodTypeCode;
    }

    public void setTransportationMethodTypeCode(String transportationMethodTypeCode) {
        this.transportationMethodTypeCode = transportationMethodTypeCode;
    }

}
