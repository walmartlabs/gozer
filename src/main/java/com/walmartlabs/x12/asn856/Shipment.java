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

import com.walmartlabs.x12.common.segment.N1PartyIdentification;
import com.walmartlabs.x12.common.segment.REFReferenceInformation;
import com.walmartlabs.x12.common.segment.TD1CarrierDetail;
import com.walmartlabs.x12.common.segment.TD3CarrierDetail;
import com.walmartlabs.x12.common.segment.TD5CarrierDetail;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.standard.X12ParsedLoop;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Shipment level of information
 * 
 */
public class Shipment extends X12ParsedLoop {

    public static final String SHIPMENT_LOOP_CODE = "S";

    /*
     * TD1: Carrier Details
     */
    private TD1CarrierDetail td1;
    /*
     * TD3: Carrier Details
     */
    private TD3CarrierDetail td3;
    /*
     * TD5: Carrier Details
     */
    private TD5CarrierDetail td5;
    /*
     * N1: Party Identifiers
     */
    private List<N1PartyIdentification> n1PartyIdentifications;
    /*
     * REF: references
     */
    private List<REFReferenceInformation> refList;
    
    public static boolean isShipmentLoop(X12Loop loop) {
        return X12Loop.isLoopWithCode(loop, SHIPMENT_LOOP_CODE);
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
    
    public TD1CarrierDetail getTd1() {
        return td1;
    }

    public void setTd1(TD1CarrierDetail td1) {
        this.td1 = td1;
    }

    public TD3CarrierDetail getTd3() {
        return td3;
    }

    public void setTd3(TD3CarrierDetail td3) {
        this.td3 = td3;
    }

    public TD5CarrierDetail getTd5() {
        return td5;
    }

    public void setTd5(TD5CarrierDetail td5) {
        this.td5 = td5;
    }

    public List<N1PartyIdentification> getN1PartyIdenfications() {
        return n1PartyIdentifications;
    }

    public void setN1PartIdentifications(List<N1PartyIdentification> n1PartyIdenfiers) {
        this.n1PartyIdentifications = n1PartyIdenfiers;
    }

    public List<REFReferenceInformation> getRefList() {
        return refList;
    }

    public void setRefList(List<REFReferenceInformation> refList) {
        this.refList = refList;
    }

}
