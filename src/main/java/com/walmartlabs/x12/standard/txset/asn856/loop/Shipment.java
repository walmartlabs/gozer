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

import com.walmartlabs.x12.common.segment.DTMDateTimeReference;
import com.walmartlabs.x12.common.segment.FOBRelatedInstructions;
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
    private List<TD1CarrierDetail> td1List;
    /*
     * TD5: Carrier Details
     */
    private List<TD5CarrierDetail> td5List;
    /*
     * TD3: Carrier Details
     */
    private List<TD3CarrierDetail> td3List;
    /*
     * REF: references
     */
    private List<REFReferenceInformation> refList;
    /*
     * DTM: Date/Time Reference
     */
    private List<DTMDateTimeReference> dtmReferences;
    /*
     * FOB: payment related instructions
     */
    private FOBRelatedInstructions fob;
    /*
     * N1: Party Identifiers
     */
    private List<N1PartyIdentification> n1PartyIdentifications;

    
    /**
     * returns true if the loop passed in is a Shipment loop
     */
    public static boolean isShipmentLoop(X12Loop loop) {
        return X12Loop.isLoopWithCode(loop, SHIPMENT_LOOP_CODE);
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
     * helper method to add TD5
     * 
     * @param td5
     */
    public void addTD5CarrierDetail(TD5CarrierDetail td5) {
        if (CollectionUtils.isEmpty(td5List)) {
            td5List = new ArrayList<>();
        }
        td5List.add(td5);
    }
    
    /**
     * helper method to add TD3
     * 
     * @param td3
     */
    public void addTD3CarrierDetail(TD3CarrierDetail td3) {
        if (CollectionUtils.isEmpty(td3List)) {
            td3List = new ArrayList<>();
        }
        td3List.add(td3);
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
     * helper method to add DTM to list
     * @param dtm
     */
    public void addDTMDateTimeReference(DTMDateTimeReference dtm) {
        if (CollectionUtils.isEmpty(dtmReferences)) {
            dtmReferences = new ArrayList<>();
        }
        dtmReferences.add(dtm);
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
    

    public List<N1PartyIdentification> getN1PartyIdentifications() {
        return n1PartyIdentifications;
    }

    public void setN1PartyIdentifications(List<N1PartyIdentification> n1PartyIdentifications) {
        this.n1PartyIdentifications = n1PartyIdentifications;
    }

    public List<TD1CarrierDetail> getTd1List() {
        return td1List;
    }

    public void setTd1List(List<TD1CarrierDetail> td1List) {
        this.td1List = td1List;
    }

    public List<TD5CarrierDetail> getTd5List() {
        return td5List;
    }

    public void setTd5List(List<TD5CarrierDetail> td5List) {
        this.td5List = td5List;
    }
    
    public List<TD3CarrierDetail> getTd3List() {
        return td3List;
    }

    public void setTd3List(List<TD3CarrierDetail> td3List) {
        this.td3List = td3List;
    }
    
    public List<DTMDateTimeReference> getDtmReferences() {
        return dtmReferences;
    }

    public void setDtmReferences(List<DTMDateTimeReference> dtmReferences) {
        this.dtmReferences = dtmReferences;
    }

    public List<REFReferenceInformation> getRefList() {
        return refList;
    }

    public void setRefList(List<REFReferenceInformation> refList) {
        this.refList = refList;
    }

    public FOBRelatedInstructions getFob() {
        return fob;
    }

    public void setFob(FOBRelatedInstructions fob) {
        this.fob = fob;
    }

}
