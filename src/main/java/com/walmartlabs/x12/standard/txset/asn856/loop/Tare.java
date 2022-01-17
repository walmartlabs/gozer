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

package com.walmartlabs.x12.standard.txset.asn856.loop;

import com.walmartlabs.x12.common.segment.PKGPackaging;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.standard.X12ParsedLoop;
import com.walmartlabs.x12.standard.txset.asn856.segment.MANMarkNumber;
import com.walmartlabs.x12.standard.txset.asn856.segment.PALPalletType;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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
    private List<PKGPackaging> pkgList;
    /*
     * MAN: Marking
     */
    private List<MANMarkNumber> manList;


    /**
     * returns true if the loop passed in is a Tare loop
     */
    public static boolean isTareLoop(X12Loop loop) {
        return X12Loop.isLoopWithCode(loop, TARE_LOOP_CODE);
    }

    /**
     * helper method to add MAN
     *
     * @param man
     */
    public void addMANMarkNumber(MANMarkNumber man) {
        if (CollectionUtils.isEmpty(manList)) {
            manList = new ArrayList<>();
        }
        manList.add(man);
    }

    /**
     * helper method to add PKG
     *
     * @param pkg
     */
    public void addPKGPackaging(PKGPackaging pkg) {
        if (CollectionUtils.isEmpty(pkgList)) {
            pkgList = new ArrayList<>();
        }
        pkgList.add(pkg);
    }

    public PALPalletType getPal() {
        return pal;
    }

    public void setPal(PALPalletType pal) {
        this.pal = pal;
    }

    public List<PKGPackaging> getPkgList() {
        return pkgList;
    }

    public void setPkgList(List<PKGPackaging> pkgList) {
        this.pkgList = pkgList;
    }

    public List<MANMarkNumber> getManList() {
        return manList;
    }

    public void setManList(List<MANMarkNumber> manList) {
        this.manList = manList;
    }

}
