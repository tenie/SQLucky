package net.tenie.plugin.DataModel.xmlCDM;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


//<Model xmlns:a="attribute" xmlns:c="collection" xmlns:o="object">
//@JacksonXmlRootElement(namespace = "object", localName = "RootObject")
public class xmlCdmRootObject {

	@JacksonXmlElementWrapper(namespace = "collection", localName = "Children")
    private List<xmlCdmOModel> cChildren;
 
    
 

	public List<xmlCdmOModel> getcChildren() {
		return cChildren;
	}




	public void setcChildren(List<xmlCdmOModel> cChildren) {
		this.cChildren = cChildren;
	}




	@Override
	public String toString() {
		return "xmlPdmRootObject [ cChildren=" + cChildren + "]";
	}

 
    
}


 