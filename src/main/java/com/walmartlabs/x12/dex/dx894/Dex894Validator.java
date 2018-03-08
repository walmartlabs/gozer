package com.walmartlabs.x12.dex.dx894;

import com.walmartlabs.x12.exceptions.X12ErrorDetail;

import java.util.Set;

public interface Dex894Validator {
    /**
     * validate the DEX 894 transmission
     *
     * @return Set of error details ({@link X12ErrorDetail}
     */
    Set<X12ErrorDetail> validate(Dex894 dex);
}
