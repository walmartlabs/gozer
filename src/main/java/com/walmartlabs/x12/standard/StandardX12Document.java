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

import com.walmartlabs.x12.X12Document;

import java.util.ArrayList;
import java.util.List;

public final class StandardX12Document implements X12Document {

    private InterchangeControlEnvelope interchangeControlEnvelope;
    private List<X12Group> groups;

    public void addGroupHeader(X12Group group) {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        groups.add(group);
    }

    public void setInterchangeControlEnvelope(InterchangeControlEnvelope isa) {
        this.interchangeControlEnvelope = isa;
    }

    public InterchangeControlEnvelope getInterchangeControlEnvelope() {
        return interchangeControlEnvelope;
    }

    public List<X12Group> getGroups() {
        return groups;
    }

    public void setGroups(List<X12Group> groups) {
        this.groups = groups;
    }

}
