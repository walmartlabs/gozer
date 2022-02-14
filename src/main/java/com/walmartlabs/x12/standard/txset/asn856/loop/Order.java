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

import com.walmartlabs.x12.common.segment.N1PartyIdentification;
import com.walmartlabs.x12.common.segment.REFReferenceInformation;
import com.walmartlabs.x12.common.segment.TD1CarrierDetail;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.standard.X12ParsedLoop;
import com.walmartlabs.x12.standard.txset.asn856.segment.PRFPurchaseOrderReference;
import org.apache.commons.collections4.CollectionUtils;

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
    private List<TD1CarrierDetail> td1List;

    /*
     * REF
     */
    private List<REFReferenceInformation> refList;
    
    /*
     * N1: Party Identifiers
     */
    private List<N1PartyIdentification> n1PartyIdentifications;


    /**
     * returns true if the loop passed in is an Order loop
     */
    public static boolean isOrderLoop(X12Loop loop) {
        return X12Loop.isLoopWithCode(loop, ORDER_LOOP_CODE);
    }

    /**
     * helper method to add TD1
     *
     * @param td1
     */
    public void addTD1CarrierDetail(TD1CarrierDetail td1) {
        if (CollectionUtils.isEmpty(td1List)) {
            td1List = new ArrayList<>();
        }
        td1List.add(td1);
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
    
    /**
     * helper method to add N1 to list
     * @param n1
     */
    public void addN1PartyIdentification(N1PartyIdentification n1) {
        if (CollectionUtils.isEmpty(n1PartyIdentifications)) {
            n1PartyIdentifications = new ArrayList<>();
        }
        n1PartyIdentifications.add(n1);
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

    public List<TD1CarrierDetail> getTd1List() {
        return td1List;
    }

    public void setTd1List(List<TD1CarrierDetail> td1List) {
        this.td1List = td1List;
    }
    
    public List<N1PartyIdentification> getN1PartyIdentifications() {
        return n1PartyIdentifications;
    }

    public void setN1PartyIdentifications(List<N1PartyIdentification> n1PartyIdentifications) {
        this.n1PartyIdentifications = n1PartyIdentifications;
    }

}
