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

import com.walmartlabs.x12.dex.dx894.DefaultDex894Parser;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.exceptions.X12ParserException;

public final class VersionUtil {

    /**
     * return the numeric part of a version number
     *
     * @param versionValue
     * @return
     */
    public static Integer parseVersion(String versionValue) {
        try {
            String versionNum = VersionUtil.remove0LeftPadding(versionValue).replace("UCS", "");
            return Integer.valueOf(versionNum);
        } catch (NumberFormatException e) {
            throw new X12ParserException(new X12ErrorDetail(DefaultDex894Parser.APPLICATION_HEADER_ID, "DXS03", "invalid version format"));
        }
    }

    private static String remove0LeftPadding(String value) {
        return value.replaceFirst("^0+(?!$)", "");
    }

    private VersionUtil() {
    }

}
