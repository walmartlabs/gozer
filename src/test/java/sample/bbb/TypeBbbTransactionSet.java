package sample.bbb;

import com.walmartlabs.x12.X12TransactionSet;

public class TypeBbbTransactionSet implements X12TransactionSet {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}