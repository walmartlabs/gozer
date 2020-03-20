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

import com.walmartlabs.x12.X12Parser;
import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 
 * Sample parser 
 * First Segment expected to have identifier YYZ
 *
 */
public class SampleX12Parser implements X12Parser<SampleX12Document> {

    @Override
    public SampleX12Document parse(String sourceData) {
        SampleX12Document mockX12 = null;

        try {
            if (!StringUtils.isEmpty(sourceData)) {
                mockX12 = new SampleX12Document();
                List<X12Segment> segments = this.splitSourceDataIntoSegments(sourceData);
                if (!segments.isEmpty()) {
                    // parse the first segment
                    X12Segment firstSegment = segments.get(0);
                    if ("YYZ".equals(firstSegment.getIdentifier())) {
                        mockX12.setFunctionalId(firstSegment.getElement(1));
                    } else {
                        X12ErrorDetail error = new X12ErrorDetail("YYZ", "YYZ00", "invalid identifier");
                        throw new X12ParserException(error);
                    }
                }
            }
        } catch (Exception e) {
            if (e instanceof X12ParserException) {
                throw e;
            } else {
                throw new X12ParserException(e);
            }
        }
        return mockX12;
    }

}