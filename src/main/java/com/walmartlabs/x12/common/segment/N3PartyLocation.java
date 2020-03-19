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
 * Purpose: To specify the location of the named party
 *
 */
public class N3PartyLocation {

    public static final String IDENTIFIER = "N3";

    // N301
    private String addressInfoOne;
    // N302
    private String addressInfoTwo;

    public String getAddressInfoOne() {
        return addressInfoOne;
    }

    public void setAddressInfoOne(String addressInfoOne) {
        this.addressInfoOne = addressInfoOne;
    }

    public String getAddressInfoTwo() {
        return addressInfoTwo;
    }

    public void setAddressInfoTwo(String addressInfoTwo) {
        this.addressInfoTwo = addressInfoTwo;
    }

}
