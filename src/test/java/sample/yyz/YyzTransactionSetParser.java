package sample.yyz;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.standard.TransactionSetParser;
import com.walmartlabs.x12.standard.X12Group;

import java.util.List;

/**
 * 
 * TODO: what should a "normal" TransactionSetParser if it is handed a 
 * transaction set it can't handle? 
 *
 */
public class YyzTransactionSetParser implements TransactionSetParser {

    @Override
    public X12TransactionSet parseTransactionSet(List<X12Segment> txLines, X12Group x12Group) {
        TypeYyzTransactionSet tx = null;
        if (txLines != null && !txLines.isEmpty()) {
            String type = txLines.get(0).getSegmentElement(1);
            if ("YYZ".equals(type)) {
                tx = new TypeYyzTransactionSet();
                X12Segment nextLine = txLines.get(1);
                if (nextLine != null) {
                    tx.setValue(nextLine.getSegmentElement(1));
                }
            }
        }
        return tx;
    }
}