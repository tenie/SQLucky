package net.tenie.plugin.DataModel.xmlCDM;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


//<Model xmlns:a="attribute" xmlns:c="collection" xmlns:o="object">

//@JacksonXmlRootElement(namespace = "object", localName = "Table")
public class xmlCdmOPPackage {
	
	@JacksonXmlProperty(namespace = "attribute", localName = "ObjectID")
	private String objectId;

	@JacksonXmlProperty(namespace = "attribute", localName = "Name")
	private String name;

	@JacksonXmlProperty(namespace = "attribute", localName = "Code")
	private String code;
	
	
//	c:Tables
	@JacksonXmlElementWrapper(namespace = "collection", localName = "Entities")
    private List<xmlCdmOEntity> cEntities;
    
    @JacksonXmlElementWrapper(namespace = "collection", localName = "DataItems")
    private List<xmlCdmODataItem> cDataItems;


	 
    
    
}
