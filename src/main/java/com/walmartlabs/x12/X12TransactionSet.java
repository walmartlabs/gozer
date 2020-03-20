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
 * Implementations should store the common ST/SE elements 
 * as well as the attributes specific to the particular 
 * transaction type. 
 *
 */
public interface X12TransactionSet {
    
    public static final String TRANSACTION_SET_HEADER = "ST";
    public static final String TRANSACTION_SET_TRAILER = "SE";
    
    /**
     * The ST01 segment element contains the functional group code, which 
     * identifies the X12 transaction type 
     * 
     * common X12 transaction types associated with retail are 856 (ASN), 
     * 850 (PO), and 812 (invoice). 
     * 
     * @return the ST01 segment value 
     */
    String getTransactionSetIdentifierCode();
    
    void setTransactionSetIdentifierCode(String transactionSetIdentifierCode);

    /**
     * The ST02 segment element contains the control number. This should match
     * the control number on the corresponding transaction trailer segment.
     * 
     * @return the ST02 segment value 
     */
    String getHeaderControlNumber() ;
    
    void setHeaderControlNumber(String headerControlNumber);

    /**
     * The SE01 segment element contains the number of segments that 
     * are in this transaction.
     * 
     * @return the SE01 segment value 
     */
    Integer getExpectedNumberOfSegments();
    
    void setExpectedNumberOfSegments(Integer expectedNumberOfSegments);

    /**
     * The SE02 segment element contains the control number. This should match
     * the control number on the corresponding transaction header segment.
     * 
     * @return the SE02 segment value 
     */
    String getTrailerControlNumber();
    
    void setTrailerControlNumber(String trailerControlNumber);
}
