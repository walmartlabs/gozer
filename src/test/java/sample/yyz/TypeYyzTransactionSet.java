package sample.yyz;

import com.walmartlabs.x12.AbstractX12TransactionSet;
import com.walmartlabs.x12.X12TransactionSet;

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
    private String value;

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
    
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    
}