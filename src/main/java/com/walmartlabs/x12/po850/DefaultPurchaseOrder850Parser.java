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
package com.walmartlabs.x12.po850;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.standard.StandardX12Parser;
import com.walmartlabs.x12.standard.StandardX12Document;
import com.walmartlabs.x12.standard.X12Group;

import java.util.List;

/**
 * 
 * @deprecated
 *
 */
public class DefaultPurchaseOrder850Parser extends StandardX12Parser<StandardX12Document> {

    @Override
    protected void parseTransactionSet(List<X12Segment> segmentLines, X12Group x12Group) {
    }
}
