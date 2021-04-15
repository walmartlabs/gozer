/**
Copyright (c) 2018-present, Walmart, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.walmartlabs.x12.asn856.segment;

/**
 * 
 * Purpose: To specify the physical qualities, packaging, weights, and
 * dimensions relating to the item
 */
public class PO4ItemPhysicalDetail {

    public static final String IDENTIFIER = "PO4";

    // PO410
    private String length;
    
    // PO411
    private String width;
    
    // PO412
    private String height;
    
    // PO413
    private String unitOfMeasurement;
    
    // PO416
    private String assignedIdentification;

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public void setUnitOfMeasurement(String uom) {
        this.unitOfMeasurement = uom;
    }

    public String getAssignedIdentification() {
        return assignedIdentification;
    }

    public void setAssignedIdentification(String assignedIdentification) {
        this.assignedIdentification = assignedIdentification;
    }
    
}
