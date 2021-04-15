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
package com.walmartlabs.x12.asn856.segment;

/**
 * 
 * Purpose: To identify the type and physical attributes of the pallet, and,
 * gross weight, gross volume, and height of the load and the pallet
 */
public class PALPalletType {

    public static final String IDENTIFIER = "PAL";

    // PAL02
    private String palletTiers;
    
    // PAL03
    private String palletBlocks;
    

    public String getPalletTiers() {
        return palletTiers;
    }

    public void setPalletTiers(String palletTiers) {
        this.palletTiers = palletTiers;
    }

    public String getPalletBlocks() {
        return palletBlocks;
    }

    public void setPalletBlocks(String palletBlocks) {
        this.palletBlocks = palletBlocks;
    }
    
    
}
