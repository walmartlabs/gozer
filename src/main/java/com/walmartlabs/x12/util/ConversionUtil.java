package com.walmartlabs.x12.util;

import com.walmartlabs.x12.exceptions.X12ParserException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ConversionUtil {

    public static BigDecimal convertStringToBigDecimal(String theString, int decimalPlaces) {
        BigDecimal returnValue = null;
        try {
            if (theString != null && theString.trim().length() > 0) {
                returnValue = new BigDecimal(theString).setScale(decimalPlaces, RoundingMode.HALF_UP);
            }
        } catch (NumberFormatException e) {
            throw new X12ParserException("Invalid numeric value");
        }
        return returnValue;
    }

    public static Integer convertStringToInteger(String theString) {
        Integer returnInteger = null;

        try {
            if (theString != null && theString.trim().length() > 0) {
                returnInteger = Integer.valueOf(theString);
            }
        } catch (NumberFormatException e) {
            throw new X12ParserException("Invalid numeric value");
        }

        return returnInteger;
    }

    private ConversionUtil() {
    }
}
