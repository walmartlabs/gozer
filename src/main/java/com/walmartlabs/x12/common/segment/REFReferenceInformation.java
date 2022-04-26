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

package com.walmartlabs.x12.common.segment;

/**
 *
 * Purpose: Reference Information
 * 
 * Contains 4 data elements, the 4th can hold multiple values and is 
 * usually delimited by a ">" but which can be specified to be 
 * a different character in the ISA segment
 * 
 * REF*PK*1234**BM>4321
 * 
 * This object will hold the entire REF04 value in the 
 * attribute `additionalReferenceIdentification`
 * expecting a separate process to manage parsing it
 * into its parts
 *
 */
public class REFReferenceInformation {

    public static final String IDENTIFIER = "REF";

    // REF01
    private String referenceIdentificationQualifier;

    // REF02
    private String referenceIdentification;
    
    // REF03
    private String description;

    // REF04
    private String additionalReferenceIdentification;

    public String getReferenceIdentificationQualifier() {
        return referenceIdentificationQualifier;
    }

    public void setReferenceIdentificationQualifier(String referenceIdentificationQualifier) {
        this.referenceIdentificationQualifier = referenceIdentificationQualifier;
    }

    public String getReferenceIdentification() {
        return referenceIdentification;
    }

    public void setReferenceIdentification(String referenceIdentification) {
        this.referenceIdentification = referenceIdentification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdditionalReferenceIdentification() {
        return additionalReferenceIdentification;
    }

    public void setAdditionalReferenceIdentification(String additionalReferenceIdentification) {
        this.additionalReferenceIdentification = additionalReferenceIdentification;
    }
    
}
