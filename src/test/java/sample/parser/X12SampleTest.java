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
import com.walmartlabs.x12.X12Parser;
import com.walmartlabs.x12.X12Validator;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

public class X12SampleTest {

    X12Parser x12Parser;
    X12Validator x12Validator;

    @BeforeEach
    public void init() {
        x12Parser = new SampleX12Parser();
        x12Validator = new SampleX12Validator();
    }

    @Test
    public void test_parse_and_validate() {
        X12Document x12 = x12Parser.parse("TST*123");
        assertNotNull(x12);

        Set<X12ErrorDetail> errors = x12Validator.validate(x12);
        assertNotNull(errors);
        assertEquals(0, errors.size());

        assertEquals("123", ((SampleX12Document) x12).getFunctionalId());
    }

    @Test
    public void test_parse_and_validate_failed() {
        X12Document x12 = x12Parser.parse("TST");
        assertNotNull(x12);

        Set<X12ErrorDetail> errors = x12Validator.validate(x12);
        assertNotNull(errors);
        assertEquals(1, errors.size());

        X12ErrorDetail x12Error = new ArrayList<>(errors).get(0);
        assertNotNull(x12Error);
        assertEquals("TST", x12Error.getSegmentId());
        assertEquals("01", x12Error.getElementId());
        assertEquals("missing functional id", x12Error.getIssueText());

        assertEquals(null, ((SampleX12Document) x12).getFunctionalId());
    }

    @Test
    public void test_parse_and_validate_failed_bad_delimiter() {
        try {
            x12Parser.parse("BOO*123");
            fail("expected exception!");

        } catch (X12ParserException e) {
            assertEquals("invalid functional group code", e.getMessage());

            Throwable cause = e.getCause();
            assertNull(cause);

            X12ErrorDetail x12Error = e.getErrorDetail();
            assertNotNull(x12Error);
            assertEquals("TST", x12Error.getSegmentId());
            assertEquals("00", x12Error.getElementId());
            assertEquals("invalid functional group code", x12Error.getIssueText());
        }
    }

}
