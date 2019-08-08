# Guidelines for contributing an EDI X12 Parser

Each new X12 Parser should be placed in its own package. 

	x12
		asn856
		po850
		dex
			dx894

# Creating a "standard" EDI X12 Parser

0.2.1 will remove the `AbstractStandardX12Document`
TODO: update this documentation 

## What is the format?
The first step is to create the domain model for the format. This object (or set of objects) will represent structure of the EDI X12 document and define the attributes. 

The domain model attributes should use the more readable names of the attributes instead of their segment identifiers and element id. 

For example the element ISA08 represents the interchange receiver id. It is modeled on the `InterchangeControlHeader` object as follows:

	// ISA08
	private String interchangeReceiverId;

The next step is to determine whether the format shares segments with other EDI documents. For example the ISA and GS segment lines are common for most document types.

If the format shares common segments then the domain object should `extend` the `AbstractStandardX12Document`. This will provide an object with all of the common segments, allowing the developer to add the elements unique to the format in the concrete class. If the format does not share any (or many) common segments then it should `implement` the `X12Document` interface. 

![X12Document hierarchy](X12DocHierarchy.png)

TODO

## Creating the Parser
If the domain object (created above) representing the document extended `AbstractStandardX12Document` then you will want to have your parser `extend` the `AbstractStandardX12Parser`. This will provide the ability to parse all of the common segments and add them to the domain model object. Otherwise the parser must `implement` the `X12Parser` interface. 

## The X12Parser
The `X12Parser` interface defines a simple method that is expected to accept a `String` representation of the document. It will parse the document and return a concrete instance of the domain model object, which must be an `X12Document`

	public interface X12Parser<T extends X12Document> {
    		T parse(String sourceData);
    	}

## The AbstractStandardX12Parser
The `AbstractStandardX12Parser` uses the template pattern to implement the `parse` method. 

TODO


![X12Parser hierarchy](X12ParserHierarchy.png)

![X12TransactionSet hierarchy](X12TransactionSetHierarchy.png)

# Creating a "non-standard" EDI X12 Parser
An example of a non-standard EDI X12 Parser is the DEX 894 format. This format does not include an interchange control header, nor adhere to the overall structure of the standard documents.

An example of a simple non-standard X12 Parser can be found in the `sample.parser` package in the test folder. 

The format must be implemented as a POJO, which can store all of the attributes. This POJO must implement the `X12Document` interface.

	public class SampleX12Document implements X12Document {

The parser should implement the `X12Parser` interface, supplying the behavior to parse the document in the `parse` method.  

	public class SampleX12Parser implements X12Parser<SampleX12Document> {
		@Override
    		public SampleX12Document parse(String sourceData) {

The consumer of the parser can use it just like any other `X12Parser` that is available.

	String x12Message = ...
	X12Parser x12Parser = new SampleX12Parser();
	X12Document x12Doc = x12Parser.parse(x12Message);

