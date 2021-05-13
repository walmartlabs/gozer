import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.asn856.AsnTransactionSet;
import com.walmartlabs.x12.asn856.DefaultAsn856TransactionSetParser;
import com.walmartlabs.x12.asn856.Shipment;
import com.walmartlabs.x12.common.segment.N1PartyIdentification;
import com.walmartlabs.x12.standard.StandardX12Document;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.X12Loop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class Asn856BatchFileParser extends BatchFileParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(Asn856BatchFileParser.class);
    
    private Path targetFile;
    
    
    protected static BatchFileParser createBatchFileParser() {
        return new Asn856BatchFileParser();
    }
    
    @Override
    protected void registerTransactionSetParsers() {
        x12Parser.registerTransactionSetParser(new DefaultAsn856TransactionSetParser());
    }
    
    @Override
    protected void checkDocument(StandardX12Document x12Doc, Path sourceFileName, Path okFolder) {
        this.createCheckDocumentFile(sourceFileName, okFolder);
        this.examineDocument(x12Doc);
    }
    
    private void createCheckDocumentFile(Path sourceFileName, Path okFolder) {
        // set the name of the file
        this.targetFile = okFolder.resolve(sourceFileName + ".transactions.txt");
        
        // open it for writing
        try {
            Files.write(targetFile, "".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            
        } catch (Exception e) {
           LOGGER.error(e.getMessage(), e);
        }
        
    }
    
    private void writeTransactionToFile(StringBuilder sb) {
        try {
            
            Files.write(targetFile, sb.toString().getBytes(), StandardOpenOption.APPEND);
            
        } catch (Exception e) {
           LOGGER.error(e.getMessage(), e);
        }        
    }
    
    private void examineDocument(StandardX12Document x12Doc) {
        List<X12Group> groups = x12Doc.getGroups();
        if (!CollectionUtils.isEmpty(groups)) {
            groups.forEach(this::checkGroup);
        }
    }
    
    private void checkGroup(X12Group group) {
        List<X12TransactionSet> transactions = group.getTransactions();
        if (!CollectionUtils.isEmpty(transactions)) {
            transactions.forEach(this::checkTransaction);
        }
    }
    
    private void checkTransaction(X12TransactionSet transaction) {
        if ("856".equals(transaction.getTransactionSetIdentifierCode())) {
            StringBuilder sb = new StringBuilder();
            sb.append("Transaction: 856");
            sb.append("\r\n");
            
            AsnTransactionSet asnTx = (AsnTransactionSet) transaction;
            
            sb.append("Document Number (BSN02):");
            sb.append(asnTx.getShipmentIdentification());
            sb.append("\r\n");
            
            sb.append("Document Date (BSN03):");
            sb.append(asnTx.getShipmentDate());
            sb.append("\r\n");
                
            LOGGER.info(sb.toString());
                
            Shipment shipment = asnTx.getShipment();
            List<N1PartyIdentification> n1List = shipment.getN1PartyIdenfications();
            if (!CollectionUtils.isEmpty(n1List)) {
                
                List<N1PartyIdentification> stList = n1List.stream()
                    .filter(n1 -> "ST".equals(n1.getEntityIdentifierCode()))
                    .collect(Collectors.toList());
                
                if (!CollectionUtils.isEmpty(stList)) {
                    N1PartyIdentification n1ShipTo= stList.get(0);
                    sb.append("ST: (N103 - N104):");
                    sb.append(n1ShipTo.getIdentificationCodeQualifier());
                    sb.append("-");
                    sb.append(n1ShipTo.getIdentificationCode());
                    sb.append("\r\n");
                } else {
                    sb.append("ST: (N103 - N104): missing");
                }

            }

            List<X12Loop> shipmentChildLoops = shipment.getParsedChildrenLoops();
                
            this.writeTransactionToFile(sb);
            
        }
            
    }
}
