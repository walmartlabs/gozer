package sample.bbb;

import com.walmartlabs.x12.AbstractX12TransactionSet;

public class TypeBbbTransactionSet extends AbstractX12TransactionSet {
    
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}