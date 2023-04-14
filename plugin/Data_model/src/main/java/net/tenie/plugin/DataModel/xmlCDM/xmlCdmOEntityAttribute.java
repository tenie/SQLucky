package net.tenie.plugin.DataModel.xmlCDM;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

//<Model xmlns:a="attribute" xmlns:c="collection" xmlns:o="object">
public class xmlCdmOEntityAttribute {
	
	@JacksonXmlProperty(namespace = "object", localName = "EntityAttribute")
	private String EntityAttribute;

	@JacksonXmlProperty(namespace = "attribute", localName = "Name")
	private String name;

	@JacksonXmlProperty(namespace = "attribute", localName = "Code")
	private String code;
	
	
//	@JacksonXmlElementWrapper(namespace = "collection", localName = "Attributes")
//    private List<EntityAttributeDataItem> oEntityAttribute;
}
