package com.walmartlabs.x12.dex.dx894;

/**
 * represents the G8303 - unit of measure code set of values
 *
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
        UnitMeasure returnEnum = UnitMeasure.UNKNOWN;

        try {
            returnEnum = UnitMeasure.valueOf(uom);
        } catch (Exception e) {
            // illegal value so returning UNKNOWN
        }

        return returnEnum;
    }

}
