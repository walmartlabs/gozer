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

import com.walmartlabs.x12.checksum.BarCodeMod10Checksum;
import com.walmartlabs.x12.checksum.Checksum;

public class RetailSellingUnitUtil {

    private static final int GTIN_SIZE = 14;
    private static final int UPC_LIMIT = 13;

    Checksum checksumUtil;

    public RetailSellingUnitUtil() {
        this.checksumUtil = new BarCodeMod10Checksum();
    }

    public RetailSellingUnitUtil(Checksum checksumUtil) {
        this.checksumUtil = checksumUtil;
    }

    /**
     * if retail number is < 14 digits then calculate ITF-14
     * -- assume the number does not have a checksum digit
     * if retail number is 14 digits check the checksum digit
     * -- if valid checksum digit return the number
     * -- otherwise return null
     * if retail number is > 14 digits return null
     * @param retailNumber
     * @return ITF-14
     * @throws NumberFormatException
     */
    public String convertRetailNumberToItf14(String retailNumber) {
        String itf14 = null;
        if (retailNumber != null && retailNumber.length() > 0) {
            if (retailNumber.length() < GTIN_SIZE) {
                // assuming there is no checksum on the value
                String paddedRetailNumber = this.padRetailNumber(retailNumber);
                itf14 = paddedRetailNumber + checksumUtil.generateChecksumDigit(paddedRetailNumber);
            } else if (retailNumber.length() == GTIN_SIZE) {
                // assuming there is a checksum
                if (this.verifyChecksumDigit(retailNumber)) {
                    itf14 = retailNumber;
                }
            }
        }
        return itf14;
    }

    /**
     * if < 13 digits, add the digit 0 until length = 13 digits
     * @param retailNumber
     * @return 13 digit retail number padded or the number passed in if >= 13 digits
     */
    protected String padRetailNumber(String retailNumber) {
        if (retailNumber != null && retailNumber.length() > 0) {
            String retailNumberPadded = retailNumber;
            while (retailNumberPadded.length() < UPC_LIMIT) {
                retailNumberPadded = new StringBuilder("0").append(retailNumberPadded).toString();
            }
            return retailNumberPadded;
        } else {
            return null;
        }
    }

    /**
     * verify the checksum digit on a number is correct
     * this method assumes that the number provided has
     * a checksum digit
     * @param number
     * @return true if calculated checksum equals checksum digit
     * @throws NumberFormatException
     */
    protected boolean verifyChecksumDigit(String number) {
        if (number != null && number.length() > 1) {
            String providedChecksumDigit = retrieveChecksumDigit(number);
            String numberWithoutChecksumDigit = number.substring(0, number.length() - 1);
            String generatedChecksumDigit = checksumUtil.generateChecksumDigit(numberWithoutChecksumDigit);
            return providedChecksumDigit.equals(generatedChecksumDigit);
        } else {
            return false;
        }
    }

    /**
     * retrieve the checksum digit
     * this method assumes that the number provided has
     * a checksum digit
     * @param number
     * @return the last digit in the number provided or null
     */
    protected String retrieveChecksumDigit(String number) {
        if (number != null && number.length() > 0) {
            return String.valueOf(number.toCharArray()[number.length() - 1]);
        } else {
            return null;
        }
    }

}
