package com.walmartlabs.x12;

/**
 * 
 * the {@link AbstractX12TransactionSet} is not required when creating a 
 * custom {@link X12TransactionSet}. It is provided as a convenience to handle 
 * common ST/SE elements. 
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
     * SE
     */
    // SE01
    private Integer expectedNumberOfSegments;
    // SE02
    private String trailerControlNumber;
    
    
    public String getTransactionSetIdentifierCode() {
        return transactionSetIdentifierCode;
    }

    public void setTransactionSetIdentifierCode(String transactionSetIdentifierCode) {
        this.transactionSetIdentifierCode = transactionSetIdentifierCode;
    }

    public String getHeaderControlNumber() {
        return headerControlNumber;
    }

    public void setHeaderControlNumber(String headerControlNumber) {
        this.headerControlNumber = headerControlNumber;
    }

    public Integer getExpectedNumberOfSegments() {
        return expectedNumberOfSegments;
    }

    public void setExpectedNumberOfSegments(Integer expectedNumberOfSegments) {
        this.expectedNumberOfSegments = expectedNumberOfSegments;
    }

    public String getTrailerControlNumber() {
        return trailerControlNumber;
    }

    public void setTrailerControlNumber(String trailerControlNumber) {
        this.trailerControlNumber = trailerControlNumber;
    }

}
