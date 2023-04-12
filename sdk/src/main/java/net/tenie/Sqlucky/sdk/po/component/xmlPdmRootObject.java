package net.tenie.Sqlucky.sdk.po.component;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


//<Model xmlns:a="attribute" xmlns:c="collection" xmlns:o="object">
//@JacksonXmlRootElement(namespace = "object", localName = "RootObject")
public class xmlPdmRootObject {

	@JacksonXmlElementWrapper(namespace = "collection", localName = "Children")
    private List<xmlPdmOModel> cChildren;
 
    
 

	public List<xmlPdmOModel> getcChildren() {
		return cChildren;
	}




	public void setcChildren(List<xmlPdmOModel> cChildren) {
		this.cChildren = cChildren;
	}




	@Override
	public String toString() {
		return "xmlPdmRootObject [ cChildren=" + cChildren + "]";
	}

 
    
}


 