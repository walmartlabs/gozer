package com.walmartlabs.x12.checksum;

public interface CyclicRedundancyCheck {
    /**
     * generates a CRC value
     *
     * @param blockOfText
     * @return the CRC value
     * @throws NumberFormatException
     */
    String generateCyclicRedundancyCheck(String blockOfText);

    default boolean verifyBlockOfText(String crcValue, String blockOfText) {
        boolean crcMatches = false;

        if (blockOfText != null && blockOfText.length() > 0 &&
                crcValue != null && crcValue.length() > 0) {

            String generatedCrcValue = this.generateCyclicRedundancyCheck(blockOfText);
            if (crcValue.equalsIgnoreCase(generatedCrcValue)) {
                crcMatches = true;
            }
        }
        return crcMatches;
    }
}
