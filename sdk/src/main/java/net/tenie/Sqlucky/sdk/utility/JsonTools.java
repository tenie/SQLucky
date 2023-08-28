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
	
	public static JSONObject strToObj(String jsonStr) {
		JSONObject obj = JSONObject.parseObject(jsonStr);
		return obj;
	}
	/**
	 * 获取json中的字段的值
	 * @param jsonStr
	 * @param keyName
	 * @return
	 */
	public static String getJsonKeyValue(String jsonStr, String keyName) {
		JSONObject obj = JSONObject.parseObject(jsonStr);
		String val = obj.getString(keyName);
		return val;
	}
	
	
	public static void getTable(String filename) {
		String txt =  FileTools.read(filename);
		JSONObject obj = JSONObject.parseObject(txt);
		  
		String  ja = obj.getString("name");
//		System.out.println(ja);
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
//				System.out.println(DataModelPoVal);
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
	
	
	/**
	 * jsonToMap
	 */
	public static Map<String,Object> jsonStrToMap(String str){
//	    String str = "{\"age\":\"24\",\"name\":\"cool_summer_moon\"}";
	    JSONObject  jsonObject = JSONObject.parseObject(str);
	    //json对象转Map
	    Map<String,Object> map = jsonObject;
	    System.out.println("map对象是：" + map);
//	    Object object = map.get("age");
//	    System.out.println("age的值是"+object);
	    return map;
	}
	
	//Map 转 Json
	public static String  mapToJson(Map<String,Object> map){ 
	    JSONObject json = new JSONObject(map);
	    return json.toString(); 
	}
	
	
}
