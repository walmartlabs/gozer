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

import com.walmartlabs.x12.common.segment.LINItemIdentification;
import com.walmartlabs.x12.common.segment.N1PartyIdentification;
import com.walmartlabs.x12.common.segment.PIDProductIdentification;
import com.walmartlabs.x12.common.segment.TD1CarrierDetail;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.standard.X12ParsedLoop;
import com.walmartlabs.x12.standard.txset.asn856.segment.MANMarkNumber;
import com.walmartlabs.x12.standard.txset.asn856.segment.PO4ItemPhysicalDetail;
import com.walmartlabs.x12.standard.txset.asn856.segment.SN1ItemDetail;
import org.apache.commons.collections4.CollectionUtils;

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
    private List<MANMarkNumber> manList;
    
    /*
     * N1: Party Identifiers
     */
    private List<N1PartyIdentification> n1PartyIdentifications;
    
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
    private List<TD1CarrierDetail> td1List;
    
    /*
     * SN1: Item Detail
     */
    private SN1ItemDetail sn1;
    
    /*
     * LIN: Item Identification
     */
    private List<LINItemIdentification> itemIdentifications;

    /**
     * returns true if the loop passed in is a Pack loop
     */
    public static boolean isPackLoop(X12Loop loop) {
        return X12Loop.isLoopWithCode(loop, PACK_LOOP_CODE);
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
     * helper method to add N1 to list
     * @param n1
     */
    public void addN1PartyIdentification(N1PartyIdentification n1) {
        if (CollectionUtils.isEmpty(n1PartyIdentifications)) {
            n1PartyIdentifications = new ArrayList<>();
        }
        n1PartyIdentifications.add(n1);
    }

    public PO4ItemPhysicalDetail getPo4() {
        return po4;
    }

    public void setPo4(PO4ItemPhysicalDetail po4) {
        this.po4 = po4;
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

    public List<MANMarkNumber> getManList() {
        return manList;
    }

    public void setManList(List<MANMarkNumber> manList) {
        this.manList = manList;
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
