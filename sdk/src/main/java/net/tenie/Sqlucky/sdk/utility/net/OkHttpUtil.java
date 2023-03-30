package net.tenie.Sqlucky.sdk.utility.net;

import java.util.Map;

import net.tenie.Sqlucky.sdk.utility.JsonTools;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtil {
 	 
		// POST 请求参数
	public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

	public static String post1(String url, Map strPamas) throws Exception {
		String json =JsonTools.mapToJson(strPamas);
		OkHttpClient client = new OkHttpClient();
		RequestBody body = RequestBody.create(json, JSON);
		  Request request = new Request.Builder()
		      .url(url)
		      .post(body)
		      .build();
		  try (Response response = client.newCall(request).execute()) {
		    return response.body().string();
		  }
	}
	 
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
	}
}
