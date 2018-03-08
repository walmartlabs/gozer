package com.walmartlabs.x12.dex.dx894;

/**
 * represents the G8305 - product/service id qualifier values
 *
 */
public enum ProductQualifier {
    DI("DEPOSIT ITEM NUMBER"),
    EN("EAN/UCC-13"),
    EO("EAN/UCC-8"),
    NR("NONRESALABLE ITEM"),
    UK("EAN/UCC-14"),
    UP("EAN/UCC-12"),
    VN("VENDOR ITEM NUMBER"),
    UNKNOWN("UNKNOWN");

    private String description;

    private ProductQualifier(String desc) {
        this.description = desc;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * Convert the unit of measure value to an enum
     * @param productQualifierCode
     * @return
     */
    public static ProductQualifier convertyProductQualifier(String productQualifierCode) {
        ProductQualifier returnEnum = ProductQualifier.UNKNOWN;

        try {
            returnEnum = ProductQualifier.valueOf(productQualifierCode);
        } catch (Exception e) {
            // illegal value so returning UNKNOWN
        }

        return returnEnum;
    }

}
