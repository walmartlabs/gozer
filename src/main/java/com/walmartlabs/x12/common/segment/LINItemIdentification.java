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
package com.walmartlabs.x12.common.segment;

/**
 * 
 * Purpose: To specify basic item identification data
 *
 */
public class LINItemIdentification {

    public static final String IDENTIFIER = "LIN";

    private String productIdQualifier;
    private String productId;

    public String getProductIdQualifier() {
        return productIdQualifier;
    }

    public void setProductIdQualifier(String productIdQualifier) {
        this.productIdQualifier = productIdQualifier;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

}
