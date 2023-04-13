package net.tenie.plugin.DataModel.xmlCDM;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


//<Model xmlns:a="attribute" xmlns:c="collection" xmlns:o="object">

//@JacksonXmlRootElement(namespace = "object", localName = "Table")
public class xmlPdmOTable {
	
	@JacksonXmlProperty(namespace = "attribute", localName = "ObjectID")
	private String objectId;

	@JacksonXmlProperty(namespace = "attribute", localName = "Name")
	private String name;

	@JacksonXmlProperty(namespace = "attribute", localName = "Code")
	private String code;
	
	
//	c:Tables
	@JacksonXmlElementWrapper(namespace = "collection", localName = "Columns")
    private List<xmlPdmOColumn> cColumns;
    
    @JacksonXmlElementWrapper(namespace = "collection", localName = "Indexes")
    private List<xmlPdmOIndex> cIndexes;

	 

	public List<xmlPdmOColumn> getcColumns() {
		return cColumns;
	}

	public void setcColumns(List<xmlPdmOColumn> cColumns) {
		this.cColumns = cColumns;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<xmlPdmOIndex> getcIndexes() {
		return cIndexes;
	}

	public void setcIndexes(List<xmlPdmOIndex> cIndexes) {
		this.cIndexes = cIndexes;
	}

	@Override
	public String toString() {
		return "xmlPdmOTable [objectId=" + objectId + ", name=" + name + ", code=" + code + ", cColumns=" + cColumns
				+ ", cIndexes=" + cIndexes + "]";
	}

 
//	public List<xmlPdmOIndex> getcIndexes() {
//		return cIndexes;
//	}
//
//	public void setcIndexes(List<xmlPdmOIndex> cIndexes) {
//		this.cIndexes = cIndexes;
//	}

	 
    
    
}
