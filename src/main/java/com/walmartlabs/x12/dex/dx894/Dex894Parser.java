package com.walmartlabs.x12.dex.dx894;

import com.walmartlabs.x12.exceptions.X12ParserException;

public interface Dex894Parser {

    public static final String APPLICATION_HEADER_ID = "DXS";
    public static final String APPLICATION_TRAILER_ID = "DXE";
    public static final String TRANSACTION_SET_HEADER_ID = "ST";
    public static final String TRANSACTION_SET_TRAILER_ID = "SE";
    public static final String G82_ID = "G82";
    public static final String G83_ID = "G83";
    public static final String G72_ID = "G72";
    public static final String G84_ID = "G84";
    public static final String G85_ID = "G85";
    public static final String G86_ID = "G86";
    public static final String LOOP_HEADER_ID = "LS";
    public static final String LOOP_TRAILER_ID = "LE";

    /**
     * parse the DEX 894 transmission into
     * a representative Java object
     *
     * @return {@link Dex894}
     * @throws X12ParserException
     */
    Dex894 parse(String sourceData);
}
