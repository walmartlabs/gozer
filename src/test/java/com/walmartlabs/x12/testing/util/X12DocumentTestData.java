package com.walmartlabs.x12.testing.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

public final class X12DocumentTestData {

    /**
     * read a file and return file contents as a String
     *
     * @param file
     * @return
     */
    public static String readFile(String file) {
        String fileContents = null;
        try {
            fileContents = new String(Files.readAllBytes(Paths.get(file)));
        } catch (IOException e) {
            fail("could not read file for testing: " + e.getMessage());
        }
        return fileContents;
    }

    /**
     * read a file and return file contents as byte array
     *
     * @param file
     * @return
     */
    public static byte[] readFileAsBytes(String file) {
        byte[] fileContents = null;
        try {
            fileContents = Files.readAllBytes(Paths.get(file));
        } catch (IOException e) {
            fail("could not read file for testing: " + e.getMessage());
        }
        return fileContents;
    }

    public X12DocumentTestData() {
        // you can't make me
    }
}
