package net.tenie.plugin.DataModel.xmlPDM;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import net.tenie.Sqlucky.sdk.utility.StrUtils;
import net.tenie.Sqlucky.sdk.utility.XmlUtil;
import net.tenie.plugin.DataModel.po.DataModelInfoPo;
import net.tenie.plugin.DataModel.po.DataModelTableFieldsPo;
import net.tenie.plugin.DataModel.po.DataModelTablePo;

public class OptionPdmFile {
	
	static public DataModelInfoPo read(String FilePath) throws IOException {
//		  String xmlFileToString = XmlUtil.xmlFileToString(FilePath);
 		  String xmlFileToString = FileUtils.readFileToString(new File(FilePath) , "UTF-8");
		  xmlPdmModel model = XmlUtil.xmlToBean(xmlFileToString, xmlPdmModel.class);
		  DataModelInfoPo po =  pdmConversion(model);
		  return po;
	}
	
	static public DataModelInfoPo read(File file) throws Exception {
			
		  String xmlFileToString = XmlUtil.xmlFileToString(file.getAbsolutePath());
//		  String xmlFileToString = FileUtils.readFileToString(file , "UTF-8");
//		  xmlFileToString = xmlFileToString.replaceAll("/[\\u0000-\\u0008\\u000b\\u000c\\u000e-\\u001f\\ud800-\\udfff\\ufffe\\uffff]/g", "");
//		  xmlFileToString = filter_xml_marks(xmlFileToString);
		  xmlPdmModel model = XmlUtil.xmlToBean(xmlFileToString, xmlPdmModel.class);
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
	
    public static void main(String[] args) throws IOException {
//    	DataModelInfoPo po = read("D:\\workDir\\data\\福特doc\\\\InfoDMS&GMS_DB2.pdm");
//    	DataModelInfoPo po = read("D:\\workDir\\data\\InfoDMS&GMS_DB2.pdm");
//    
//    	System.out.println(po);
//    	//构造数据模型实例并赋值
//        XmlSendFileModel xmlSendFileModel = XmlSendFileModel.builder(). 
//                bankType("SELF").
//                moneySum(new BigDecimal(20)).
//                totalIty(new BigDecimal(20)).build();
//        //调用util类把bean对象转换成XML字符串
//        String xmlStr = XMLUtil.beanToXmlStr(xmlSendFileModel);
//        // 写入xml字符串到文件。
//        XMLUtil.strToXmlFile(xmlStr, new File("D:\\myGit\\xmltext.xml"));
//        System.out.println("xml文件生成完毕");
//
//        //读取一个XML文件成XML字符串格式。
////        System.out.println("输出XML文件的字符串 = " + XMLUtil.xmlFileToString("D:\\myGit\\xmltext.xml"));
//        String xmlFileToString = XmlUtil.xmlFileToString("D:\\myGit\\xmltext.xml");
//    	  String xmlFileToString = XmlUtil.xmlFileToString("D:\\workDir\\data\\InfoDMS&GMS_DB2.pdm");
//        
//        
//        //读取一个XML文件成XML字符串格式。
////        String xmlFileToString = XmlUtil.xmlFileToString(file.getAbsolutePath());
    	  String xmlFileToString = FileUtils.readFileToString(new File("D:\\workDir\\data\\InfoDMS&GMS_DB2.pdm") , "UTF-8");
        xmlPdmModel model = XmlUtil.xmlToBean(xmlFileToString, xmlPdmModel.class);
//        
////       System.out.println(model);
       System.out.println(model.getoRootObject().getcChildren().size());
       System.out.println(model.getoRootObject().getcChildren().get(0).getcPackages().size());

       System.out.println(model.getoRootObject().getcChildren().get(0).getcPackages().get(0).getcTables().size());

       System.out.println(model.getoRootObject().getcChildren().get(0).getcPackages().get(1).getcTables().size());
       System.out.println(model.getoRootObject().getcChildren().get(0).getcPackages().get(2).getcTables());
       System.out.println(model.getoRootObject().getcChildren().get(0).getcPackages().get(3));
	}
}
