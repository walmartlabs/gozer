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
import com.walmartlabs.x12.asn856.segment.PALPalletType;
import com.walmartlabs.x12.common.segment.PKGPackaging;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.standard.X12ParsedLoop;

/**
 * Represents the Tare (Pallet) level of information
 * 
 */
public class Tare extends X12ParsedLoop {

    public static final String TARE_LOOP_CODE = "T";

    /*
     * PAL: Pallet Type
     */
    private PALPalletType pal;
    /*
     * PKG: Packaging
     */
    private PKGPackaging pkg;
    /*
     * MAN: Marking
     */
    private MANMarkNumber man;

    public static boolean isTareLoop(X12Loop loop) {
        return X12Loop.isLoopWithCode(loop, TARE_LOOP_CODE);
    }

    public PALPalletType getPal() {
        return pal;
    }

    public void setPal(PALPalletType pal) {
        this.pal = pal;
    }

    public PKGPackaging getPkg() {
        return pkg;
    }

    public void setPkg(PKGPackaging pkg) {
        this.pkg = pkg;
    }

    public MANMarkNumber getMan() {
        return man;
    }

    public void setMan(MANMarkNumber man) {
        this.man = man;
    }

}
