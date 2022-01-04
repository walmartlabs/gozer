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

package sample.yyz;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.X12TransactionSet;
import com.walmartlabs.x12.standard.TransactionSetParser;
import com.walmartlabs.x12.standard.X12Group;

import java.util.List;

public class YyzTransactionSetParser implements TransactionSetParser {

    @Override
    public X12TransactionSet parseTransactionSet(List<X12Segment> txLines, X12Group x12Group) {
        TypeYyzTransactionSet tx = null;
        if (txLines != null && !txLines.isEmpty()) {
            String type = txLines.get(0).getElement(1);
            if ("YYZ".equals(type)) {
                tx = new TypeYyzTransactionSet();
                tx.setTransactionSetIdentifierCode(type);
                X12Segment nextLine = txLines.get(1);
                if (nextLine != null) {
                    tx.setValue(nextLine.getElement(1));
                }
            }
        }
        return tx;
    }
}