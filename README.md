# EDI X12 Standard Parsing Library

The EDI X12 Standard provides a uniform way for companies to exchange information across different sectors. 

This open source library, available through @WalmartLabs, provides Java based classes that can parse various X12 file formats into a representative Java object model. It's primary focus is on those formats related to the Supply Chain sector.

## Parsers

| X12     	| Name                        	| Description 	|
|---------	|-----------------------------	|-------------	|
| DEX 894 	| Delivery/Return Base Record 	| x           	|
| TBD     	| X                           	| x           	|
|         	|                             	|             	|

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

## More Information

[About EDI Standards](http://ediacademy.com/blog/edi-x12-standard/)

[EDI formats](https://www.spscommerce.com/resources/edi-documents-transactions/)

## Licensing
Copyright 2018 WalmartLabs

This software is licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0)