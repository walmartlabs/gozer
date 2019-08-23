package sample.aaa;

import com.walmartlabs.x12.AbstractX12TransactionSet;

public class TypeAaaTransactionSet extends AbstractX12TransactionSet {
    private String anAaaOnlyValue;
    
    public String getAaaOnlyValue() {
        return anAaaOnlyValue;
    }

    public void setAaaOnlyValue(String value) {
        this.anAaaOnlyValue = value;
    }
}