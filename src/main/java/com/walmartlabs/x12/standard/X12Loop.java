package com.walmartlabs.x12.standard;

import com.walmartlabs.x12.X12Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * models a basic HL loop
 * with support for nesting
 */
public class X12Loop {

    /*
     * HL
     */
    // HL01: hierarchical loop id
    private String hierarchicalId;
    
    // HL02: parent hierarchical loop id
    private String parentHierarchicalId;
    
    // HL03: code
    private String code;
    
    // any segments that are part of this loop
    private List<X12Segment> segments;
    
    // any loops that identified this loop
    // that it was the parent or 
    // related higher level loop
    private List<X12Loop> childLoops;

    public void addSegment(X12Segment segment) {
        if (segments == null) {
            segments = new ArrayList<>();
        }
        segments.add(segment);
    }
    
    public void addLoop(X12Loop loop) {
        if (childLoops == null) {
            childLoops = new ArrayList<>();
        }
        childLoops.add(loop);
    }
    
    public static boolean isLoopWithCode(X12Loop loop, String desiredCode) {
        Optional<String> os =  Optional.ofNullable(loop)
            .map(notNullLoop -> notNullLoop.getCode())
            .filter(Objects::nonNull)
            .filter(code -> code.equals(desiredCode));
        return os.isPresent();
    }
    
    public List<X12Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<X12Segment> segments) {
        this.segments = segments;
    }

    public List<X12Loop> getChildLoops() {
        return childLoops;
    }

    public void setChildLoops(List<X12Loop> childLoops) {
        this.childLoops = childLoops;
    }

    public String getHierarchicalId() {
        return hierarchicalId;
    }

    public void setHierarchicalId(String hierarchicalId) {
        this.hierarchicalId = hierarchicalId;
    }

    public String getParentHierarchicalId() {
        return parentHierarchicalId;
    }

    public void setParentHierarchicalId(String parentHierarchicalId) {
        this.parentHierarchicalId = parentHierarchicalId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
}
