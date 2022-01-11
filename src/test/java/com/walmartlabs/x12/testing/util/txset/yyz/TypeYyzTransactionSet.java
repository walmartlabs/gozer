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

package com.walmartlabs.x12.testing.util.txset.yyz;

import com.walmartlabs.x12.AbstractX12TransactionSet;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.standard.X12Loop;

import java.util.List;

/**
 *
 * the {@link AbstractX12TransactionSet} is not required when creating a
 * custom {@link X12TransactionSet}. It is provided as a convenience to handle
 * common ST/SE elements.
 *
 */
public class TypeYyzTransactionSet implements X12TransactionSet {
    private String transactionSetIdentifierCode;
    private String headerControlNumber;
    private String trailerControlNumber;
    private Integer numSegments;
    private String rushValue;
    private Integer transactionLineItems;

    private List<X12Loop> loops;
    private List<X12ErrorDetail> loopingErrors;

    @Override
    public String getTransactionSetIdentifierCode() {
        return this.transactionSetIdentifierCode;
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
    public String getTrailerControlNumber() {
        return trailerControlNumber;
    }

    @Override
    public void setTrailerControlNumber(String trailerControlNumber) {
        this.trailerControlNumber = trailerControlNumber;
    }

    @Override
    public Integer getExpectedNumberOfSegments() {
        return numSegments;
    }

    @Override
    public void setExpectedNumberOfSegments(Integer expectedNumberOfSegments) {
        this.numSegments = expectedNumberOfSegments;
    }

    public String getRushValue() {
        return rushValue;
    }

    public void setRushValue(String value) {
        this.rushValue = value;
    }

    @Override
    public Integer getTransactionLineItems() {
        return transactionLineItems;
    }

    @Override
    public void setTransactionLineItems(Integer transactionLineItems) {
        this.transactionLineItems = transactionLineItems;
    }

    public List<X12Loop> getLoops() {
        return loops;
    }

    public void setLoops(List<X12Loop> loops) {
        this.loops = loops;
    }

    public List<X12ErrorDetail> getLoopingErrors() {
        return loopingErrors;
    }

    public void setLoopingErrors(List<X12ErrorDetail> loopingErrors) {
        this.loopingErrors = loopingErrors;
    }

}