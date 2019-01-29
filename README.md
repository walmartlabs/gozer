# EDI X12 Standard Parsing Library

The EDI X12 Standard provides a uniform way for companies to exchange information across different sectors. 

This open source library, available through @WalmartLabs, provides Java based classes that can parse various X12 file formats into a representative Java object model. It's primary focus is on those formats related to the Supply Chain sector.

## Supported X12 Format Parsers

| X12     	| Name                             	  | Description 	  | X12 Version(s) |
|---------	|-----------------------------------   |-------------	  |--------------  |
| DEX 894 	| Delivery/Return Base Record 	      | DSD deliveries | 4010,5010      |
| ASN 856   	| Ship Notice/Manifest Transaction Set |             	  | Under Consideration |

## Basic Design Approach

Gozer seeks to provide more than a generic X12 parsing capability that turns an EDI X12 message into a list of segments and elements. Gozer hopes to provide a library of easy to use classes, that can transform the parsed message into a POJO that corresponds with a specific X12 format. Attributes are labeled with their business names rather than the more cryptic segement identifiers. It further provides basic validation capabilities as defined in the EDI X12 manuals for each format.   

## Why the name Gozer

The Agile development team at @WalmartLabs that is responsible for the design and development of "inbound processing" is called the GhostBusters. Inbound Processing covers the broad array of micro-services involved with moving and receiving merchandise between locations, including  the supplier to the distribution center, the supplier to the store (DSD), and the distribution center to the store.  

## Getting Started: Using the Parsers and Validators

Each X12 format that is supported will have a Java implementation for both the `X12Parser` and `X12Validator`. The parser will be responsible for parsing the message in the given format to the representative Java object model (POJO). The validator will be responsible for providing validations applying them to the values stored in the POJO that the parser creates.

If the X12 message is invalid and can't be parsed the parser is expected to throw an `X12ParserException`.
The validator will not throw an exception under normal circumstances. Instead any validation errors will be returned in a `Set` of `X12ErrorDetail` objects.

```java

String x12Message = ...
X12Parser dexParser = new DefaultDex894Parser();
X12Validator dexValidator = new DefaultDex894Validator();

X12Document x12 = dexParser.parse(dexMessage);
Set<X12ErrorDetail> errorSet = dexValidator.validate(x12);

```

After parsing and validating an X12 formatted message, various utilities can be used for post-processing the data. 
For example:

```java
// cast the generic X12 document to 
// the specific format POJO class
// then get an item from the DEX object 
// that was returned from the parser
Dex894 dex = (Dex894)x12;
Dex894Item dexItem = dex.getItems().get(0);

// convert the UPC in G8304 to a 14 digit GTIN
String itf14 = util.convertRetailNumberToItf14(dexItem.getUpc());

```

## More Information

[About EDI Standards](http://ediacademy.com/blog/edi-x12-standard/)

[EDI formats](https://www.spscommerce.com/resources/edi-documents-transactions/)

[GTIN formats](https://www.gtin.info/)

[Converting between GTIN formats](https://www.free-barcode-generator.net/ean-14/)

[ITF-14 format](https://www.free-barcode-generator.net/itf-14/)

## Build and Release

```
mvn clean install
```

See [walmartlabs-pom for more information](https://github.com/walmartlabs/walmartlabs-pom)

## Licensing

Copyright 2011-present Walmart Inc.

This software is licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0)

Also see LICENSE file.
