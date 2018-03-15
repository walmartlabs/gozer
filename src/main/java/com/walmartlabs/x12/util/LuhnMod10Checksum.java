package com.walmartlabs.x12.util;

public class LuhnMod10Checksum implements Checksum {

    private static final int NINE = 9;
    private static final int TEN = 10;

    @Override
    /**
     * Luhn algorithm (Modulo 10) to generate checksum digit
     * This implementation assumes that there is NO checksum digit
     * included in the number provided
     * https://en.wikipedia.org/wiki/Luhn_algorithm
     * @param number
     * @return the checksum digit
     */
    public String generateChecksumDigit(String number) {
        if (number != null && number.length() > 0) {
            // work from rightmost digit
            StringBuilder sb = new StringBuilder(number);
            String reversedNumber = sb.reverse().toString();
            char[] val = reversedNumber.toCharArray();
            int sum = 0;
            for (int i = 0; i < val.length; i++) {
                int currentVal = Integer.parseInt(String.valueOf(val[i]));
                int calcVal = currentVal;
                if (i % 2 == 0) {
                    // double every other digit
                    // starting with rightmost digit
                    int product = currentVal * 2;
                    if (product > NINE) {
                        calcVal = product - NINE;
                    } else {
                        calcVal = product;
                    }
                }
                sum += calcVal;
            }
            int cd = (sum * NINE) % TEN;
            return Integer.toString(cd);
        } else {
            return null;
        }
    }

}
