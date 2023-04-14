package net.tenie.plugin.DataModel.xmlCDM;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

//<Model xmlns:a="attribute" xmlns:c="collection" xmlns:o="object">
//@JacksonXmlRootElement(namespace = "object", localName = "Package")
public class xmlCdmODataItem {
	
//	c:Tables
	@JacksonXmlElementWrapper(namespace = "collection", localName = "Packages")
    private List<xmlPdmOTable> cPackages;

    
    
}
