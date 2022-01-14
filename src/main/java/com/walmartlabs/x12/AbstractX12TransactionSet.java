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

/**
 *
 * the {@link AbstractX12TransactionSet} is not required when creating a
 * custom {@link X12TransactionSet}. It is provided as a convenience to handle
 * common ST/SE elements and the CTT segment.
 *
 */
public abstract class AbstractX12TransactionSet implements X12TransactionSet {

    /*
     * ST
     */
    // ST01
    private String transactionSetIdentifierCode;
    // ST02
    private String headerControlNumber;

    /*
     * CTT (optional)
     */
    // CTT 01
    private Integer transactionLineItems;

    /*
     * SE
     */
    // SE01
    private Integer expectedNumberOfSegments;
    // SE02
    private String trailerControlNumber;


    @Override
    public String getTransactionSetIdentifierCode() {
        return transactionSetIdentifierCode;
    }

    @Override
    public void setTransactionSetIdentifierCode(String transactionSetIdentifierCode) {
        this.transactionSetIdentifierCode = transactionSetIdentifierCode;
    }

    @Override
    public String getHeaderControlNumber() {
        return headerControlNumber;
    }

    @Override
    public void setHeaderControlNumber(String headerControlNumber) {
        this.headerControlNumber = headerControlNumber;
    }

    @Override
    public Integer getExpectedNumberOfSegments() {
        return expectedNumberOfSegments;
    }

    @Override
    public void setExpectedNumberOfSegments(Integer expectedNumberOfSegments) {
        this.expectedNumberOfSegments = expectedNumberOfSegments;
    }

    @Override
    public String getTrailerControlNumber() {
        return trailerControlNumber;
    }

    @Override
    public void setTrailerControlNumber(String trailerControlNumber) {
        this.trailerControlNumber = trailerControlNumber;
    }

    @Override
    public Integer getTransactionLineItems() {
        return transactionLineItems;
    }

    @Override
    public void setTransactionLineItems(Integer transactionLineItems) {
        this.transactionLineItems = transactionLineItems;
    }

}
