package com.walmartlabs.x12.util;

public interface Checksum {
    /**
     * generates a checksum digit from the number
     * @param number
     * @return
     */
    String generateChecksumDigit(String number);
}
