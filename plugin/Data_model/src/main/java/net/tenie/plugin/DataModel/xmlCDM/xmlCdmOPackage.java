package net.tenie.plugin.DataModel.xmlCDM;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

//<Model xmlns:a="attribute" xmlns:c="collection" xmlns:o="object">
@JacksonXmlRootElement(namespace = "object", localName = "Package")
public class xmlCdmOPackage {
	
//	c:Tables
	@JacksonXmlElementWrapper(namespace = "collection", localName = "Tables")
    private List<xmlPdmOTable> cTables;

	public List<xmlPdmOTable> getcTables() {
		return cTables;
	}

	public void setcTables(List<xmlPdmOTable> cTables) {
		this.cTables = cTables;
	}

	@Override
	public String toString() {
		return "xmlPdmOPackage [cTables=" + cTables + "]";
	}
    
    
}
