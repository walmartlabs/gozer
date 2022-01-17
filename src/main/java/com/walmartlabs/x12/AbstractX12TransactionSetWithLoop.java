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
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * the {@link AbstractX12TransactionSetWithLoop} is not required when creating a
 * custom {@link X12TransactionSet}. It is provided as a convenience to handle
 * common looping constructs for some document types
 *
 */
public abstract class AbstractX12TransactionSetWithLoop extends AbstractX12TransactionSet implements X12LoopSupport {

    // looping issues are captured
    private List<X12ErrorDetail> loopingErrors;
    
    /**
     * helper method to add X12ErrorDetail for looping errors
     * @param errorDetail
     */
    public void addX12ErrorDetailForLoop(X12ErrorDetail errorDetail) {
        this.addX12ErrorDetailForLoop(Collections.singletonList(errorDetail));
    }

    /**
     * helper method to add multiple X12ErrorDetail for looping errors
     * @param errorDetail
     */
    public void addX12ErrorDetailForLoop(List<X12ErrorDetail> errorDetails) {
        if (!CollectionUtils.isEmpty(errorDetails)) {
            if (CollectionUtils.isEmpty(loopingErrors)) {
                loopingErrors = new ArrayList<>();
            }
            loopingErrors.addAll(errorDetails);
        }
    }
    
    @Override
    public List<X12ErrorDetail> getLoopingErrors() {
        return loopingErrors;
    }

}
