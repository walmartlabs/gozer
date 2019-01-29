package com.walmartlabs.x12.crc;

public interface CyclicRedundancyCheck {
    /**
     * generates a CRC value
     *
     * @param blockOfText
     * @return the CRC value
     * @throws NumberFormatException
     */
    String generateCyclicRedundancyCheck(String blockOfText);

    /**
     * generates a CRC value padded with zeroes at the front if the generated value is less than the specified length
     *
     * @param blockOfText
     * @param minSize
     * @return the CRC value
     * @throws NumberFormatException
     */
    default String generateCyclicRedundancyCheck(String blockOfText, int minSize) {
        String crcValue = this.generateCyclicRedundancyCheck(blockOfText);
        if (crcValue != null && crcValue.length() > 0 && crcValue.length() < minSize) {
            StringBuilder formatter = new StringBuilder().append("%0").append(minSize - crcValue.length()).append("d%s");
            crcValue = String.format(formatter.toString(), 0, crcValue);
        }
        return crcValue;
    }

    /**
     * compare a block of text with a CRC value
     *
     * @param crcValue
     * @param blockOfText
     * @return true if the text has a CRC value that matches the one passed in
     */
    default boolean verifyBlockOfText(String crcValue, String blockOfText) {
        return this.verifyBlockOfText(crcValue, blockOfText, 0);
    }

    /**
     * compare a block of text with a CRC value if the minSize is > 0 it will be used to pad the CRC value generated for the block of text
     *
     * @param crcValue
     * @param blockOfText
     * @param minSize
     * @return true if the text has a CRC value that matches the one passed in
     */
    default boolean verifyBlockOfText(String crcValue, String blockOfText, int minSize) {
        boolean crcMatches = false;

        if (blockOfText != null && blockOfText.length() > 0 && crcValue != null && crcValue.length() > 0) {
            String generatedCrcValue = this.generateCyclicRedundancyCheck(blockOfText, minSize);
            if (crcValue.equalsIgnoreCase(generatedCrcValue.toString())) {
                crcMatches = true;
            }
        }
        return crcMatches;
    }
}
