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
package sample;

import com.walmartlabs.x12.X12Document;
import com.walmartlabs.x12.X12Parser;
import com.walmartlabs.x12.X12Validator;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class X12SampleTest {

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

        Set<X12ErrorDetail> errors = defaultValidator.validate(x12);
        assertNotNull(errors);
        assertEquals(0, errors.size());

        assertEquals("123", ((MockX12Document) x12).getFunctionalId());
    }

    @Test
    public void test_parse_and_validate_failed() {
        X12Document x12 = defaultParser.parse("MOCK");
        assertNotNull(x12);

        Set<X12ErrorDetail> errors = defaultValidator.validate(x12);
        assertNotNull(errors);
        assertEquals(1, errors.size());

        assertEquals(null, ((MockX12Document) x12).getFunctionalId());
    }
}
