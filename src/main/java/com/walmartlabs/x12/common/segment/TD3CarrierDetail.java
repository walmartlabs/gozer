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

package com.walmartlabs.x12.common.segment;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Purpose: To specify transportation details relating to the equipment used by
 * the carrierLoop
 *
 */
public class TD3CarrierDetail {

    public static final String IDENTIFIER = "TD3";

    /*
     * TD3
     */
    // TD301
    private String equipmentDescriptionCode;
    // TD302
    private String equipmentInitial;
    // TD303
    private String equipmentNumber;
    // TD309
    private String sealNumber;
    
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
    

    public String getEquipmentDescriptionCode() {
        return equipmentDescriptionCode;
    }

    public void setEquipmentDescriptionCode(String equipmentDescriptionCode) {
        this.equipmentDescriptionCode = equipmentDescriptionCode;
    }

    public String getEquipmentInitial() {
        return equipmentInitial;
    }

    public void setEquipmentInitial(String equipmentInitial) {
        this.equipmentInitial = equipmentInitial;
    }

    public String getEquipmentNumber() {
        return equipmentNumber;
    }

    public void setEquipmentNumber(String equipmentNumber) {
        this.equipmentNumber = equipmentNumber;
    }

    public String getSealNumber() {
        return sealNumber;
    }

    public void setSealNumber(String sealNumber) {
        this.sealNumber = sealNumber;
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
