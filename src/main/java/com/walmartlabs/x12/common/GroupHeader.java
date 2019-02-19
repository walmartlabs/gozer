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
package com.walmartlabs.x12.common;

public class GroupHeader {
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
    private String groupControlNumber;
    // GS07
    private String responsibeAgencyCode;
    // GS08
    private String version;

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

    public String getGroupControlNumber() {
        return groupControlNumber;
    }

    public void setGroupControlNumber(String groupControlNumber) {
        this.groupControlNumber = groupControlNumber;
    }

    public String getResponsibeAgencyCode() {
        return responsibeAgencyCode;
    }

    public void setResponsibeAgencyCode(String responsibeAgencyCode) {
        this.responsibeAgencyCode = responsibeAgencyCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
