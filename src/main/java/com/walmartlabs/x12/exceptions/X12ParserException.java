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

package com.walmartlabs.x12.exceptions;

/**
 * ParserException
 */
public class X12ParserException extends RuntimeException {
    private X12ErrorDetail errorDetail;

    public X12ParserException(String msg) {
        super(msg);
    }

    public X12ParserException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public X12ParserException(Throwable cause) {
        super(cause);
    }

    public X12ParserException(String msg, X12ErrorDetail error) {
        super(msg);
        this.errorDetail = error;
    }

    public X12ParserException(X12ErrorDetail error) {
        super(error != null ? error.getIssueText() : "");
        this.errorDetail = error;
    }

    public X12ErrorDetail getErrorDetail() {
        return errorDetail;
    }

}
