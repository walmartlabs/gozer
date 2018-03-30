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

public class BarCodeMod10Checksum implements Checksum {

    private static final int THREE = 3;
    private static final int TEN = 10;

    /**
     * Yet another Modulo 10 to generate checksum digit
     * This implementation assumes that there is NO checksum digit
     * included in the number provided
     * https://www.activebarcode.com/codes/checkdigit/modulo10.html
     * https://www.idautomation.com/barcode-faq/upc-ean/#MOD_10
     * @param number
     * @return
     */
    @Override
    public String generateChecksumDigit(String number) {
        if (number != null && number.length() > 0) {
            // work from rightmost digit
            StringBuilder sb = new StringBuilder(number);
            String reversedNumber = sb.reverse().toString();
            char[] val = reversedNumber.toCharArray();
            int even = 0;
            int odd = 0;
            for (int i = 0; i < val.length; i++) {
                int currentVal = Integer.parseInt(String.valueOf(val[i]));
                if (i % 2 == 0) {
                    even = currentVal + even;
                } else {
                    odd = currentVal + odd;
                }
            }
            int cd = ((even * THREE) + odd) % TEN;
            if (cd != 0) {
                cd = TEN - cd;
            }

            return Integer.toString(cd);
        } else {
            return null;
        }
    }

}
