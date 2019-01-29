package com.walmartlabs.x12.asn856;

import com.walmartlabs.x12.X12Parser;
import com.walmartlabs.x12.exceptions.X12ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * ASN 856 is the Advance Shipping Notice
 * Used to communicate the contents of a shipment prior to arriving at the facility
 * where the contents will be delivered.
 *
 */
public class DefaultAsn856Parser implements X12Parser<Asn856> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAsn856Parser.class);

    /**
     * parse the ASN 856 transmission into
     * a representative Java object
     *
     * @return {@link Asn856}
     * @throws X12ParserException
     */
    @Override
    public Asn856 parse(String sourceData) {
        Asn856 asn856 = null;

        if (!StringUtils.isEmpty(sourceData)) {
            asn856 = new Asn856();
            List<String> segmentLines = this.splitSourceDataIntoSegments(sourceData);

            // TODO: implement the parser
        }

        return asn856;
    }

}
