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
package com.walmartlabs.x12.util;

import com.walmartlabs.x12.exceptions.X12ParserException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VersionUtilTest {

    @Test
    public void test() {
        assertEquals(new Integer(4010), VersionUtil.parseVersion("004010UCS"));
        assertEquals(new Integer(5010), VersionUtil.parseVersion("005010UCS"));
        assertEquals(new Integer(4010), VersionUtil.parseVersion("4010"));
        assertEquals(new Integer(4010), VersionUtil.parseVersion("004010"));
        assertEquals(new Integer(4010), VersionUtil.parseVersion("4010UCS"));
    }

    @Test(expected = X12ParserException.class)
    public void test_error_text() {
        VersionUtil.parseVersion("VERSION");
    }

    @Test(expected = X12ParserException.class)
    public void test_error_incorrect_ending() {
       VersionUtil.parseVersion("004010VERSION");
    }

}
