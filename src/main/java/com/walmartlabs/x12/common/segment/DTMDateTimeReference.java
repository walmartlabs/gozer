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
 * Purpose: To specify pertinent dates and times
 *
 */
public class DTMDateTimeReference {

    public static final String IDENTIFIER = "DTM";

    // DTM01
    private String dateTimeQualifier;

    // DTM02
    // Date expressed as CCYYMMDD
    private String date;

    // DTM03
    // Time expressed in 24-hour clock time as follows:
    // HHMM, or HHMMSS, or HHMMSSD, or HHMMSSDD
    private String time;

    public String getDateTimeQualifier() {
        return dateTimeQualifier;
    }

    public void setDateTimeQualifier(String dateTimeQualifier) {
        this.dateTimeQualifier = dateTimeQualifier;
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

}
