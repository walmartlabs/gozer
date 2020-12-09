package com.walmartlabs.x12.common.segment;

/**
 * 
 * Purpose: To describe a product or process in coded or free-form format
 *
 */
public class PIDProductIdentification {

    public static final String IDENTIFIER = "PID";

    // PID01
    private String itemDescriptionType;

    // PID02
    private String characteristicCode;
    
    // PID05
    private String description;

    public String getItemDescriptionType() {
        return itemDescriptionType;
    }

    public void setItemDescriptionType(String itemDescriptionType) {
        this.itemDescriptionType = itemDescriptionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCharacteristicCode() {
        return characteristicCode;
    }

    public void setCharacteristicCode(String characteristicCode) {
        this.characteristicCode = characteristicCode;
    }

}
