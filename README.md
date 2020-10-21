[![Build Status](https://github.com/walmartlabs/gozer/workflows/Java CI with Maven/badge.svg)](https://github.com/walmartlabs/gozer/actions)

[![Build Status](https://travis-ci.org/walmartlabs/gozer.svg?branch=master)](https://travis-ci.org/walmartlabs/gozer)

[![codecov](https://codecov.io/gh/walmartlabs/gozer/branch/master/graph/badge.svg)](https://codecov.io/gh/walmartlabs/gozer)

# EDI X12 Standard Parsing Library

The EDI X12 Standard provides a uniform way for companies to exchange information across different sectors. 

This open source library, available through @WalmartLabs, provides Java based classes that can parse various X12 file formats into a representative Java object model. It's primary focus is on those formats related to the Supply Chain sector.

## Supported X12 Format Parsers

| X12     	| Name                             	  | Description 	  | X12 Version(s) |
|---------	|-----------------------------------   |-------------	  |--------------  |
| DEX 894 	| Delivery/Return Base Record 	      | DSD deliveries | 4010,5010      |
| ASN 856   	| Ship Notice/Manifest Transaction Set | Advance Ship Notice | 5010 |
| PO 850   	| Purchase Order Transaction Set | Purchase Order | WIP |


## Why the name Gozer

The Agile development team at @WalmartLabs that is responsible for the design and development of "inbound processing" is called the GhostBusters. Inbound Processing covers the broad array of micro-services involved with moving and receiving merchandise between locations, including  the supplier to the distribution center, the supplier to the store (DSD), and the distribution center to the store. 

## Basic Design Approach

Gozer seeks to provide more than a generic X12 parsing capability that turns an EDI X12 message into a list of segments and elements. Gozer hopes to provide a library of easy to use classes, that can transform the parsed message into a set of POJOs that corresponds with a specific X12 format. Attributes are labeled with their business names rather than the more cryptic segment identifiers. It further provides basic validation capabilities as defined in the EDI X12 manuals for each format. 

In addition, interfaces and various extension points allow users to customize some of the parsing and validation that is offered in Gozer. 

### Practical Approach to Parsing
Each X12 format that is supported will have a Java implementation for both the `X12Parser` and `X12Validator`. The parser will be responsible for parsing the message in the given format to the representative Java object model (POJO). The validator will be responsible for applying various checks against the values stored in the POJO that the parser creates.

The Gozer parsers, that implement `X12Parser`, are designed to parse the EDI X12 message. Successfully parsing a document would require the message to be well-formed. The segments must have the correct segment identifiers, which must be nested correctly and appear in the proper order.

If the X12 message is invalid and can't be parsed the parser will throw an `X12ParserException`. The parser, however, does not attempt to validate the values within the segment elements (other than segment identifiers). Rather it will be as loose as possible regarding validation during the parsing of different versions of the EDI X12 message. 

After parsing, it is considered the responsibility of the consumer to perform any additional validations on the data that was provided in various segments and elements. That can be done with the validation implementation provided or through custom code. 

In Gozer, the validator will not throw an exception under normal circumstances. Instead any validation errors will be returned in a `Set` of `X12ErrorDetail` objects.

## Getting Started: The X12Document in Gozer
Most EDI/X12 messages adhere to a common format. Each message will have an envelope that contains a set  of groups. Each group will contain a set of transaction sets. 

```
ISA*...
	GS*...*12345*X*005010
		ST*856*00001
			...
		SE*100*00001
	GE*1*12345
ISE*...
```

These types of messages can be parsed using the `StandardX12Parser`. This will return a `StandardX12Document`. 

Valid, well-formed messages will be wrapped in an interchange control envelope. This envelope will require the first segment to start with `ISA` and the last segment to end with an `ISE`. 

```
ISA*...
	...
ISE*...
```

The parser will place the elements found in the interchange control envelope in the object `InterchangeControlEnvelope`.

Inside the interchange control envelope, the message can contain one or more groups. Each group will start with a `GS` segment line and end with the first occurrence of a segment that has the identifier `GE`. 

```
ISA*...
	GS*...*12345*X*005010
		...
	GE*1*12345
ISE*...
```

The `StandardX12Document` will hold a list of `X12Group`s. Each instance in the list will map to a group in the message. 

Inside each group, the message can contain one or more transaction sets. Each transaction set will start with an `ST` segment line and end with the first occurrence of a segment that has the identifier `SE`. 

```
ISA*...
	GS*...*12345*X*005010
		ST*856*00001
			...
		SE*100*00001
	GE*1*12345
ISE*...
```

The attributes in each transaction set will be stored in an object that implements the `X12TransactionSet` interface (more on that later). All of the transaction sets for a particular a group will be placed in the corresponding `X12Group` as a list. 

Therefore this EDI message:

```
ISA*...
	GS*...*12345*X*005010
		ST*856*00001
			BSN*00*001*20190823*2112*0001
		SE*100*00001
		ST*856*00002
			BSN*00*002*20190823*2112*0001
		SE*100*00002
	GE*1*12345
	GS*...*12346*X*005010
		ST*856*00003
			BSN*00*003*20190823*2112*0001
		SE*100*00003
	GE*2*12346	
ISE*...
```

would be stored in a `StandardX12Document` as follows:
* One `InterchangeControlEnvelope`
* list with 2 `X12Group` objects
* the first `X12Group` would have a list with 2  `X12TransactionSet`s
* the second `X12Group` would have a list with 1  `X12TransactionSet`
	

### Handling Transaction Sets
Each transaction set represents a different document type that can be sent. For example the ASN (856) or a Purchase Order (850). Each of these transaction sets is comprised of a variety of different segments, therefore each requires its own parser that extends the `AbstractTransactionSetParserChainable`. 

The implementation will be passed all of the segment lines for the transaction set and is expected to return a POJO that extends `AbstractX12TransactionSet` and represents the document.

The transaction set parser must be registered with the parser. 

```java

String x12Message = ...
StandardX12Parser x12Parser = new StandardX12Parser();
x12Parser.registerTransactionSetParser(new DefaultAsn856TransactionSetParser());

StandardX12Document x12Doc = x12Parser.parse(x12Message);
```

By default, any transaction set that does not have a registered parser will be ignored. 
When it is important to process a transaction set that has no parser a custom class that implements
`UnhandledTransactionSet` can be registered. 

```java


StandardX12Parser x12Parser = new StandardX12Parser();
x12Parser.registerTransactionSetParser(new DefaultAsn856TransactionSetParser());
x12Parser.registerUnhandledTransactionSet(new DefaultUnhandledTransactionSet());
```

Look at the [Sample](https://github.com/walmartlabs/gozer/tree/master/src/test/java/sample) to see a  very simplified approach see how  to register a transaction set parser and using them within the `StandardX12Parser`

### Working with an X12Document after parsing
After parsing and validating an X12 formatted message, an application can start to use the information stored in the POJOs. Due to the flexibility of the transaction sets and the variability of loop sequencing, it can still be difficult to access some values. 

How  to find the transaction set in the document:

```java
StandardX12Parser x12Parser = new StandardX12Parser();
x12Parser.registerTransactionSetParser(new DefaultAsn856TransactionSetParser());

StandardX12Document x12Doc = x12Parser.parse(x12Message);
X12Group group1 = x12Doc.getGroups().get(0);
List<X12TransactionSet> group1TxList = group1.getTransactions();
X12TransactionSet tx1 = group1TxList.get(0);
if ("856".equals(tx1.getTransactionSetIdentifierCode())) {
   AsnTransactionSet asnTx = (AsnTransactionSet)tx1;
   ...
}
```

How to access loop data within a transaction set:

```java

AsnTransactionSet asnTx = (AsnTransactionSet)tx1;
Shipment shipment = asnTx.getShipment();
// the Shipment has one or more Order loops
List<X12Loop> shipmentChildLoops = shipment.getParsedChildrenLoops();

X12Loop shipmentChildLoop = shipmentChildLoops.get(0);
if ("O".equals(shipmentChildLoop.getCode())) {
   // found the order
   Order order = (Order) shipmentChildLoop;
   order.getPrf().getPurchaseOrderNumber();
   
   List<X12Loop> orderChildLoops = order.getParsedChildrenLoops();
   X12Loop orderChildLoop = orderChildLoops.get(0);
   switch (orderChildLoop.getCode()) {
     case "T" :
         // found a tare
         processTare((Tare)orderChildLoop);
         break;
      case "P" :
         // found a pack
         processPack((Pack)orderChildLoop);
         break;
      case  "I" :
         // found an item
         processItem((Item)orderChildLoop);
         break;
      default:
         break;
   }

   
```


Look at the [DefaultAsn856TransactionSetParserEntireTxSetTest](https://github.com/walmartlabs/gozer/blob/master/src/test/java/com/walmartlabs/x12/asn856/DefaultAsn856TransactionSetParserEntireTxSetTest.java) to see how to access various parts of the parsed document. 

### Using utilities after parsing
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

## Getting Started: Using the DEX Parsers and Validators
The DEX EDI/X12 message format does not follow the standard message format. It has a separate parser, however it still implements the `X12Parser` interface. This allows it to be used in any product with the standard Gozer parser without any special considerations. 

```java

String x12Message = ...
X12Parser x12Parser = new DefaultDex894Parser();
X12Validator x12Validator = new DefaultDex894Validator();

X12Document x12 = x12Parser.parse(x12Message);
Set<X12ErrorDetail> errorSet = x12Validator.validate(x12);

```


## Getting Started: Writing a Parser and Validator
[Guide for writing EDI parsers](AddingParsers.md)

## More Information

[About EDI Standards](http://ediacademy.com/blog/edi-x12-standard/)

[EDI file format basics](https://www.xtranslator.com/prod/beginguidex12.pdf)

[ASC X12 Standards](http://edi.aaltsys.info/01_standards.html)

[EDI transaction set codes](https://www.spscommerce.com/resources/edi-documents-transactions/)

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
