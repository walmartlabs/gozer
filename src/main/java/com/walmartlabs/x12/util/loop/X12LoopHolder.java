package com.walmartlabs.x12.util.loop;

import com.walmartlabs.x12.exceptions.X12ErrorDetail;
import com.walmartlabs.x12.standard.X12Loop;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class X12LoopHolder {

    private List<X12Loop> loops;
    
    private List<X12ErrorDetail> loopErrors;

    /**
     * helper method to add loop error
     * 
     * @param errorDetail
     */
    public void addX12ErrorDetail(X12ErrorDetail errorDetail) {
        if (CollectionUtils.isEmpty(loopErrors)) {
            loopErrors = new ArrayList<>();
        }
        loopErrors.add(errorDetail);
    }
    
    public List<X12Loop> getLoops() {
        return loops;
    }

    public void setLoops(List<X12Loop> loops) {
        this.loops = loops;
    }

    public List<X12ErrorDetail> getLoopErrors() {
        return loopErrors;
    }

    public void setLoopErrors(List<X12ErrorDetail> loopErrors) {
        this.loopErrors = loopErrors;
    }
    
    
}
