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

public abstract class X12ParsedLoop extends X12Loop {

    // child loops after parsing
    // when unparsed loops are parsed
    // they should be in specific class
    // but sometimes the order is unspecified
    // and need way to handle that generically
    // (see Tare in ASN 856 for example)
    private List<X12Loop> parsedChildrenLoops;
    
    // all the segments associated with the loop
    // should be parsed as attributes on the subclass
    // of the X12ParsedLoop
    // any unexpected segments will be added to
    // this list
    // (the MAN segment would be parsed on the Tare
    // but the PO4 would be unexpected & unparsed)
    private List<X12Segment> unparsedSegments;
    
    /**
     * helper method to copy the attributes
     * from the unparsed loop to this object
     * @param loop
     */
    public void copyAttributes(X12Loop loop) {
        this.setCode(loop.getCode());
        this.setHierarchicalId(loop.getHierarchicalId());
        this.setParentHierarchicalId(loop.getParentHierarchicalId());
        this.setSegments(loop.getSegments());
    }
    
    /**
     * helper method to add {@link X12Segment} to list
     * @param segment
     */
    public void addUnparsedSegment(X12Segment segment) {
        if (unparsedSegments == null) {
            unparsedSegments = new ArrayList<>();
        }
        unparsedSegments.add(segment);
    }
    
    /**
     * helper method to add {@link X12Loop} to list
     * @param loop
     */
    public void addParsedChildLoop(X12Loop loop) {
        if (parsedChildrenLoops == null) {
            parsedChildrenLoops = new ArrayList<>();
        }
        parsedChildrenLoops.add(loop);
    }
    
    public List<X12Loop> getParsedChildrenLoops() {
        return parsedChildrenLoops;
    }

    public void setParsedChildrenLoops(List<X12Loop> parsedChildrenLoops) {
        this.parsedChildrenLoops = parsedChildrenLoops;
    }

    public List<X12Segment> getUnparsedSegments() {
        return unparsedSegments;
    }

    public void setUnparsedSegments(List<X12Segment> unparsedSegments) {
        this.unparsedSegments = unparsedSegments;
    }

}
