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

import com.walmartlabs.x12.asn856.segment.SN1ItemDetail;
import com.walmartlabs.x12.common.segment.DTMDateTimeReference;
import com.walmartlabs.x12.common.segment.LINItemIdentification;
import com.walmartlabs.x12.common.segment.N1PartyIdentification;
import com.walmartlabs.x12.common.segment.PIDProductIdentification;
import com.walmartlabs.x12.common.segment.REFReferenceInformation;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.standard.X12ParsedLoop;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * represents the Batch Loop 
 *
 */
public class Batch extends X12ParsedLoop {
    
    public static final String BATCH_LOOP_CODE = "ZZ";

    /*
     * PID: Product Identification
     */
    private List<PIDProductIdentification> productIdentifications;
    /*
     * SN1: Item Detail
     */
    private SN1ItemDetail sn1;
    /*
     * LIN: Item Identification
     */
    private List<LINItemIdentification> itemIdentifications;
    /*
     * N1: Party Identifiers
     */
    private List<N1PartyIdentification> n1PartyIdentifications;
    /*
     * DTM: Date/Time Reference
     */
    List<DTMDateTimeReference> dtmReferences;
    /*
     * REF
     */
    private List<REFReferenceInformation> refList;
    
    
    public static boolean isBatchLoop(X12Loop loop) {
        return X12Loop.isLoopWithCode(loop, BATCH_LOOP_CODE);
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
     * helper method to add LIN to list
     * 
     * @param lin
     */
    public void addLINItemIdentification(LINItemIdentification lin) {
        if (CollectionUtils.isEmpty(itemIdentifications)) {
            itemIdentifications = new ArrayList<>();
        }
        itemIdentifications.add(lin);
    }
    
    /**
     * helper method to add PID to list
     * 
     * @param pid
     */
    public void addPIDProductIdentification(PIDProductIdentification pid) {
        if (CollectionUtils.isEmpty(productIdentifications)) {
            productIdentifications = new ArrayList<>();
        }
        productIdentifications.add(pid);
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

    public List<PIDProductIdentification> getProductIdentifications() {
        return productIdentifications;
    }

    public void setProductIdentifications(List<PIDProductIdentification> productIdentifications) {
        this.productIdentifications = productIdentifications;
    }

    public SN1ItemDetail getSn1() {
        return sn1;
    }

    public void setSn1(SN1ItemDetail sn1) {
        this.sn1 = sn1;
    }

    public List<LINItemIdentification> getItemIdentifications() {
        return itemIdentifications;
    }

    public void setItemIdentifications(List<LINItemIdentification> itemIdentifications) {
        this.itemIdentifications = itemIdentifications;
    }

    public List<N1PartyIdentification> getN1PartyIdentifications() {
        return n1PartyIdentifications;
    }

    public void setN1PartyIdentifications(List<N1PartyIdentification> n1PartyIdentifications) {
        this.n1PartyIdentifications = n1PartyIdentifications;
    }

    public List<REFReferenceInformation> getRefList() {
        return refList;
    }

    public void setRefList(List<REFReferenceInformation> refList) {
        this.refList = refList;
    }
    
    public List<DTMDateTimeReference> getDtmReferences() {
        return dtmReferences;
    }

    public void setDtmReferences(List<DTMDateTimeReference> dtmReferences) {
        this.dtmReferences = dtmReferences;
    }
    
}
