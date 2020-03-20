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

package com.walmartlabs.x12.util;

import com.walmartlabs.x12.exceptions.X12ParserException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ConversionUtil {

    /**
     * convert a String to a BigDecimal using the specified decimal places
     * @param theString
     * @param decimalPlaces
     * @return
     */
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

    /**
     * convert a String to a Integer
     * @param theString
     * @return
     */
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
