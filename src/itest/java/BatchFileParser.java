import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.StandardX12Document;
import com.walmartlabs.x12.standard.StandardX12Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BatchFileParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchFileParser.class);
    
    protected static final StandardX12Parser x12Parser = new StandardX12Parser();

    private static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;
    private static final Charset LATIN_ONE_CHARSET = StandardCharsets.ISO_8859_1;
    private static final Charset MAC_CHARSET = Charset.forName("x-MacRoman");

    protected void verifyArgsAndRun(BatchFileParser bfp, String[] args) throws IOException {
        if (args != null && args.length > 0) {
            // get list of files in input folder
            String inputDirectory = args[0];
            Path inputFolder = Paths.get(inputDirectory);

            if (Files.exists(inputFolder)) {
                bfp.registerTransactionSetParsers();
                bfp.runBatch(inputDirectory);
                
            } else {
                LOGGER.warn("the input folder does not exist");
            }
        } else {
            LOGGER.warn("please provide an input folder as an argument");
        }
    }
    
    private void runBatch(String inputDirectory) throws IOException {
        Path inputFolder = Paths.get(inputDirectory);
        
        Path okFolder = Paths.get(inputDirectory + "/success");
        this.createFolderIfNotExists(okFolder);
        Path rejectFolder = Paths.get(inputDirectory + "/failed");
        this.createFolderIfNotExists(rejectFolder);
        
        this.processDirectory(inputFolder, okFolder, rejectFolder);
    }
    
    private void processDirectory(Path inputFolder, Path okFolder, Path rejectFolder) throws IOException {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);

        try (Stream<Path> files = Files.list(inputFolder)) {
            files.filter(Files::isRegularFile)
            .forEach(sourceFile -> {
                boolean isSuccess = this.parseFile(sourceFile, okFolder, rejectFolder);
                if (isSuccess) {
                    successCount.incrementAndGet();
                } else {
                    failedCount.incrementAndGet();
                }
            });
        }

        LOGGER.info("Parsed files - successful {}, failed {}", successCount, failedCount);
    }

    private boolean parseFile(Path sourceFile, Path okFolder, Path rejectFolder) {
        boolean isSuccess = false;

        try {
            LOGGER.info("parsing file {}", sourceFile.getFileName());
            
            // read the file
            String sourceData = this.readFile(sourceFile);

            // parse the file
            StandardX12Document x12Doc = x12Parser.parse(sourceData);
            
            // check the document
            this.checkDocument(x12Doc, sourceFile.getFileName(),  okFolder);
            
            // copy the file to OK folder
            this.copyFile(sourceFile, okFolder);
            isSuccess = true;
        } catch (X12ParserException e) {
            // copy the file to failed folder
            this.copyFile(sourceFile, rejectFolder);
            // write file w/ parsing fail reason
            this.writeReasonFile(sourceFile, rejectFolder, e.getMessage());
        } catch (UncheckedIOException | IOException e) {
            this.writeReasonFile(sourceFile, rejectFolder, e.getMessage());
        }

        return isSuccess;
    }
    
    private String readFile(Path sourceFile) throws IOException {
        String fileContents = null;
        try {
            fileContents = this.readFile(sourceFile, UTF8_CHARSET);
        } catch (UncheckedIOException e) {
            Throwable t = e.getCause();
            if (t != null && t instanceof MalformedInputException) {
                LOGGER.warn("switching encoding to Latin-1");
                fileContents = this.readFile(sourceFile, LATIN_ONE_CHARSET);
            } else {
                throw e;
            }
        }
        return fileContents;
    }
    
    private String readFile(Path sourceFile, Charset charSet) throws IOException {
        // read the file
        String content = null;
        try (Stream<String> lines = Files.lines(sourceFile, charSet)) {
            content = lines.collect(Collectors.joining(System.lineSeparator()));
        }
        return content;
    }
    
    private void writeReasonFile(Path sourceFile, Path rejectFolder, String errReason) {
        try {
            Path errFile = rejectFolder.resolve(sourceFile.getFileName() + ".reason.txt");
            Files.write(errFile, errReason.getBytes());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
         }
    }
    
    private void copyFile(Path fileToCopy, Path folder) {
        try {
            Path targetFile = folder.resolve(fileToCopy.getFileName());
            Files.copy(fileToCopy, targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
           LOGGER.error(e.getMessage(), e);
        }
    }
    
    private void createFolderIfNotExists(Path folder) throws IOException {
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }
    }
    
    /**
     * override this method 
     * and register parsers
     */
    protected void registerTransactionSetParsers() {
    }
    
    /**
     * override this method 
     * check the results of parsing the document 
     * and write some meta data to a file
     * @param x12Doc
     * @throws X12ParserException to indicate an error in the document
     */
    protected void checkDocument(StandardX12Document x12Doc, Path sourceFileName, Path okFolder) {
    }

}