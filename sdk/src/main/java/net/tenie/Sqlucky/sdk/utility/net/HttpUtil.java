package net.tenie.Sqlucky.sdk.utility.net;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
/**
 * 
 * demo : https://github.com/apache/httpcomponents-client/blob/master/httpclient5-fluent/src/test/java/org/apache/hc/client5/http/examples/fluent/FluentExecutor.java
 * @author tenie
 *
 */
public class HttpUtil {
	/**
	 * 
	 * @param url 
	 * @return
	 * @throws Exception 
	 */
	public static String post(String url, Map<String, String> Param) throws Exception {
		// 表单参数
		var nvps = mapToPairs(Param);
		String content = Request.post(url)
		        .bodyForm(nvps)
		        .execute().returnContent().asString();
		return content;
	}
	  
	
	/**
	 * post文件, 并且可以传多个文本参数
	 * @param url
	 * @param filepath
	 * @param Param
	 * @throws IOException
	 */
	public static void postFile(String url, String filepath, Map<String, String> Param) throws IOException {
		FileBody bin = new FileBody(new File(filepath));
		MultipartEntityBuilder meb = MultipartEntityBuilder.create().addPart("file", bin);

		if (Param != null) {
			Param.forEach((key, val) -> {
				StringBody valBody = new StringBody(val, ContentType.TEXT_PLAIN);
				meb.addPart(key, valBody);
			});
		}
		HttpEntity entity = meb.build();
		
		String html = Request.post(url)
		        .body(entity)
		        .execute().returnContent().asString();
	}
	 
	 
	/**
	 * 简洁版 get
	 * @param url
	 * @return
	 */
	public static String get(String url) {
		String result = null;
		try {
			Response response = Request.get(url).execute();
			result = response.returnContent().asString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
 
	// 使用post下载插件文件, 到指定目录
	public static String downloadPluginByPostToDir(String url, String fileSaveDir, Map<String, String> Param) {
		var nvps = mapToPairs(Param);
		String name = "";
		 try {
			 Response re =	Request.post(url).bodyForm(nvps).execute();
			 name = re.handleResponse(response -> {
				 		String sqluckyPluginName = response.getHeader("SQLUCKY_PLUGIN").getValue();
				 		HttpEntity entity = response.getEntity(); 
			            
			            byte[] data =  EntityUtils.toByteArray(entity);
			            String fileName = fileSaveDir + "/" + sqluckyPluginName;
			            Files.write(Paths.get(fileName), data);

			            //清理
			            EntityUtils.consume(entity);
				 	
			            return fileName;
				 });
		} catch (IOException e) {
			e.printStackTrace();
			
		}
		return name;
	}
	
	// 使用post下载插件文件, 到指定目录
		public static String downloadAppPatchByPostToDir(String url, String fileSaveDir, Map<String, String> Param) {
			var nvps = mapToPairs(Param);
			String name = "";
			 try {
				 Response re =	Request.post(url).bodyForm(nvps).execute();
				 name = re.handleResponse(response -> {
					 		String sqluckyPluginName = response.getHeader("SQLUCKY_PATH").getValue();
					 		HttpEntity entity = response.getEntity(); 
				            
				            byte[] data =  EntityUtils.toByteArray(entity);
				            String fileName = fileSaveDir + "/" + sqluckyPluginName;
				            Files.write(Paths.get(fileName), data);

				            //清理
				            EntityUtils.consume(entity);
					 	
				            return fileName;
					 });
			} catch (IOException e) {
				e.printStackTrace();
				
			}
			return name;
		}
	
	// 使用post下载文件
	public static void downloadByPost(String url, String filepath, Map<String, String> Param) {
		var nvps = mapToPairs(Param);
		 try {
			Request.post(url).bodyForm(nvps).execute().saveContent(new File(filepath));
			
		} catch (IOException e) {
			e.printStackTrace();
			
		}
	}
	
	private static List<NameValuePair>  mapToPairs( Map<String, String> Param){
		// 表单参数
		List<NameValuePair> nvps = new ArrayList<>();
		// POST 请求参数
		
		for(String key : Param.keySet()) {
			BasicNameValuePair tmp = new BasicNameValuePair(key, Param.get(key));
			nvps.add(tmp);
		}
		return nvps;
	}
	 

	private static String post1_test() throws Exception {
		Map<String, String> pamas = new HashMap<>();
		pamas.put("EMAIL", "tenie@tenie.net");
		pamas.put("PASSWORD", "mima");
		
		String val = HttpUtil.post("http://127.0.0.1:8088/sqlucky/queryAllBackup", pamas);
		return val;
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
}
