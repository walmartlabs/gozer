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

package com.walmartlabs.x12.util.checksum;

public class LuhnMod10Checksum implements Checksum {

    private static final int NINE = 9;
    private static final int TEN = 10;

    /**
     * Luhn algorithm (Modulo 10) to generate checksum digit
     * This implementation assumes that there is NO checksum digit
     * included in the number provided
     * https://en.wikipedia.org/wiki/Luhn_algorithm
     * 
     * @param documentAsBytes
     * @return the checksum value or null if no input is given
     */
    @Override
    public String generateChecksumDigit(byte[] documentAsBytes) {
        if (documentAsBytes != null) {
            return this.generateCheckSumUsingLuhnMod10(new String(documentAsBytes));
        } else {
            return null;
        }
    }
    
    @Override
    public String generateChecksumDigit(String documentAsString) {
        return this.generateCheckSumUsingLuhnMod10(documentAsString);
    }
    
    private String generateCheckSumUsingLuhnMod10(String inputValue) {
        if (inputValue != null && inputValue.length() > 0) {
            // work from rightmost digit
            StringBuilder sb = new StringBuilder(inputValue);
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
