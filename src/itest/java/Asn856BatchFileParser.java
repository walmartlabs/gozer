import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.standard.StandardX12Document;
import com.walmartlabs.x12.standard.X12Group;
import com.walmartlabs.x12.standard.txset.asn856.AsnTransactionSet;
import com.walmartlabs.x12.standard.txset.asn856.DefaultAsn856TransactionSetParser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class Asn856BatchFileParser extends BatchFileParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(Asn856BatchFileParser.class);
    
    private Path targetFile;
    
    public static void main(String[] args) throws IOException {
        BatchFileParser bfp = new Asn856BatchFileParser();
        bfp.verifyArgsAndRun(bfp, args);
    }
    
    @Override
    protected void registerTransactionSetParsers() {
        LOGGER.info("registering ASN TransactionSet Parser");
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
            sb.append("\r\n");
            sb.append("Transaction: 856");
            sb.append("\r\n");
            
            AsnTransactionSet asnTx = (AsnTransactionSet) transaction;
            
            sb.append("Document Number (BSN02):");
            sb.append(asnTx.getShipmentIdentification());
            sb.append("\r\n");
            
            sb.append("Document Date (BSN03):");
            sb.append(asnTx.getShipmentDate());
            sb.append("\r\n");
            
            List<X12ErrorDetail> loopErrors = asnTx.getLoopingErrors();
            if (loopErrors != null) {
                for(X12ErrorDetail error: loopErrors) {
                    sb.append(error.getIssueText());
                    sb.append("\r\n");
                    if (StringUtils.isNotEmpty(error.getInvalidValue())) {
                        sb.append(error.getInvalidValue());
                        sb.append("\r\n");
                    }
                }
            } else {
                sb.append("No looping errors");
                sb.append("\r\n");
            }
                
            LOGGER.info(sb.toString());
                
            this.writeTransactionToFile(sb);
            
        }
            
    }
}
