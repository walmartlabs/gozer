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
 * Weight Qualifier
 *
 */
public enum WeightQualifier {
    G("GROSS WEIGHT"),
    N("ACTUAL_NET_WEIGHT"),
    UNKNOWN("");

    private String description;
    
    private WeightQualifier(String desc) {
        this.description = desc;
    }
    
    private void setDescription(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return this.description;
    }
    
    /**
     * Convert the code to an enum
     * @param code
     * @return
     */
    public static WeightQualifier convert(String code) {
        if (code == null) {
            return null;
        } else {
            WeightQualifier returnEnum = WeightQualifier.UNKNOWN;
            returnEnum.setDescription(code);

            try {
                returnEnum = WeightQualifier.valueOf(code);
            } catch (Exception e) {
                // illegal value so returning UNKNOWN
            }

            return returnEnum;
        }
    }
}