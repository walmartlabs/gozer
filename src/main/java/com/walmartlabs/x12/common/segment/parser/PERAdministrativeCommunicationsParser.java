package com.walmartlabs.x12.common.segment.parser;

import com.walmartlabs.x12.X12Segment;
import com.walmartlabs.x12.common.segment.PERAdministrativeCommunication;

import java.util.ArrayList;
import java.util.List;

public final class PERAdministrativeCommunicationsParser {

    /**
     * parse the segment
     *
     * @param segment
     * @return
     */
    public static List<PERAdministrativeCommunication> parse(X12Segment segment) {
        List<PERAdministrativeCommunication> perList = new ArrayList<>();

        if (segment != null) {
            String segmentIdentifier = segment.getIdentifier();
            if (PERAdministrativeCommunication.IDENTIFIER.equals(segmentIdentifier)) {
                int size = segment.segmentSize();
                if (size > 2) {
                    String functionCode = segment.getElement(1);
                    String freeFormName = segment.getElement(2);
                    for (int i = 4; i < size; i += 2) {
                        PERAdministrativeCommunication per = new PERAdministrativeCommunication();
                        per.setContactFunctionCode(functionCode);
                        per.setFreeFormName(freeFormName);
                        per.setCommunicationNumberQualifier(segment.getElement(i - 1));
                        per.setCommunicationNumber(segment.getElement(i));

                        perList.add(per);
                    }
                }
            }
        }
        return perList;
    }

    private PERAdministrativeCommunicationsParser() {
        // you can't make me
    }
}