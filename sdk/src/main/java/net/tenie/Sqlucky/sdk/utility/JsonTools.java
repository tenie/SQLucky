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
	
	public static void main(String[] args) {
		String str = "{\r\n"
				+ "  \"url\": \"https://api.github.com/repos/tenie/SQLucky/releases/54633029\",\r\n"
				+ "  \"assets_url\": \"https://api.github.com/repos/tenie/SQLucky/releases/54633029/assets\",\r\n"
				+ "  \"upload_url\": \"https://uploads.github.com/repos/tenie/SQLucky/releases/54633029/assets{?name,label}\",\r\n"
				+ "  \"html_url\": \"https://github.com/tenie/SQLucky/releases/tag/v2.1.0\",\r\n"
				+ "  \"id\": 54633029,\r\n"
				+ "  \"author\": {\r\n"
				+ "    \"login\": \"tenie\",\r\n"
				+ "    \"id\": 13869926,\r\n"
				+ "    \"node_id\": \"MDQ6VXNlcjEzODY5OTI2\",\r\n"
				+ "    \"avatar_url\": \"https://avatars.githubusercontent.com/u/13869926?v=4\",\r\n"
				+ "    \"gravatar_id\": \"\",\r\n"
				+ "    \"url\": \"https://api.github.com/users/tenie\",\r\n"
				+ "    \"html_url\": \"https://github.com/tenie\",\r\n"
				+ "    \"followers_url\": \"https://api.github.com/users/tenie/followers\",\r\n"
				+ "    \"following_url\": \"https://api.github.com/users/tenie/following{/other_user}\",\r\n"
				+ "    \"gists_url\": \"https://api.github.com/users/tenie/gists{/gist_id}\",\r\n"
				+ "    \"starred_url\": \"https://api.github.com/users/tenie/starred{/owner}{/repo}\",\r\n"
				+ "    \"subscriptions_url\": \"https://api.github.com/users/tenie/subscriptions\",\r\n"
				+ "    \"organizations_url\": \"https://api.github.com/users/tenie/orgs\",\r\n"
				+ "    \"repos_url\": \"https://api.github.com/users/tenie/repos\",\r\n"
				+ "    \"events_url\": \"https://api.github.com/users/tenie/events{/privacy}\",\r\n"
				+ "    \"received_events_url\": \"https://api.github.com/users/tenie/received_events\",\r\n"
				+ "    \"type\": \"User\",\r\n"
				+ "    \"site_admin\": false\r\n"
				+ "  },\r\n"
				+ "  \"node_id\": \"RE_kwDOEqqS_M4DQaJF\",\r\n"
				+ "  \"tag_name\": \"v2.1.0\",\r\n"
				+ "  \"target_commitish\": \"main\",\r\n"
				+ "  \"name\": \"v2.1.0\",\r\n"
				+ "  \"draft\": false,\r\n"
				+ "  \"prerelease\": false,\r\n"
				+ "  \"created_at\": \"2021-12-04T15:36:53Z\",\r\n"
				+ "  \"published_at\": \"2021-12-04T15:56:00Z\",\r\n"
				+ "  \"assets\": [\r\n"
				+ "    {\r\n"
				+ "      \"url\": \"https://api.github.com/repos/tenie/SQLucky/releases/assets/51002240\",\r\n"
				+ "      \"id\": 51002240,\r\n"
				+ "      \"node_id\": \"RA_kwDOEqqS_M4DCjuA\",\r\n"
				+ "      \"name\": \"SQLucky-MacOS.dmg\",\r\n"
				+ "      \"label\": null,\r\n"
				+ "      \"uploader\": {\r\n"
				+ "        \"login\": \"tenie\",\r\n"
				+ "        \"id\": 13869926,\r\n"
				+ "        \"node_id\": \"MDQ6VXNlcjEzODY5OTI2\",\r\n"
				+ "        \"avatar_url\": \"https://avatars.githubusercontent.com/u/13869926?v=4\",\r\n"
				+ "        \"gravatar_id\": \"\",\r\n"
				+ "        \"url\": \"https://api.github.com/users/tenie\",\r\n"
				+ "        \"html_url\": \"https://github.com/tenie\",\r\n"
				+ "        \"followers_url\": \"https://api.github.com/users/tenie/followers\",\r\n"
				+ "        \"following_url\": \"https://api.github.com/users/tenie/following{/other_user}\",\r\n"
				+ "        \"gists_url\": \"https://api.github.com/users/tenie/gists{/gist_id}\",\r\n"
				+ "        \"starred_url\": \"https://api.github.com/users/tenie/starred{/owner}{/repo}\",\r\n"
				+ "        \"subscriptions_url\": \"https://api.github.com/users/tenie/subscriptions\",\r\n"
				+ "        \"organizations_url\": \"https://api.github.com/users/tenie/orgs\",\r\n"
				+ "        \"repos_url\": \"https://api.github.com/users/tenie/repos\",\r\n"
				+ "        \"events_url\": \"https://api.github.com/users/tenie/events{/privacy}\",\r\n"
				+ "        \"received_events_url\": \"https://api.github.com/users/tenie/received_events\",\r\n"
				+ "        \"type\": \"User\",\r\n"
				+ "        \"site_admin\": false\r\n"
				+ "      },\r\n"
				+ "      \"content_type\": \"application/octet-stream\",\r\n"
				+ "      \"state\": \"uploaded\",\r\n"
				+ "      \"size\": 68370674,\r\n"
				+ "      \"download_count\": 7,\r\n"
				+ "      \"created_at\": \"2021-12-04T15:50:11Z\",\r\n"
				+ "      \"updated_at\": \"2021-12-04T15:55:56Z\",\r\n"
				+ "      \"browser_download_url\": \"https://github.com/tenie/SQLucky/releases/download/v2.1.0/SQLucky-MacOS.dmg\"\r\n"
				+ "    },\r\n"
				+ "    {\r\n"
				+ "      \"url\": \"https://api.github.com/repos/tenie/SQLucky/releases/assets/51002383\",\r\n"
				+ "      \"id\": 51002383,\r\n"
				+ "      \"node_id\": \"RA_kwDOEqqS_M4DCjwP\",\r\n"
				+ "      \"name\": \"sqlucky-ubuntu-amd64.deb\",\r\n"
				+ "      \"label\": null,\r\n"
				+ "      \"uploader\": {\r\n"
				+ "        \"login\": \"tenie\",\r\n"
				+ "        \"id\": 13869926,\r\n"
				+ "        \"node_id\": \"MDQ6VXNlcjEzODY5OTI2\",\r\n"
				+ "        \"avatar_url\": \"https://avatars.githubusercontent.com/u/13869926?v=4\",\r\n"
				+ "        \"gravatar_id\": \"\",\r\n"
				+ "        \"url\": \"https://api.github.com/users/tenie\",\r\n"
				+ "        \"html_url\": \"https://github.com/tenie\",\r\n"
				+ "        \"followers_url\": \"https://api.github.com/users/tenie/followers\",\r\n"
				+ "        \"following_url\": \"https://api.github.com/users/tenie/following{/other_user}\",\r\n"
				+ "        \"gists_url\": \"https://api.github.com/users/tenie/gists{/gist_id}\",\r\n"
				+ "        \"starred_url\": \"https://api.github.com/users/tenie/starred{/owner}{/repo}\",\r\n"
				+ "        \"subscriptions_url\": \"https://api.github.com/users/tenie/subscriptions\",\r\n"
				+ "        \"organizations_url\": \"https://api.github.com/users/tenie/orgs\",\r\n"
				+ "        \"repos_url\": \"https://api.github.com/users/tenie/repos\",\r\n"
				+ "        \"events_url\": \"https://api.github.com/users/tenie/events{/privacy}\",\r\n"
				+ "        \"received_events_url\": \"https://api.github.com/users/tenie/received_events\",\r\n"
				+ "        \"type\": \"User\",\r\n"
				+ "        \"site_admin\": false\r\n"
				+ "      },\r\n"
				+ "      \"content_type\": \"application/x-deb\",\r\n"
				+ "      \"state\": \"uploaded\",\r\n"
				+ "      \"size\": 67784232,\r\n"
				+ "      \"download_count\": 5,\r\n"
				+ "      \"created_at\": \"2021-12-04T15:53:14Z\",\r\n"
				+ "      \"updated_at\": \"2021-12-04T15:55:56Z\",\r\n"
				+ "      \"browser_download_url\": \"https://github.com/tenie/SQLucky/releases/download/v2.1.0/sqlucky-ubuntu-amd64.deb\"\r\n"
				+ "    },\r\n"
				+ "    {\r\n"
				+ "      \"url\": \"https://api.github.com/repos/tenie/SQLucky/releases/assets/51033278\",\r\n"
				+ "      \"id\": 51033278,\r\n"
				+ "      \"node_id\": \"RA_kwDOEqqS_M4DCrS-\",\r\n"
				+ "      \"name\": \"SQLucky-win64.msi\",\r\n"
				+ "      \"label\": null,\r\n"
				+ "      \"uploader\": {\r\n"
				+ "        \"login\": \"tenie\",\r\n"
				+ "        \"id\": 13869926,\r\n"
				+ "        \"node_id\": \"MDQ6VXNlcjEzODY5OTI2\",\r\n"
				+ "        \"avatar_url\": \"https://avatars.githubusercontent.com/u/13869926?v=4\",\r\n"
				+ "        \"gravatar_id\": \"\",\r\n"
				+ "        \"url\": \"https://api.github.com/users/tenie\",\r\n"
				+ "        \"html_url\": \"https://github.com/tenie\",\r\n"
				+ "        \"followers_url\": \"https://api.github.com/users/tenie/followers\",\r\n"
				+ "        \"following_url\": \"https://api.github.com/users/tenie/following{/other_user}\",\r\n"
				+ "        \"gists_url\": \"https://api.github.com/users/tenie/gists{/gist_id}\",\r\n"
				+ "        \"starred_url\": \"https://api.github.com/users/tenie/starred{/owner}{/repo}\",\r\n"
				+ "        \"subscriptions_url\": \"https://api.github.com/users/tenie/subscriptions\",\r\n"
				+ "        \"organizations_url\": \"https://api.github.com/users/tenie/orgs\",\r\n"
				+ "        \"repos_url\": \"https://api.github.com/users/tenie/repos\",\r\n"
				+ "        \"events_url\": \"https://api.github.com/users/tenie/events{/privacy}\",\r\n"
				+ "        \"received_events_url\": \"https://api.github.com/users/tenie/received_events\",\r\n"
				+ "        \"type\": \"User\",\r\n"
				+ "        \"site_admin\": false\r\n"
				+ "      },\r\n"
				+ "      \"content_type\": \"application/octet-stream\",\r\n"
				+ "      \"state\": \"uploaded\",\r\n"
				+ "      \"size\": 67524282,\r\n"
				+ "      \"download_count\": 9,\r\n"
				+ "      \"created_at\": \"2021-12-05T05:33:42Z\",\r\n"
				+ "      \"updated_at\": \"2021-12-05T05:35:49Z\",\r\n"
				+ "      \"browser_download_url\": \"https://github.com/tenie/SQLucky/releases/download/v2.1.0/SQLucky-win64.msi\"\r\n"
				+ "    },\r\n"
				+ "    {\r\n"
				+ "      \"url\": \"https://api.github.com/repos/tenie/SQLucky/releases/assets/51033308\",\r\n"
				+ "      \"id\": 51033308,\r\n"
				+ "      \"node_id\": \"RA_kwDOEqqS_M4DCrTc\",\r\n"
				+ "      \"name\": \"SQLucky-win64.zip\",\r\n"
				+ "      \"label\": null,\r\n"
				+ "      \"uploader\": {\r\n"
				+ "        \"login\": \"tenie\",\r\n"
				+ "        \"id\": 13869926,\r\n"
				+ "        \"node_id\": \"MDQ6VXNlcjEzODY5OTI2\",\r\n"
				+ "        \"avatar_url\": \"https://avatars.githubusercontent.com/u/13869926?v=4\",\r\n"
				+ "        \"gravatar_id\": \"\",\r\n"
				+ "        \"url\": \"https://api.github.com/users/tenie\",\r\n"
				+ "        \"html_url\": \"https://github.com/tenie\",\r\n"
				+ "        \"followers_url\": \"https://api.github.com/users/tenie/followers\",\r\n"
				+ "        \"following_url\": \"https://api.github.com/users/tenie/following{/other_user}\",\r\n"
				+ "        \"gists_url\": \"https://api.github.com/users/tenie/gists{/gist_id}\",\r\n"
				+ "        \"starred_url\": \"https://api.github.com/users/tenie/starred{/owner}{/repo}\",\r\n"
				+ "        \"subscriptions_url\": \"https://api.github.com/users/tenie/subscriptions\",\r\n"
				+ "        \"organizations_url\": \"https://api.github.com/users/tenie/orgs\",\r\n"
				+ "        \"repos_url\": \"https://api.github.com/users/tenie/repos\",\r\n"
				+ "        \"events_url\": \"https://api.github.com/users/tenie/events{/privacy}\",\r\n"
				+ "        \"received_events_url\": \"https://api.github.com/users/tenie/received_events\",\r\n"
				+ "        \"type\": \"User\",\r\n"
				+ "        \"site_admin\": false\r\n"
				+ "      },\r\n"
				+ "      \"content_type\": \"application/x-zip-compressed\",\r\n"
				+ "      \"state\": \"uploaded\",\r\n"
				+ "      \"size\": 66291567,\r\n"
				+ "      \"download_count\": 19,\r\n"
				+ "      \"created_at\": \"2021-12-05T05:34:36Z\",\r\n"
				+ "      \"updated_at\": \"2021-12-05T05:35:13Z\",\r\n"
				+ "      \"browser_download_url\": \"https://github.com/tenie/SQLucky/releases/download/v2.1.0/SQLucky-win64.zip\"\r\n"
				+ "    }\r\n"
				+ "  ],\r\n"
				+ "  \"tarball_url\": \"https://api.github.com/repos/tenie/SQLucky/tarball/v2.1.0\",\r\n"
				+ "  \"zipball_url\": \"https://api.github.com/repos/tenie/SQLucky/zipball/v2.1.0\",\r\n"
				+ "  \"body\": \"1.改变启动画面\\r\\n2.修复bug\"\r\n"
				+ "}";
		
		var jobj = strToObj(str);
		String val = jobj.getString("tag_name");
		System.out.println(val);
	}
	
}
