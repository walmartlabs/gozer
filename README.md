[![Build Status](https://github.com/walmartlabs/gozer/workflows/Gozer-CI/badge.svg)](https://github.com/walmartlabs/gozer/actions)

[![codecov](https://codecov.io/gh/walmartlabs/gozer/branch/master/graph/badge.svg)](https://codecov.io/gh/walmartlabs/gozer)

# EDI X12 Standard Parsing Library

The EDI X12 Standard provides a uniform way for companies to exchange information across different sectors. 

This open source library, available through @WalmartLabs, provides Java based classes that can parse various X12 file formats into a representative Java object model. It's primary focus is on those formats related to the Supply Chain sector.

## Supported X12 Format Parsers and Transaction Set Parsers

| X12     	| Name                             	    | Description 	  | X12 Version(s) |
|---------	|-------------------------------------   |-------------	  |--------------  |
| Generic 	| Handles X12 Standard Documents in a generic way	  | Generic | 5010      |
| DEX 894 	| Delivery/Return Base Record 	        | DSD deliveries | 4010,5010      |
| DEX 895 	| Delivery/Return Acknowledgment Record  | DSD delivery acknowledgement | Under consideration     |
| EDI 856   	| Ship Notice/Manifest Transaction Set   | Advance Ship Notice (ASN) | 5010 |
| EDI 850   	| PO Transaction Set                     | Purchase Order (PO) | WIP |
| EDI 855   	| PO Acknowledgment Transaction Set      | Purchase Order acknowledgement| Under consideration |
| EDI 810   | Invoice Transaction Set                | Invoice | Under consideration     |
| EDI 214   | Shipment Status Transaction Set        | Shipment Status  | Under consideration     |

## Why the name Gozer

The Agile development team at @WalmartLabs that is responsible for the design and development of "inbound processing" products is called the GhostBusters. Inbound Processing covers the broad array of micro-services involved with moving and receiving merchandise between locations, including the supplier to the distribution center, the supplier to the store (DSD), and the distribution center to the store. 

## Basic Design Approach

Gozer seeks to provide more than a generic X12 parsing capability that turns an EDI X12 message into a list of segments and elements, although it does have that capability. Gozer hopes to provide a library of easy to use classes, that can transform a parsed EDI message into a set of POJOs that corresponds with a specific X12 format. Attributes are labeled with their business names rather than the more cryptic segment identifiers. In addition, there are numerous interfaces and various extension points allowing users to customize some of the parsing approaches that are offered in Gozer.

The initial design provided some basic validation capabilities but most of that was updated in 0.3.0. It is now recommended that users of the framework build out validation capabilities so that they can enforce compliance with the guides that are agreed upon between the various parties. This also allows users to decide how best to handle the generation of 824 warnings and errors when validation fails.  

### Practical Approach to Parsing
Each X12 format that is supported will have a Java implementation of the `X12Parser`. The parser will be responsible for parsing the message in the given format to an `X12Document`, which is the representative Java object model (POJO).

	public interface X12Parser<T extends X12Document> {
		/**
	     * parse the X12 transmission into a representative Java object
	     *
	     * @return the representative Java object
	     * @throws X12ParserException
	     */
		T parse(String sourceData);

The Gozer parsers that implement `X12Parser` are designed to parse a specific EDI X12 document type. In order for a document to be considered successfully parsed the message MUST be well-formed. That means that the segments must have the correct segment identifiers, which must be nested correctly and appear in the proper order. If the X12 message is not well-formed and can't be parsed the parser will throw an `X12ParserException`. 

If the EDI message was well-formed and successfully parsed that does not mean that it is valid. The parsers, intentionally, do not attempt to validate any values within the segment elements (other than segment identifiers). They are intended to be as loose as possible regarding validation during the parsing of different versions of the EDI X12 message. Validation can be performed against the `X12Document` that is returned by the parser when it is successful.

Gozer currently provides two implementations of the `X12Parser`. 

- The `StandardX12Parser` for most EDI documents wrapped in ISA/ISE envelopes and contain transaction sets.
- The `DefaultDex894Parser` to handle DEX 894 documents.

Using Gozer is as easy as passing the contents of the EDI message to an instantiated parser:

	X12Document x12Doc = x12Parser.parse(x12Message);
	
Creating an instance of the `X12Parser` will vary depending on which one is being used. 

# Getting Started: The Standard EDI Parser and Document in Gozer

Most EDI/X12 messages adhere to a common format that wraps transaction sets in an envelope. These can all be parsed using the `StandardX12Parser`.  

Creating an instance of the `StandardX12Parser` is very simple:

```java
try {
	StandardX12Parser x12Parser = new StandardX12Parser();
	StandardX12Document x12Document = x12Parser.parse(new String(ediMessage));
} catch (X12ParserException e) {
	// parsing did not go well
}
```
	
However, please check out the section **Handling Transaction Sets** to make sure the `StandardX12Parser` is setup correctly.

## Exploring the Standard EDI message format

Each message will have an envelope that contains a set  of groups. Each group will contain a set of transaction sets. 

```
ISA*...
	GS*...*12345*X*005010
		ST*856*00001
			...
		SE*100*00001
	GE*1*12345
ISE*...
```

These types of messages can be parsed using the `StandardX12Parser`. This is an implementation of the `X12Parser` interface that returns a `StandardX12Document`. 

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

Inside each group, the message can contain one or more transaction sets. Each transaction set will start with an `ST` segment line and end with the first occurrence of a segment that has the identifier `SE`. It is the transaction set that contains an EDI document.

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
* list with two `X12Group` objects
* the first `X12Group` would have a list with two `X12TransactionSet`s
* the second `X12Group` would have a list with one `X12TransactionSet`
	

## Handling Transaction Sets
Each EDI transmission can contain one or more transaction sets. Each transaction set can represent a different document type. For example the ASN (856) or a Purchase Order (850). Each of these document types are comprised of a variety of different segments. 

When the `StandardX12Parser` is instantiated it will not be able to parse any EDI document types. 

	StandardX12Parser x12Parser = new StandardX12Parser();

One or more `TransactionSetParser` implementations must be registered so that the parser will work correctly. Each `TransactionSetParser` will understand a different document type. 

The `GenericTransactionSetParser` is designed to work with any document type but will only be able to return a list of generic `X12Segment`s instead of a more user-friendly domain object. 

	StandardX12Parser x12Parser = new StandardX12Parser();
	x12Parser.registerTransactionSetParser(new GenericTransactionSetParser());

In order to take advantage of the set of POJOs that corresponds with a specific X12 format, each individual `TransactionSetParser` implementation that a user is interested in must be registered individually.

	// setting up the standard X12 parser to work
	// with ASN 856 and PO 850 documents
	// all other documents will be ignored
	StandardX12Parser x12Parser = new StandardX12Parser();
	x12Parser.registerTransactionSetParser(new DefaultAsn856TransactionSetParser());
	x12Parser.registerTransactionSetParser(new DefaultPo950TransactionSetParser());

When a transaction set does not have a registered parser the `X12StandardParser` will send the list of segments associated with it to an extension point. By default, the extension point will ignore any transaction set that it receives. When it is important to interact with a transaction set that had no `TransactionSetParser` implementation registered a custom class that implements `UnhandledTransactionSet` can be registered. 

```java
StandardX12Parser x12Parser = new StandardX12Parser();
x12Parser.registerTransactionSetParser(new DefaultAsn856TransactionSetParser());
x12Parser.registerUnhandledTransactionSet(new CustomHandlerForUnhandledTransactionSet());
```

Look at the [Sample](https://github.com/walmartlabs/gozer/tree/master/src/test/java/sample) to see a very simplified example of how to register a transaction set parser and use the `StandardX12Parser`.

## Using the parsed document data

The `StandardX12Document` provides access to the entire parsed EDI message which may contain one or more transaction sets.

In order to access the first transaction set on the first group:

	// parse an EDI file transmission
	StandardX12Document x12 = x12Parser.parse(new String(ediMessage));
	
	// access to envelope
	InterchangeControlEnvelope envelope = x12.getInterchangeControlEnvelope();
	
	// access to the groups
	List<X12Group> groups = x12.getGroups();
	
	// retrieve the transaction sets from the first group
	List<X12TransactionSet> txForGroupOne = groups.get(0).getTransactions();
	
	// retrieve the first transaction set from the first group
	X12TransactionSet txSet = txForGroupOne.get(0);
	
	// identify which EDI document type 
	// the transaction set represents
	if ("856".equals(txSet.getTransactionSetIdentifierCode()) {
		// we have an ASN
		AsnTransactionSet asnTx = (AsnTransactionSet) txSet;
		

### Dealing with Hierarchy Loops

Some of the EDI documents have hierarchy loops. Each of the hierarchy segments is related to the others using a set of indexes. 

	HL*1**S
	HL*2*1*O
	HL*3*2*P
	HL*4*3*I
	HL*5*3*I

The basic looping structure (used on an ASN) above denotes a hierarchy of data that starts with a shipment loop. 

	- S(hipment) 
		- O(rder)
			- P(ack)
				- I(tem)
				- I(item)
	
Each loop may have other segments associated with it. 
For example this is a pack loop with a MAN segment providing a pack label

	HL*3*2*P
	MAN*GM*00001914178883300010
	
All looping information will be available in the `X12TransactionSet` object returned by the `TransactionSetParser`.

```java
// after parsing an EDI message
if ("856".equals(txSet.getTransactionSetIdentifierCode()) {
	// we have an ASN
	AsnTransactionSet asnTx = (AsnTransactionSet) txSet;
	// the first loop is an X12Loop of type Shipment
	Shipment shipment = asnTx.getShipment()
	
	// get the children loops
	List<X12Loop> shipmentChildLoops = shipment.getParsedChildrenLoops();
	
	// the Shipment has one or more Order loops
	// get the first child
	X12Loop loop = shipmentChildLoops.get(0);
	if ("O".equals(loop.getCode()) {
		// we have an Order loop
		Order orderLoop = (Order) loop;
		// access a segment on the order loop
		order.getPrf().getPurchaseOrderNumber();
   
		List<X12Loop> orderChildLoops = order.getParsedChildrenLoops();
		X12Loop orderChildLoop = orderChildLoops.get(0);
		
		// examine loops on the order
		switch (orderChildLoop.getCode()) {
			case "T" :
	         		// found a tare
	         		this.processTare((Tare)orderChildLoop);
	         		break;
      		case "P" :
         			// found a pack
			     this.processPack((Pack)orderChildLoop);
			     break;
			case  "I" :
				// found an item
			     this.processItem((Item)orderChildLoop);
			     break;
		      default:
		      	break;
		}
   
```

Look at the [X12LoopUtilTest](https://github.com/walmartlabs/gozer/tree/master/src/test/java/com/walmartlabs/x12/util/loop/X12LoopUtilTest) to see how to walk through loops and examine the segments associated with each one.

Look at the [DefaultAsn856TransactionSetParserEntireTxSetTest](https://github.com/walmartlabs/gozer/blob/master/src/test/java/com/walmartlabs/x12/asn856/DefaultAsn856TransactionSetParserEntireTxSetTest.java) to see how to access various parts of the parsed document. 

In earlier versions any error detected during looping would throw an `X12ParserException` stopping any further parsing. Starting with 0.3.0, when an error in looping occurs an exception will no longer be thrown. Instead the parser will set an attribute to indicate how that part of the parsing went. Invalid looping is now treated as a post-parsing validation error. It will be up to the user of the framework to check the value of this attribute and assess the looping issues.

### Dealing with Hierarchy Loop errors
It is expected that after parsing, the code should verify how looping was handled.
	
	X12TransactionSet txSet = txForGroupOne.get(0);
	if (txSet.hasLooping()) {
		if (txSet.isLoopingValid()) {
	     	// looping ok
		} else {
	     	// handle looping errors
	     	List<X12ErrorDetail> loopingErrors = asnTx.getLoopingErrors();
		}

After parsing, it is considered the responsibility of the consumer to perform any additional validations on the data that was provided in various segments and elements. That can be done through custom code. 


# Getting Started: The DEX 894 Parser and Document in Gozer
The DEX EDI/X12 message format does not follow the standard message format. It has a separate parser, however it still implements the `X12Parser` interface. This allows it to be used in any product with the standard Gozer parser without any special considerations. Since there are no transaction sets in DEX there is nothing extra to register. This parser will work correctly after instantiation. 

```java
String dexMessage = ...
DefaultDex894Parser dexParser = new DefaultDex894Parser();
Dex894 dexDoc = dexParser.parse(dexMessage);
```

The `DefaultDex894Parser` returns an `X12Document` of the type `Dex894`.

### Using utilities after parsing
A parsed DEX document can be used similar to a standard X12 document. Various utilities can be used for post-processing the data. 
For example:

```java
Dex894 dex = dexParser.parse(dexMessage);
Dex894Item dexItem = dex.getItems().get(0);

// check CRC on each DEX transaction
CyclicRedundancyCheck crc16 = new CyclicRedundancyCheck();
List<Dex894TransactionSet> dexTxList = dex.getTransactions();
for (Dex894TransactionSet dexTx : dexTxList) {
   if (crc16.verifyBlockOfText(dexTx.getIntegrityCheckValue(), dexTx.getTransactionData())) {
      // passed CRC check
   } else {
      // failed CRC check
   }
}

```

# Contributing to Gozer

## Getting Started: Writing a Parser
[Guide for writing EDI parsers](AddingParsers.md)

## Getting Started: Writing a TransactionSetParser
[Guide for writing EDI parsers](AddingTransactionSetParsers.md)

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
