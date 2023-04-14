package net.tenie.plugin.DataModel.xmlCDM;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

public class xmlCdmOEntity {
	
	@JacksonXmlProperty(namespace = "attribute", localName = "ObjectID")
	private String objectId;

	@JacksonXmlProperty(namespace = "attribute", localName = "Name")
	private String name;

	@JacksonXmlProperty(namespace = "attribute", localName = "Code")
	private String code;
	
	
	
//	c:Tables
	@JacksonXmlElementWrapper(namespace = "collection", localName = "Attributes")
    private List<xmlCdmOEntityAttribute> cAttributes;

    
    
}
