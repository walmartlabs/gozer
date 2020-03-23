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

package com.walmartlabs.x12.asn856;

import com.walmartlabs.x12.asn856.segment.MANMarkNumber;
import com.walmartlabs.x12.asn856.segment.PO4ItemPhysicalDetail;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.standard.X12ParsedLoop;

/**
 * Represents the Pack level of information
 * 
 */
public class Pack extends X12ParsedLoop {

    public static final String PACK_LOOP_CODE = "P";
    
    /*
     * PO4: Item Physical Details
     */
    private PO4ItemPhysicalDetail po4;
    /*
     * MAN: Marking
     */
    private MANMarkNumber man;
    
    
    public static boolean isPackLoop(X12Loop loop) {
        return X12Loop.isLoopWithCode(loop, PACK_LOOP_CODE);
    }


    public PO4ItemPhysicalDetail getPo4() {
        return po4;
    }


    public void setPo4(PO4ItemPhysicalDetail po4) {
        this.po4 = po4;
    }


    public MANMarkNumber getMan() {
        return man;
    }


    public void setMan(MANMarkNumber man) {
        this.man = man;
    }
    
    
}
