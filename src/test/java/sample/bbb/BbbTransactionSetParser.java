package sample.bbb;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.standard.AbstractTransactionSetParserChainable;
import com.walmartlabs.x12.standard.X12Group;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BbbTransactionSetParser extends AbstractTransactionSetParserChainable {

    protected X12TransactionSet doParse(List<X12Segment> txLines, X12Group x12Group) {
        assertNotNull(txLines);
        assertEquals(3, txLines.size());
        assertEquals("ST", txLines.get(0).getIdentifier());
        assertEquals("BBB", txLines.get(0).getElement(1));
        assertEquals("TEST", txLines.get(1).getIdentifier());
        assertEquals("SE", txLines.get(2).getIdentifier());
        
        TypeBbbTransactionSet tx = new TypeBbbTransactionSet();
        tx.setTransactionSetIdentifierCode(txLines.get(0).getElement(1));
        tx.setValue(txLines.get(1).getElement(1));
        return tx;
    }

    protected boolean handlesTransactionSet(List<X12Segment> transactionSegments, X12Group x12Group) {
        boolean isHandled = false;
        if (transactionSegments != null) {
            X12Segment segment = transactionSegments.get(0);
            if (segment != null && "BBB".equals(segment.getElement(1))) {
                isHandled = true;
            }
        }
        return isHandled;
    }
}