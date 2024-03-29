package net.tenie.plugin.DataModel.xmlPDM;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.XmlUtils;
import net.tenie.plugin.DataModel.po.DataModelInfoPo;
import net.tenie.plugin.DataModel.po.DataModelTableFieldsPo;
import net.tenie.plugin.DataModel.po.DataModelTablePo;

public class OptionPdmFile {
	
	static public DataModelInfoPo read(String FilePath) throws IOException {
//		  String xmlFileToString = XmlUtil.xmlFileToString(FilePath);
 		  String xmlFileToString = FileUtils.readFileToString(new File(FilePath) , "UTF-8");
		  xmlPdmModel model = XmlUtils.xmlToBean(xmlFileToString, xmlPdmModel.class);
		  DataModelInfoPo po =  pdmConversion(model);
		  return po;
	}
	
	static public DataModelInfoPo read(File file) throws Exception {
			
		  String xmlFileToString = XmlUtils.xmlFileToString(file.getAbsolutePath());
//		  String xmlFileToString = FileUtils.readFileToString(file , "UTF-8");
//		  xmlFileToString = xmlFileToString.replaceAll("/[\\u0000-\\u0008\\u000b\\u000c\\u000e-\\u001f\\ud800-\\udfff\\ufffe\\uffff]/g", "");
//		  xmlFileToString = filter_xml_marks(xmlFileToString);
		  xmlPdmModel model = XmlUtils.xmlToBean(xmlFileToString, xmlPdmModel.class);
		  DataModelInfoPo po =  pdmConversion(model);
		  return po;
	}
	
	// xml对象转换为info对象
	static public DataModelInfoPo pdmConversion(xmlPdmModel val) {
		List<xmlPdmOModel> oModel_ls = val.getoRootObject().getcChildren();
		
		DataModelInfoPo modelInfoPo = new DataModelInfoPo();
		List<DataModelTablePo> entities = new ArrayList<>();
		modelInfoPo.setEntities(entities);
		
		
		for(xmlPdmOModel md: oModel_ls) {
			modelInfoPo.setName(md.getName());
			modelInfoPo.setDescribe(md.getCode());
			
			
			//
			List<xmlPdmOPackage> pak_ls = md.getcPackages();
			if(pak_ls != null && pak_ls.size() > 0) {
				for(xmlPdmOPackage pak : pak_ls) {
					 List<xmlPdmOTable> tabs =	pak.getcTables();
					 if(tabs !=null && tabs.size() > 0) {
						 for(var tab : tabs) {
							 DataModelTablePo tabpo = new DataModelTablePo();
							 tabpo.setDefName(tab.getName());
							 tabpo.setDefKey(tab.getCode());
							 
							 entities.add(tabpo);
							 // 字段集合
							 List<DataModelTableFieldsPo> fields = new ArrayList<>();
							 tabpo.setFields(fields);
							 // 字符
							 List<xmlPdmOColumn> cols =  tab.getcColumns();
							 if(cols != null && cols.size() > 0) {
								 for(var col : cols) {
									 DataModelTableFieldsPo field = new DataModelTableFieldsPo();
									 field.setDefName(col.getName());
									 field.setDefKey(col.getCode());
									 field.setComment(col.getComment());
									 field.setTypeFullName(col.getDataType());
									 if( col.getLength() != null ) {
										 field.setLen(Integer.valueOf( col.getLength()));
									 }
									 String mandatory = col.getMandatory();
									 if(StrUtils.isNotNullOrEmpty(mandatory)) {
										 field.setNotNull("1");
										 
									 }
									 fields.add(field);
									 
								 }
							 }
						 }
					 }
				}
			}
			
		}
		
		return modelInfoPo;
	}
	
	
	// 过滤非法unicode 字符
	static public String filter_xml_marks(String in) {
		String out = "";
		for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
			if ((c >= 0x00 && c <= 0x08) || (c >= 0x0b && c <= 0x0c) || (c >= 0x0e && c <= 0x1f)) {
				continue;
			} 

			out += c;
		}

		return out;
	}
	
}
