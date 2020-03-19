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
