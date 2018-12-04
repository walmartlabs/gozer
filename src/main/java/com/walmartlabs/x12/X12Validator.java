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

import java.util.Set;

public interface X12Validator<T extends X12Document> {
    /**
     * validate the X12 transmission including the CRC integrity check
     *
     * @param pojo the domain object returned by an {@link X12Parser}
     * @return Set of error details ({@link X12ErrorDetail}
     */
    default Set<X12ErrorDetail> validate(T pojo) {
        return this.validate(pojo, true);
    }

    /**
     * validate the X12 transmission with option to turn on/off the CRC integrity check
     *
     * @param pojo the domain object returned by an {@link X12Parser}
     * @param performCrcCheck set to true to perform the check
     * @return Set of error details ({@link X12ErrorDetail}
     */
    Set<X12ErrorDetail> validate(T pojo, boolean performCrcCheck);
}
