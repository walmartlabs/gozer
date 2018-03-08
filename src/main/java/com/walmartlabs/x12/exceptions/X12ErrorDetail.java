package com.walmartlabs.x12.exceptions;

public class X12ErrorDetail {
    private String segmentId;
    private String elementId;
    private Integer lineNumber;
    private String message;

    public X12ErrorDetail(String segmentId, String elementId, String msg) {
        this.segmentId = segmentId;
        this.elementId = elementId;
        this.message = msg;
    }

    public X12ErrorDetail(String segmentId, String elementId, Integer lineNumber, String msg) {
        this.segmentId = segmentId;
        this.elementId = elementId;
        this.lineNumber = lineNumber;
        this.message = msg;
    }

    public String getSegmentId() {
        return segmentId;
    }
    public String getElementId() {
        return elementId;
    }
    public Integer getLineNumber() {
        return lineNumber;
    }
    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DEX error:");
        sb.append("segmentId:").append(segmentId);
        sb.append("elementId:").append(elementId);
        sb.append("line number:").append(lineNumber);
        sb.append("message:").append(message);
        return sb.toString();
    }

}
