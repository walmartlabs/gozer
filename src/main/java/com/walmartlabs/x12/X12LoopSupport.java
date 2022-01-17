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

package com.walmartlabs.x12;

import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.standard.txset.asn856.AsnTransactionSet;

import java.util.List;

/**
 *
 * Transaction sets that have HL looping as part of their
 * document definition should implement this interface
 * (or extend an abstract class that does)
 * 
 * Note: there is no "getLoops" added to this interface
 * as it is expected an implementing class would 
 * provide a more direct method to do that
 * (ie see getShipment on {@link AsnTransactionSet}
 *
 */
public interface X12LoopSupport {

    /**
     * any looping errors will be stored in this list
     *
     * possible errors include orphan loops and those without proper indexing
     *
     * @return list of {@link X12ErrorDetail}
     */
    List<X12ErrorDetail> getLoopingErrors();
}
