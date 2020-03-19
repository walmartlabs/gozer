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
import com.walmartlabs.x12.common.segment.TD1CarrierDetails;
import com.walmartlabs.x12.common.segment.TD3CarrierDetails;
import com.walmartlabs.x12.common.segment.TD5CarrierDetails;
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
    private TD1CarrierDetails td1;
    /*
     * TD3: Carrier Details
     */
    private TD3CarrierDetails td3;
    /*
     * TD5: Carrier Details
     */
    private TD5CarrierDetails td5;
    /*
     * N1: Party Identifiers
     */
    private List<N1PartyIdentification> n1PartyIdentifications;
    
    public static boolean isShipmentLoop(X12Loop loop) {
        return X12Loop.isLoopWithCode(loop, SHIPMENT_LOOP_CODE);
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
    
    public TD1CarrierDetails getTd1() {
        return td1;
    }

    public void setTd1(TD1CarrierDetails td1) {
        this.td1 = td1;
    }

    public TD3CarrierDetails getTd3() {
        return td3;
    }

    public void setTd3(TD3CarrierDetails td3) {
        this.td3 = td3;
    }

    public TD5CarrierDetails getTd5() {
        return td5;
    }

    public void setTd5(TD5CarrierDetails td5) {
        this.td5 = td5;
    }

    public List<N1PartyIdentification> getN1PartyIdenfications() {
        return n1PartyIdentifications;
    }

    public void setN1PartIdentifications(List<N1PartyIdentification> n1PartyIdenfiers) {
        this.n1PartyIdentifications = n1PartyIdenfiers;
    }

}
