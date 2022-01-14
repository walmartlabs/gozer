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

package com.walmartlabs.x12.util.loop;

import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.standard.X12Loop;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class X12LoopHolder {

    private List<X12Loop> loops;

    private List<X12ErrorDetail> loopErrors;

    /**
     * helper method to add loop error
     *
     * @param errorDetail
     */
    public void addX12ErrorDetail(X12ErrorDetail errorDetail) {
        if (CollectionUtils.isEmpty(loopErrors)) {
            loopErrors = new ArrayList<>();
        }
        loopErrors.add(errorDetail);
    }

    public List<X12Loop> getLoops() {
        return loops;
    }

    public void setLoops(List<X12Loop> loops) {
        this.loops = loops;
    }

    public List<X12ErrorDetail> getLoopErrors() {
        return loopErrors;
    }

    public void setLoopErrors(List<X12ErrorDetail> loopErrors) {
        this.loopErrors = loopErrors;
    }


}
