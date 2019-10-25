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
package com.walmartlabs.x12.types;

/**
 * represents the G8201 - debit/credit flag
 *
 */
public enum InvoiceType {
    C, // CREDIT
    D, // DEBIT
    UNKNOWN;

    /**
     * Convert the debit credit value to an enum
     * @param debitCreditFlag
     * @return
     */
    public static InvoiceType convertDebitCreditFlag(String debitCreditFlag) {
        if (debitCreditFlag == null) {
            return null;
        } else {
            InvoiceType returnEnum = InvoiceType.UNKNOWN;

            try {
                returnEnum = InvoiceType.valueOf(debitCreditFlag);
            } catch (Exception e) {
                // illegal value so returning UNKNOWN
            }

            return returnEnum;
        }
    }
}