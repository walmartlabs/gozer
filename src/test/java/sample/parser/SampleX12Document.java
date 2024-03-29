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

package sample.parser;

import com.walmartlabs.x12.X12Document;

/**
 *
 * a sample document that only supports
 * a one line message
 *
 * TST*NNN
 *
 * where NNN is the functional id
 *
 */
public class SampleX12Document implements X12Document {
    public static final String FUNCTIONAL_GROUP_CODE = "TST";

    private String functionalId;

    public String getFunctionalId() {
        return functionalId;
    }

    public void setFunctionalId(String functionalId) {
        this.functionalId = functionalId;
    }

}
