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
import com.walmartlabs.x12.common.AbstractStandardX12Parser;
import com.walmartlabs.x12.exceptions.X12ParserException;
import org.springframework.util.StringUtils;

import java.util.List;

public class DefaultPurchaseOrder850Parser extends AbstractStandardX12Parser<PurchaseOrder850> {
    /**
     * parse the PO 850 transmission into a representative Java object
     *
     * @return {@link PurchaseOrder850}
     * @throws X12ParserException
     */
    @Override
    public PurchaseOrder850 parse(String sourceData) {
        PurchaseOrder850 po850 = null;

        if (!StringUtils.isEmpty(sourceData)) {
            po850 = new PurchaseOrder850();
            List<X12Segment> segmentLines = this.splitSourceDataIntoSegments(sourceData);
            // TODO: implement the parser
            int segmentIdx = 0;
            this.parseInterchangeControlHeader(segmentLines.get(segmentIdx), po850);
        }

        return po850;
    }
}
