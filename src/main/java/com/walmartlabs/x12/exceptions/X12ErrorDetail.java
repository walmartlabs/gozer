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
    // can be used as TED 02 error text in 824
    private String issueText;
    // can be used as TED 07 invalid value in 824
    private String invalidValue;
    
    public X12ErrorDetail(String segmentId, String elementId, String issueText) {
        this.segmentId = segmentId;
        this.elementId = elementId;
        this.issueText = issueText;
    }

    public X12ErrorDetail(String segmentId, String elementId, String issueText, String invalidValue) {
        this.segmentId = segmentId;
        this.elementId = elementId;
        this.issueText = issueText;
        this.invalidValue = invalidValue;
    }

    public String getSegmentId() {
        return segmentId;
    }

    public String getElementId() {
        return elementId;
    }

    public String getIssueText() {
        return issueText;
    }
    
    public String getInvalidValue() {
        return invalidValue;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("error:");
        sb.append("\\nsegmentId: ").append(segmentId);
        sb.append("\\nelementId: ").append(elementId);
        sb.append("\\nissue text: ").append(issueText);
        sb.append("\\nissue value: ").append(invalidValue);
        return sb.toString();
    }
}
