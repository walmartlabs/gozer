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
import com.walmartlabs.x12.exceptions.X12ParserException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class X12ExampleTest {

    X12Parser defaultParser;
    X12Validator defaultValidator;

    @Before
    public void init() {
        defaultParser = new MockX12Parser();
        defaultValidator = new MockX12Validator();
    }

    @Test
    public void test_parse_and_validate() {
        X12Document x12 = defaultParser.parse("MOCK*123");
        assertNotNull(x12);
        assertEquals("MOCK", x12.getX12DocumentType());

        Set<X12ErrorDetail> errors = defaultValidator.validate(x12);
        assertNotNull(errors);
        assertEquals(0, errors.size());

        assertEquals("123", ((MockX12Document)x12).getFunctionalId());
    }

    @Test
    public void test_parse_and_validate_failed() {
        X12Document x12 = defaultParser.parse("MOCK");
        assertNotNull(x12);
        assertEquals("MOCK", x12.getX12DocumentType());

        Set<X12ErrorDetail> errors = defaultValidator.validate(x12);
        assertNotNull(errors);
        assertEquals(1, errors.size());

        assertEquals(null, ((MockX12Document)x12).getFunctionalId());
    }

    /**
     * simple document
     */
    private class MockX12Document implements X12Document {
        private String functionalId;

        public String getFunctionalId() {
            return functionalId;
        }

        public void setFunctionalId(String functionalId) {
            this.functionalId = functionalId;
        }

        @Override
        public String getX12DocumentType() {
            return "MOCK";
        }

    }

    /**
     * simple parser
     */
    private class MockX12Parser implements X12Parser<MockX12Document> {

        @Override
        public MockX12Document parse(String sourceData) {
            MockX12Document mockX12 = null;

            try {
                if (!StringUtils.isEmpty(sourceData)) {
                    mockX12 = new MockX12Document();
                    List<X12Segment> segments = this.splitSourceDataIntoSegments(sourceData);
                    if (!segments.isEmpty()) {
                        mockX12.setFunctionalId(segments.get(0).getSegmentElement(1));
                    }
                }
            } catch (Exception e) {
                throw new X12ParserException(e);
            }
            return mockX12;
        }

    }

    /**
     * simple validator
     */
    private class MockX12Validator implements X12Validator<MockX12Document> {

        @Override
        public Set<X12ErrorDetail> validate(MockX12Document pojo, boolean performCrcCheck) {
            Set<X12ErrorDetail> errors = new HashSet<>();
            if (pojo == null) {
                errors.add(new X12ErrorDetail(null, null, "no object"));
            } else {
                if (StringUtils.isEmpty(pojo.getFunctionalId())) {
                    errors.add(new X12ErrorDetail("MOCK", "MK01", "missing functional id"));
                }
            }

            return errors;
        }

    }
}
