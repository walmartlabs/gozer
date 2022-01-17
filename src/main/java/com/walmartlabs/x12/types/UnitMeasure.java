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
 * unit of measure code set of values for GS1
 */
public enum UnitMeasure {
    BX("BOX"),
    CA("CASE"),
    CT("CARTON"),
    CH("CONTAINER"),
    EA("EACH"),
    PK("PACKAGE"),
    PL("PALLET"),
    SP("SHELF PACKAGE"),
    TK("TANK"),
    UN("UNIT"),
    CM("CENTIMETER"),
    IN("INCH"),
    GA("GALLON"),
    QT("QUART"),
    PT("PINT"),
    CU("CUP"),
    FO("FLUID OUNCE"),
    Y2("TABLESPOON"),
    Y3("TEASPOON"),
    LT("LITER"),
    ML("MILLILITER"),
    UW("MILLIEQUIVALENT"),
    AM("AMPOULE"),
    AR("SUPPOSITORY"),
    AV("CAPSULE"),
    BO("BOTTLE"),
    DF("DRAM"),
    DZ("DOZEN"),
    KE("KEG"),
    GS("GROSS"),
    GX("GRAIN"),
    KG("KILOGRAM"),
    MC("MICROGRAM"),
    ME("MILLIGRAM"),
    KT("KIT"),
    LB("POUND"),
    N9("CARTRIDGE NEEDLE"),
    SZ("SYRINGE"),
    U2("TABLET"),
    VI("VIAL"),
    X4("DROP"),
    P1("PERCENT"),
    UNKNOWN("UNKNOWN");

    private String description;

    private UnitMeasure(String desc) {
        this.description = desc;
    }

    private void setDescription(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * Convert the code to an enum
     *
     * @param code
     * @return
     */
    public static UnitMeasure convert(String code) {
        if (code == null) {
            return null;
        } else {
            UnitMeasure returnEnum = UnitMeasure.UNKNOWN;
            returnEnum.setDescription(code);

            try {
                returnEnum = UnitMeasure.valueOf(code);
            } catch (Exception e) {
                // illegal value so returning UNKNOWN
            }

            return returnEnum;
        }
    }

}
