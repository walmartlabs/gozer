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
package com.walmartlabs.x12.dex.dx894;

/**
 * represents the G8305 - product/service id qualifier values
 *
 */
public enum ProductQualifier {
    DI("DEPOSIT ITEM NUMBER"),
    EN("EAN/UCC-13"),
    EO("EAN/UCC-8"),
    NR("NONRESALABLE ITEM"),
    UK("EAN/UCC-14"),
    UP("EAN/UCC-12"),
    VN("VENDOR ITEM NUMBER"),
    UNKNOWN("UNKNOWN");

    private String description;

    private ProductQualifier(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * Convert the unit of measure value to an enum
     * @param productQualifierCode
     * @return
     */
    public static ProductQualifier convertyProductQualifier(String productQualifierCode) {
        if (productQualifierCode == null) {
            return null;
        } else {
            ProductQualifier returnEnum = ProductQualifier.UNKNOWN;

            try {
                returnEnum = ProductQualifier.valueOf(productQualifierCode);
            } catch (Exception e) {
                // illegal value so returning UNKNOWN
            }

            return returnEnum;
        }
    }

}
