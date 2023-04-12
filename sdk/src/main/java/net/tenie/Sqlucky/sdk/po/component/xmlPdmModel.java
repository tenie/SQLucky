package net.tenie.Sqlucky.sdk.po.component;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
 


//<Model xmlns:a="attribute" xmlns:c="collection" xmlns:o="object">
 
@JacksonXmlRootElement(localName = "Model")
public class xmlPdmModel {
	// 命名空间设置
	@JacksonXmlProperty(localName = "xmlns:a", isAttribute = true)
    private String a = "attribute";
	@JacksonXmlProperty(localName = "xmlns:c", isAttribute = true)
    private String c = "collection";
	@JacksonXmlProperty(localName = "xmlns:o", isAttribute = true)
    private String o = "object";
	
	
//	o:RootObject
	@JacksonXmlProperty(namespace = "object", localName = "RootObject")
    private xmlPdmRootObject oRootObject;

 
	public xmlPdmRootObject getoRootObject() {
		return oRootObject;
	}

	public void setoRootObject(xmlPdmRootObject oRootObject) {
		this.oRootObject = oRootObject;
	}

	public String getA() {
		return a;
	}

	public void setA(String a) {
		this.a = a;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

	public String getO() {
		return o;
	}

	public void setO(String o) {
		this.o = o;
	}

	@Override
	public String toString() {
		return "xmlPdmModel [a=" + a + ", c=" + c + ", o=" + o + ", oRootObject=" + oRootObject + "]";
	}
    
    
}
