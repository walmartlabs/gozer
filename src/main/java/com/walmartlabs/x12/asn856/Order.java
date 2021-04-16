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

import com.walmartlabs.x12.asn856.segment.PRFPurchaseOrderReference;
import com.walmartlabs.x12.common.segment.REFReferenceInformation;
import com.walmartlabs.x12.common.segment.TD1CarrierDetail;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.standard.X12ParsedLoop;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Order level of information
 * 
 */
public class Order extends X12ParsedLoop {

    public static final String ORDER_LOOP_CODE = "O";

    /*
     * PRF: Purchase Order Ref
     */
    private PRFPurchaseOrderReference prf;

    /*
     * TD1: Carrier Details
     */
    private TD1CarrierDetail td1;

    /*
     * REF
     */
    private List<REFReferenceInformation> refList;

    public static boolean isOrderLoop(X12Loop loop) {
        return X12Loop.isLoopWithCode(loop, ORDER_LOOP_CODE);
    }

    /**
     * helper method to add REF
     * 
     * @param ref
     */
    public void addReferenceInformation(REFReferenceInformation ref) {
        if (CollectionUtils.isEmpty(refList)) {
            refList = new ArrayList<>();
        }
        refList.add(ref);
    }

    public PRFPurchaseOrderReference getPrf() {
        return prf;
    }

    public void setPrf(PRFPurchaseOrderReference prf) {
        this.prf = prf;
    }

    public List<REFReferenceInformation> getRefList() {
        return refList;
    }

    public void setRefList(List<REFReferenceInformation> refList) {
        this.refList = refList;
    }
    
    public TD1CarrierDetail getTd1() {
        return td1;
    }

    public void setTd1(TD1CarrierDetail td1) {
        this.td1 = td1;
    }

}
