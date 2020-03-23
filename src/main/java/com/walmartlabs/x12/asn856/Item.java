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
import com.walmartlabs.x12.common.segment.LINItemIdentification;
import com.walmartlabs.x12.common.segment.PIDProductIdentification;
import com.walmartlabs.x12.standard.X12Loop;
import com.walmartlabs.x12.standard.X12ParsedLoop;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Item level of information
 * 
 */
public class Item extends X12ParsedLoop {

    public static final String ITEM__LOOP_CODE = "I";

    /*
     * PID: Product Identification
     */
    private PIDProductIdentification pid;
    /*
     * SN1: Item Detail
     */
    private SN1ItemDetail sn1;
    /*
     * LIN: Item Identification
     */
    private List<LINItemIdentification> itemIdentifications;

    public static boolean isItemLoop(X12Loop loop) {
        return X12Loop.isLoopWithCode(loop, ITEM__LOOP_CODE);
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

    public PIDProductIdentification getPid() {
        return pid;
    }

    public void setPid(PIDProductIdentification pid) {
        this.pid = pid;
    }

    public List<LINItemIdentification> getItemIdentifications() {
        return itemIdentifications;
    }

    public void setItemIdentifications(List<LINItemIdentification> itemIdentifications) {
        this.itemIdentifications = itemIdentifications;
    }

    public SN1ItemDetail getSn1() {
        return sn1;
    }

    public void setSn1(SN1ItemDetail sn1) {
        this.sn1 = sn1;
    }

}
