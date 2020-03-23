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
 * Purpose: To identify a party by type of organization, name, and code
 *
 */
public class N1PartyIdentification {

    public static final String IDENTIFIER = "N1";

    // N101
    private String entityIdentifierCode;
    // N102
    private String name;
    // N103
    private String identificationCodeQualifier;
    // N104
    private String identificationCode;

    /*
     * N3 Party Identification
     */
    private N3PartyLocation n3;

    /*
     * N4 Party Geographic Identification
     */
    private N4GeographicLocation n4;

    public String getEntityIdentifierCode() {
        return entityIdentifierCode;
    }

    public void setEntityIdentifierCode(String entityIdentifierCode) {
        this.entityIdentifierCode = entityIdentifierCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentificationCodeQualifier() {
        return identificationCodeQualifier;
    }

    public void setIdentificationCodeQualifier(String identificationCodeQualifier) {
        this.identificationCodeQualifier = identificationCodeQualifier;
    }

    public String getIdentificationCode() {
        return identificationCode;
    }

    public void setIdentificationCode(String identificationCode) {
        this.identificationCode = identificationCode;
    }

    public N3PartyLocation getN3() {
        return n3;
    }

    public void setN3(N3PartyLocation n3) {
        this.n3 = n3;
    }

    public N4GeographicLocation getN4() {
        return n4;
    }

    public void setN4(N4GeographicLocation n4) {
        this.n4 = n4;
    }

}
