package com.walmartlabs.x12.standard;

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

}
