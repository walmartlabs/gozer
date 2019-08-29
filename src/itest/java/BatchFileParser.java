import com.walmartlabs.x12.exceptions.X12ParserException;
import com.walmartlabs.x12.standard.StandardX12Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BatchFileParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(BatchFileParser.class);
    private static final StandardX12Parser x12Parser = new StandardX12Parser();
    
    public static void main(String[] args) throws IOException {
        if (args != null && args.length > 0) {
            // get list of files in input folder
            String inputDirectory = args[0];
            Path inputFolder = Paths.get(inputDirectory);

            if (Files.exists(inputFolder)) {
                Path okFolder = Paths.get(inputDirectory + "/success");
                createFolderIfNotExists(okFolder);
                Path rejectFolder = Paths.get(inputDirectory + "/failed");
                createFolderIfNotExists(rejectFolder);
                
                processDirectory(inputFolder, okFolder, rejectFolder);
            } else {
                LOGGER.warn("the input folder does not exist");
            }
        } else {
            LOGGER.warn("please provide an input folder as an argument");
        }
    }
        
    private static void processDirectory(Path inputFolder, Path okFolder, Path rejectFolder) throws IOException {
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failedCount = new AtomicInteger(0);
        
        try (Stream<Path> files = Files.list(inputFolder)) {
            files.filter(Files::isRegularFile)
            .forEach(sourceFile -> {
                boolean isSuccess = parseFile(sourceFile, okFolder, rejectFolder);
                if (isSuccess) {
                    successCount.incrementAndGet();
                } else {
                    failedCount.incrementAndGet();
                }
            });
        }
        
        LOGGER.info("Parsed files - successful {}, failed {}", successCount, failedCount);
    }

    private static boolean parseFile(Path sourceFile, Path okFolder, Path rejectFolder) {
        boolean isSuccess = false;
        
        try {
            // read the file
            String sourceData = readFile(sourceFile);
            
            // parse the file
            x12Parser.parse(sourceData);

            // copy the file to OK folder
            copyFile(sourceFile, okFolder);
            isSuccess = true;
        } catch (X12ParserException e) {
            // copy the file to failed folder
            copyFile(sourceFile, rejectFolder);
            // write file w/ parsing fail reason
            writeReasonFile(sourceFile, rejectFolder, e.getMessage());
        } catch (UncheckedIOException | IOException e) {
            writeReasonFile(sourceFile, rejectFolder, e.getMessage());
        }
        
        return isSuccess;
    }

    private static String readFile(Path sourceFile) throws IOException, UncheckedIOException {
        // read the file
        String content = null;
        try (Stream<String> lines = Files.lines(sourceFile)) {
            content = lines.collect(Collectors.joining(System.lineSeparator()));
        }
        return content;
    }
    
    private static void writeReasonFile(Path sourceFile, Path rejectFolder, String errReason) {
        try {
            Path errFile = rejectFolder.resolve(sourceFile.getFileName() + ".reason.txt");
            Files.write(errFile, errReason.getBytes());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
         }
    }
    
    private static void copyFile(Path fileToCopy, Path folder) {
        try {
            Path targetFile = folder.resolve(fileToCopy.getFileName());
            Files.copy(fileToCopy, targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
           LOGGER.error(e.getMessage(), e);
        }
    }
    
    private static void createFolderIfNotExists(Path folder) throws IOException {
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }
    }

}