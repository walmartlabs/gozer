package com.walmartlabs.x12.standard;

import com.walmartlabs.x12.X12TransactionSet;
import sample.aaa.TypeAaaTransactionSet;
import sample.bbb.TypeBbbTransactionSet;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public final class AssertBaseDocumentUtil {

    /**
     * 
     * check for correct parsing of base document
     * when no parsers  are registered
     */
    public static void assertBaseDocumentNoParsers(StandardX12Document x12) {
        assertNotNull(x12);
        
        // ISA segment
        InterchangeControlEnvelope isa = x12.getInterchangeControlEnvelope();
        assertNotNull(isa);
        assertEquals("01", isa.getAuthorizationInformationQualifier());
        assertEquals("0000000000", isa.getAuthorizationInformation());
        assertEquals("01", isa.getSecurityInformationQualifier());
        assertEquals("0000000000", isa.getSecurityInformation());
        assertEquals("ZZ", isa.getInterchangeIdQualifier());
        assertEquals("ABCDEFGHIJKLMNO", isa.getInterchangeSenderId());
        assertEquals("ZZ", isa.getInterchangeIdQualifierTwo());
        assertEquals("123456789012345", isa.getInterchangeReceiverId());
        assertEquals("101127", isa.getInterchangeDate());
        assertEquals("1719", isa.getInterchangeTime());
        assertEquals("U", isa.getInterchangeControlStandardId());
        assertEquals("00400", isa.getInterchangeControlVersion());
        assertEquals("000000049", isa.getInterchangeControlNumber());
        assertEquals("0", isa.getAcknowledgementRequested());
        assertEquals("P", isa.getUsageIndicator());
        assertEquals(">", isa.getElementSeparator());
        
        // ise trailer
        InterchangeControlEnvelope ise = x12.getInterchangeControlEnvelope();
        assertNotNull(ise);
        assertEquals("000000049", ise.getInterchangeControlNumber());
        assertEquals(new Integer(2), ise.getNumberOfGroups());

        // groups
        assertNotNull(x12.getGroups());
        assertEquals(2, x12.getGroups().size());
        
        // group 1
        // GS*SH*4405197800*999999999*20111206*1045*00*X*004060
        X12Group group1 = x12.getGroups().get(0);
        assertEquals("SH", group1.getFunctionalCodeId());
        assertEquals("4405197800", group1.getApplicationSenderCode());
        assertEquals("999999999", group1.getApplicationReceiverCode());
        assertEquals("20111206", group1.getDate());
        assertEquals("1045", group1.getTime());
        assertEquals("00", group1.getHeaderGroupControlNumber());
        assertEquals("X", group1.getResponsibleAgencyCode());
        assertEquals("004060", group1.getVersion());
        
        List<X12TransactionSet> group1TxList = group1.getTransactions();
        assertNull(group1TxList);

        // group 2
        // GS*SH*4405197800*999999999*20111206*1045*99*X*004060
        X12Group group2 = x12.getGroups().get(1);
        assertEquals("SH", group2.getFunctionalCodeId());
        assertEquals("4405197800", group2.getApplicationSenderCode());
        assertEquals("999999999", group2.getApplicationReceiverCode());
        assertEquals("20111206", group2.getDate());
        assertEquals("1045", group2.getTime());
        assertEquals("99", group2.getHeaderGroupControlNumber());
        assertEquals("X", group2.getResponsibleAgencyCode());
        assertEquals("004060", group2.getVersion());
        
        List<X12TransactionSet> group2TxList = group2.getTransactions();
        assertNull(group2TxList);
    }
    
    /**
     * 
     * check for correct parsing of base document
     * when AAA and BBB parsers  are registered
     * but not the YYZ parser
     */
    public static void assertBaseDocument(StandardX12Document x12) {
        assertNotNull(x12);
        
        // ISA segment
        InterchangeControlEnvelope isa = x12.getInterchangeControlEnvelope();
        assertNotNull(isa);
        assertEquals("01", isa.getAuthorizationInformationQualifier());
        assertEquals("0000000000", isa.getAuthorizationInformation());
        assertEquals("01", isa.getSecurityInformationQualifier());
        assertEquals("0000000000", isa.getSecurityInformation());
        assertEquals("ZZ", isa.getInterchangeIdQualifier());
        assertEquals("ABCDEFGHIJKLMNO", isa.getInterchangeSenderId());
        assertEquals("ZZ", isa.getInterchangeIdQualifierTwo());
        assertEquals("123456789012345", isa.getInterchangeReceiverId());
        assertEquals("101127", isa.getInterchangeDate());
        assertEquals("1719", isa.getInterchangeTime());
        assertEquals("U", isa.getInterchangeControlStandardId());
        assertEquals("00400", isa.getInterchangeControlVersion());
        assertEquals("000000049", isa.getInterchangeControlNumber());
        assertEquals("0", isa.getAcknowledgementRequested());
        assertEquals("P", isa.getUsageIndicator());
        assertEquals(">", isa.getElementSeparator());
        
        // ise trailer
        InterchangeControlEnvelope ise = x12.getInterchangeControlEnvelope();
        assertNotNull(ise);
        assertEquals("000000049", ise.getInterchangeControlNumber());
        assertEquals(new Integer(2), ise.getNumberOfGroups());

        // groups
        assertNotNull(x12.getGroups());
        assertEquals(2, x12.getGroups().size());
        
        // group 1
        // GS*SH*4405197800*999999999*20111206*1045*00*X*004060
        X12Group group1 = x12.getGroups().get(0);
        assertEquals("SH", group1.getFunctionalCodeId());
        assertEquals("4405197800", group1.getApplicationSenderCode());
        assertEquals("999999999", group1.getApplicationReceiverCode());
        assertEquals("20111206", group1.getDate());
        assertEquals("1045", group1.getTime());
        assertEquals("00", group1.getHeaderGroupControlNumber());
        assertEquals("X", group1.getResponsibleAgencyCode());
        assertEquals("004060", group1.getVersion());
        
        List<X12TransactionSet> group1TxList = group1.getTransactions();
        assertNotNull(group1TxList);
        assertEquals(2, group1TxList.size());
        X12TransactionSet tx1 = group1TxList.get(0);
        assertTrue(tx1 instanceof TypeAaaTransactionSet);
        assertEquals("AAA", tx1.getTransactionSetIdentifierCode());
        assertEquals("1", ((TypeAaaTransactionSet)tx1).getAaaOnlyValue());
        X12TransactionSet tx2 = group1TxList.get(1);
        assertTrue(tx2 instanceof TypeBbbTransactionSet);
        assertEquals("BBB", tx2.getTransactionSetIdentifierCode());
        assertEquals("2", ((TypeBbbTransactionSet)tx2).getValue());
        
        // group 2
        // GS*SH*4405197800*999999999*20111206*1045*99*X*004060
        X12Group group2 = x12.getGroups().get(1);
        assertEquals("SH", group2.getFunctionalCodeId());
        assertEquals("4405197800", group2.getApplicationSenderCode());
        assertEquals("999999999", group2.getApplicationReceiverCode());
        assertEquals("20111206", group2.getDate());
        assertEquals("1045", group2.getTime());
        assertEquals("99", group2.getHeaderGroupControlNumber());
        assertEquals("X", group2.getResponsibleAgencyCode());
        assertEquals("004060", group2.getVersion());
        
        List<X12TransactionSet> group2TxList = group2.getTransactions();
        assertNotNull(group2TxList);
        assertEquals(1, group2TxList.size());
        X12TransactionSet tx3 = group2TxList.get(0);
        assertTrue(tx3 instanceof TypeAaaTransactionSet);
        assertEquals("AAA", tx3.getTransactionSetIdentifierCode());
        assertEquals("3", ((TypeAaaTransactionSet)tx3).getAaaOnlyValue());
    }
    
}
