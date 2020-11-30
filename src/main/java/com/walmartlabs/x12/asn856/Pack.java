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
import com.walmartlabs.x12.asn856.segment.SN1ItemDetail;
import com.walmartlabs.x12.common.segment.LINItemIdentification;
import com.walmartlabs.x12.common.segment.PIDProductIdentification;
import com.walmartlabs.x12.common.segment.TD1CarrierDetail;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.standard.X12ParsedLoop;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Pack level of information
 * 
 */
public class Pack extends X12ParsedLoop {

    public static final String PACK_LOOP_CODE = "P";
    
    /*
     * MAN: Marking
     */
    private MANMarkNumber man;
    /*
     * PO4: Item Physical Details
     */
    private PO4ItemPhysicalDetail po4;
    /*
     * PID: Product Identification
     */
    private List<PIDProductIdentification> productIdentifications;
    /*
     * TD1: Carrier Details
     */
    private TD1CarrierDetail td1;
    /*
     * SN1: Item Detail
     */
    private SN1ItemDetail sn1;
    /*
     * LIN: Item Identification
     */
    private List<LINItemIdentification> itemIdentifications;
    
    
    public static boolean isPackLoop(X12Loop loop) {
        return X12Loop.isLoopWithCode(loop, PACK_LOOP_CODE);
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

    public TD1CarrierDetail getTd1() {
        return td1;
    }

    public void setTd1(TD1CarrierDetail td1) {
        this.td1 = td1;
    }
    
}
