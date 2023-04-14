package net.tenie.plugin.DataModel.xmlCDM;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class xmlCdmOPDataItem {
	@JacksonXmlProperty(namespace = "attribute", localName = "ObjectID")
	private String objectId;

	@JacksonXmlProperty(namespace = "attribute", localName = "Name")
	private String name;

	@JacksonXmlProperty(namespace = "attribute", localName = "Code")
	private String code;
	
}
