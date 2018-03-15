package com.walmartlabs.x12.util;

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
                retailNumberPadded = "0" + retailNumberPadded;
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
