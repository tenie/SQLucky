package net.tenie.Sqlucky.sdk.utility;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSONObject;

import net.tenie.Sqlucky.sdk.component.ComponentGetter;
import net.tenie.Sqlucky.sdk.po.DataModelPo;
import net.tenie.Sqlucky.sdk.subwindow.MyAlert;

public class JsonTools {
	/**
	 * 对象转字符串
	 * @param obj
	 * @return
	 */
	public static String objToStr(Object obj) {
		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(obj);
		return jsonObject.toJSONString();
	}
	
	public static void getTable(String filename) {
		String txt =  FileTools.read(filename);
		JSONObject obj = JSONObject.parseObject(txt);
		  
		 String  ja = obj.getString("name");
		System.out.println(ja);
	}
	
	
	public static void readJosnModel(String encode) {
		File f = FileOrDirectoryChooser.showOpenSqlFile("Open", ComponentGetter.primaryStage);
		if (f == null)
			return ;
		String val = "";
		try {
			val = FileUtils.readFileToString(f, encode);
			if(val != null && !"".equals(val) ) { 
				DataModelPo DataModelPoVal = JSONObject.parseObject(val, DataModelPo.class);
				System.out.println(DataModelPoVal);
			}
		} catch (IOException e) {
			e.printStackTrace();
			MyAlert.errorAlert( e.getMessage());
		} 
		
		
	}
	
	public static void main(String[] args) {
		readJosnModel("C:\\Users\\tenie\\Downloads\\infodms.chnr.json");
	}
//	public static void main(String[] args) {
//		getTable("C:\\Users\\tenie\\Downloads\\infodms.chnr.json");
//	}
}
