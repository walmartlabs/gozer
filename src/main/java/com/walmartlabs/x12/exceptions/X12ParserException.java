package com.walmartlabs.x12.exceptions;

/**
 * ParserException
 */
public class X12ParserException extends RuntimeException {
    private X12ErrorDetail errorDetail;

    public X12ParserException(String msg) {
        super(msg);
    }

    public X12ParserException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public X12ParserException(Throwable cause) {
        super(cause);
    }

    public X12ParserException(String msg, X12ErrorDetail error) {
        super(msg);
        this.errorDetail = error;
    }

    public X12ParserException(X12ErrorDetail error) {
        super(error != null ? error.getMessage() : "");
        this.errorDetail = error;
    }

    public X12ErrorDetail getErrorDetail() {
        return errorDetail;
    }

}
