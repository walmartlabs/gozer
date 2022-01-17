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

import com.walmartlabs.x12.X12Segment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * models a basic HL loop with support for nesting
 */
public class X12Loop {

    public static final String HIERARCHY_LOOP_ID = "HL";

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

    /**
     * helper method to add {@link X12Segment} to list
     * @param segment
     */
    public void addSegment(X12Segment segment) {
        if (segments == null) {
            segments = new ArrayList<>();
        }
        segments.add(segment);
    }

    /**
     * helper method to add {@link X12Loop} to list
     * @param loop
     */
    public void addLoop(X12Loop loop) {
        if (childLoops == null) {
            childLoops = new ArrayList<>();
        }
        childLoops.add(loop);
    }

    /**
     * check if the loop has a specific code
     * @param loop
     * @param desiredCode
     */
    public static boolean isLoopWithCode(X12Loop loop, String desiredCode) {
        Optional<String> os = Optional.ofNullable(loop).map(notNullLoop -> notNullLoop.getCode()).filter(
            Objects::nonNull).filter(code -> code.equals(desiredCode));
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
