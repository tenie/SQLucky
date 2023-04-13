package net.tenie.plugin.DataModel.xmlPDM;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

//<Model xmlns:a="attribute" xmlns:c="collection" xmlns:o="object">
//@JacksonXmlRootElement(namespace = "object", localName = "Model")
public class xmlPdmOModel {

	@JacksonXmlProperty(namespace = "attribute", localName = "ObjectID")
	private String objectId;
	@JacksonXmlProperty(namespace = "attribute", localName = "Name")
	private String name;

	@JacksonXmlProperty(namespace = "attribute", localName = "Code")
	private String code;
	 
	@JacksonXmlElementWrapper(namespace = "collection", localName = "Packages")
    private List<xmlPdmOPackage> cPackages;

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

	@Override
	public String toString() {
		return "xmlPdmOModel [objectId=" + objectId + ", name=" + name + ", code=" + code + ", cPackages=" + cPackages
				+ "]";
	}

	public List<xmlPdmOPackage> getcPackages() {
		return cPackages;
	}

	public void setcPackages(List<xmlPdmOPackage> cPackages) {
		this.cPackages = cPackages;
	}

//	public List<xmlPdmOPackage> getcPackages() {
//		return cPackages;
//	}
//
//	public void setcPackages(List<xmlPdmOPackage> cPackages) {
//		this.cPackages = cPackages;
//	}

 
	 
    
    
}
