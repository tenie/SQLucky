package net.tenie.Sqlucky.sdk.utility;

import com.alibaba.fastjson.JSONObject;

public class JsonTools {
	public static void getTable(String filename) {
		String txt =  FileTools.read(filename);
		JSONObject obj = JSONObject.parseObject(txt);
		  
		 String  ja = obj.getString("name");
		System.out.println(ja);
	}
	
	
	
	public static void main(String[] args) {
		getTable("C:\\Users\\tenie\\Downloads\\infodms.chnr.json");
	}
}
