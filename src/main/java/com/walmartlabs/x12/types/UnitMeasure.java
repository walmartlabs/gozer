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
package com.walmartlabs.x12.types;

/**
 * unit of measure code set of values
 * used on DEX G8303
 * used on ASN LIN08
 */
public enum UnitMeasure {
    BX("BOX"),
    CA("CASE"),
    CT("CARTON"),
    EA("EACH"),
    DZ("DOZEN"),
    GA("GALLON"),
    KE("KEG"),
    KG("KILOGRAM"),
    LB("POUND"),
    PK("PACKAGE"),
    PL("PALLET"),
    TK("TANK"),
    UN("UNIT"),
    UNKNOWN("UNKNOWN");

    private String description;

    private UnitMeasure(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * Convert the unit of measure value to an enum
     * @param uom
     * @return
     */
    public static UnitMeasure convertUnitMeasure(String uom) {
        if (uom == null) {
            return null;
        } else {
            UnitMeasure returnEnum = UnitMeasure.UNKNOWN;

            try {
                returnEnum = UnitMeasure.valueOf(uom);
            } catch (Exception e) {
                // illegal value so returning UNKNOWN
            }

            return returnEnum;
        }
    }

}
