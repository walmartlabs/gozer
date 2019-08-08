package sample.yyz;

import com.walmartlabs.x12.X12TransactionSet;

public class TypeYyzTransactionSet implements X12TransactionSet {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}