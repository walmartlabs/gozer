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
package com.walmartlabs.x12.standard;

/**
 * The Group Header segment (GS) contains the functional code
 *
 * These values were based on the following document:
 * https://www-01.ibm.com/support/docview.wss?uid=swg21548276
 */
public enum FunctionalGroupCode {
   DX("894"),//also 895
   PO("850"),
   PR("855"),
   SH("856"),
   BS("857"),
   PC("860"),
   CA("865"),
   IN("810"),
   AG("824"),
   FS("997");

   private String documentType;

    FunctionalGroupCode(String docType) {
        this.documentType = docType;
    }

    public String getDocumentType() {
        return this.documentType;
    }
}
