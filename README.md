# EDI X12 Standard Parsing Library

The EDI X12 Standard provides a uniform way for companies to exchange information across different sectors. 

This open source library, available through @WalmartLabs, provides Java based classes that can parse various X12 file formats into a representative Java object model. It's primary focus is on those formats related to the Supply Chain sector.

## Supported X12 Format Parsers

| X12     	| Name                        	| Description 	 | X12 Version(s) |
|---------	|-----------------------------	|-------------	 |--------------  |
| DEX 894 	| Delivery/Return Base Record 	| DSD deliveries | 4010,5010      |
|         	|                             	|             	 |                |

## Getting Started

Each X12 format that is supported will have a Java interface for both a parser and a validator. The parser will be responsible for parsing the message in the given format to the representative Java object model (POJO). The validator will be responsible for providing any validations applying them to the values stored in the POJO that the parser creates.

If the X12 message is invalid and can't be parsed the parser is expected to throw an `X12ParserException`.

```java

String dexMessage = ...
DefaultDex894Parser dexParser = new DefaultDex894Parser();
DefaultDex894Validator dexValidator = new DefaultDex894Validator();

Dex894 dex = dexParser.parse(dexMessage);
Set<X12ErrorDetail> errorSet = dexValidator.validate(dex);

```

After parsing and validating an X12 formatted message, various utilities can be used for post-processing the data. 
For example 

```java
// get an item from the DEX object 
// that was returned from the parser
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
