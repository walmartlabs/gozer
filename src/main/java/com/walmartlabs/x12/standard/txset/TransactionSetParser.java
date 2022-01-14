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

package com.walmartlabs.x12.standard.txset;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.standard.X12Group;

import java.util.List;

public interface TransactionSetParser {

    /**
     * parse the transaction set
     * implementation should NOT add the transaction set to the group
     * the group is available to give the transaction context if needed
     *
     * it can expect the list of transactionSegments to have the first segment have an id of ST
     * and the last segment have an id of SE
     *
     * @param transactionSegments
     * @param x12Group
     * @return the parsed transaction set
     */
    X12TransactionSet parseTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group);

}
