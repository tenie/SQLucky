package net.tenie.Sqlucky.sdk.utility;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.alibaba.fastjson.JSON;
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
	/**
	 * json 字符串传为对象
	 * @param <T>
	 * @param jsonStr
	 * @param clazz
	 * @return
	 */
	public static <T> T strToObj(String jsonStr, Class<T> clazz) {
		T obj = JSONObject.parseObject(jsonStr, clazz);
		return obj;
//		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(obj);
//		return jsonObject.toJSONString();
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
	
	/**
	 * json 字符串转list, 元素是T
	 * @param <T>
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static  <T> List<T> jsonToList(String json,Class<T> clazz){ 
		List<T> rs = JSONObject.parseArray(json, clazz);
		return rs;
	}
	/**
	 * 
	 * @param <T>
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static  <T> String listToJson(List<T> ls){ 
		String val = JSON.toJSONString(ls);
		return val;
//		JSONObject jsonObject = (JSONObject) JSONObject.toJSON(ls);
//		return jsonObject.toJSONString();
	}
	
	public static void main(String[] args) {
//		readJosnModel("C:\\Users\\tenie\\Downloads\\infodms.chnr.json");
		String str = "[{\"id\":1,\"userId\":1,\"backupName\":\"lll\",\"filePath\":\"C:\\\\Users\\\\tenie\\\\ssfblog/upload\\\\1\\\\1\\\\lll\",\"type\":1,\"createdAt\":\"2023-02-24T03:31:15.103+00:00\",\"updatedAt\":null},{\"id\":2,\"userId\":1,\"backupName\":\"lll\",\"filePath\":\"C:\\\\Users\\\\tenie\\\\ssfblog/upload\\\\2\\\\1\\\\lll\",\"type\":2,\"createdAt\":\"2023-02-24T03:31:15.283+00:00\",\"updatedAt\":null},{\"id\":3,\"userId\":1,\"backupName\":\"bbb\",\"filePath\":\"C:\\\\Users\\\\tenie\\\\ssfblog/upload\\\\1\\\\1\\\\bbb\",\"type\":1,\"createdAt\":\"2023-02-24T07:42:51.543+00:00\",\"updatedAt\":null}]\r\n"
				+ "";
		List<Map> ls = jsonToList(str, Map.class);
		System.out.println(ls);
		System.out.println("==========");
		String  json = listToJson(ls);
		System.out.println(json);

	}
//	public static void main(String[] args) {
//		getTable("C:\\Users\\tenie\\Downloads\\infodms.chnr.json");
//	}
}
