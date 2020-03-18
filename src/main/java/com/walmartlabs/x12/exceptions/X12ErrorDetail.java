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

public class X12ErrorDetail {
    private String segmentId;
    private String elementId;
    private Integer lineNumber;
    private String message;

    public X12ErrorDetail(String segmentId, String elementId, String msg) {
        this.segmentId = segmentId;
        this.elementId = elementId;
        this.message = msg;
    }

    public X12ErrorDetail(String segmentId, String elementId, Integer lineNumber, String msg) {
        this.segmentId = segmentId;
        this.elementId = elementId;
        this.lineNumber = lineNumber;
        this.message = msg;
    }

    public String getSegmentId() {
        return segmentId;
    }

    public String getElementId() {
        return elementId;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DEX error:");
        sb.append("\\nsegmentId: ").append(segmentId);
        sb.append("\\nelementId: ").append(elementId);
        sb.append("\\nline number: ").append(lineNumber);
        sb.append("\\nmessage: ").append(message);
        return sb.toString();
    }
}
