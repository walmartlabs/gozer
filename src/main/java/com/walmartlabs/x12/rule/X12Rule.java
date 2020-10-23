package com.walmartlabs.x12.rule;

import com.walmartlabs.x12.X12Segment;

import java.util.List;

public interface X12Rule {

    void verify(List<X12Segment> segmentList);

}