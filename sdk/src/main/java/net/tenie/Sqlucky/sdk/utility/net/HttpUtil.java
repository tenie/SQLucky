package net.tenie.Sqlucky.sdk.utility.net;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.message.StatusLine;
import org.apache.hc.core5.net.URIBuilder;

public class HttpUtil {

	/**
	 * post 文件和参数
	 * @param url
	 * @param filePath
	 * @param strPamas
	 */
	public static void postFileAndPamas(String url, String filePath, Map<String, String> strPamas) {
		try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
			HttpPost httppost = new HttpPost(url);

			FileBody bin = new FileBody(new File(filePath));
			MultipartEntityBuilder meb = MultipartEntityBuilder.create().addPart("file", bin);

			if (strPamas != null) {
				strPamas.forEach((key, val) -> {
					StringBody valBody = new StringBody(val, ContentType.TEXT_PLAIN);
					meb.addPart(key, valBody);
				});
			}
			HttpEntity reqEntity = meb.build();

			httppost.setEntity(reqEntity);

			System.out.println("executing request " + httppost);
			httpclient.execute(httppost, response -> {
				System.out.println("----------------------------------------");
				System.out.println(httppost + "->" + new StatusLine(response));
				final HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					System.out.println("Response content length: " + resEntity.getContentLength());
				}
				EntityUtils.consume(response.getEntity());
				return null;
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	 
	
	/**
	 * post文件, 并且可以传多个文本参数
	 * @param url
	 * @param filepath
	 * @param Param
	 * @throws IOException
	 */
	public static void postFile(String url, String filepath, Map<String, String> Param) throws IOException {

		try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
			final HttpPost httppost = new HttpPost(url);

			final FileBody bin = new FileBody(new File(filepath));

			MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
			entityBuilder.addPart("file", bin);
			for (String keyStr : Param.keySet()) {
				String valStr = Param.get(keyStr);
				final StringBody tmpBody = new StringBody(valStr, ContentType.TEXT_PLAIN);
				entityBuilder.addPart(keyStr, tmpBody);
			}
			final HttpEntity reqEntity = entityBuilder.build();
			httppost.setEntity(reqEntity);

			System.out.println("executing request " + httppost);
			httpclient.execute(httppost, response -> {
				System.out.println("----------------------------------------");
				System.out.println(httppost + "->" + new StatusLine(response));
				final HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					System.out.println("Response content length: " + resEntity.getContentLength());
				}
				EntityUtils.consume(response.getEntity());
				return null;
			});
		}
	}
	/**
	 * post 文件
	 * @param url
	 * @param user
	 * @param password
	 * @param filepath
	 * @throws IOException
	 */
	public static void postFile(String url, String user, String password, String filepath) throws IOException {

		try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
			final HttpPost httppost = new HttpPost(url);

			final FileBody bin = new FileBody(new File(filepath));
			final StringBody userVal = new StringBody(user, ContentType.TEXT_PLAIN);
			final StringBody passwordVal = new StringBody(password, ContentType.TEXT_PLAIN);

			final HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", bin).addPart("user", userVal)
					.addPart("password", passwordVal).build();

			httppost.setEntity(reqEntity);

			System.out.println("executing request " + httppost);
			httpclient.execute(httppost, response -> {
				System.out.println("----------------------------------------");
				System.out.println(httppost + "->" + new StatusLine(response));
				final HttpEntity resEntity = response.getEntity();
				if (resEntity != null) {
					System.out.println("Response content length: " + resEntity.getContentLength());
				}
				EntityUtils.consume(response.getEntity());
				return null;
			});
		}
	}
	
	// 普通get
	public static String get(String url) {
		String resultContent = null;
		HttpGet httpGet = new HttpGet(url);
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
				// 获取状态码
				System.out.println(response.getVersion()); // HTTP/1.1
				System.out.println(response.getCode()); // 200
				System.out.println(response.getReasonPhrase()); // OK
				HttpEntity entity = response.getEntity();
				// 获取响应信息
				resultContent = EntityUtils.toString(entity);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return resultContent;
	}
	/**
	 * 简洁版 get
	 * @param url
	 * @return
	 */
	public static String get2(String url) {
		String result = null;
		try {
			Response response = Request.get(url).execute();
			result = response.returnContent().asString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 带参数的get
	 * 
	 * @param url
	 * @param user
	 * @param password
	 * @return
	 */
	public static String get(String url, String user, String password) {
		String resultContent = null;
		HttpGet httpGet = new HttpGet(url);
		// 表单参数
		List<NameValuePair> nvps = new ArrayList<>();
		// GET 请求参数
		nvps.add(new BasicNameValuePair("username", "wdbyte.com"));
		nvps.add(new BasicNameValuePair("password", "secret"));
		// 增加到请求 URL 中
		try {
			URI uri = new URIBuilder(new URI(url)).addParameters(nvps).build();
			httpGet.setUri(uri);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
				// 获取状态码
				System.out.println(response.getVersion()); // HTTP/1.1
				System.out.println(response.getCode()); // 200
				System.out.println(response.getReasonPhrase()); // OK
				HttpEntity entity = response.getEntity();
				// 获取响应信息
				resultContent = EntityUtils.toString(entity);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return resultContent;
	}
	
	/**
	 * 
	 * @param url
	 * @return
	 */
	public static String post1(String url, Map<String, String> strPamas) {
		String result = null;
		HttpPost httpPost = new HttpPost(url);
		// 表单参数
		List<NameValuePair> nvps = new ArrayList<>();
		// POST 请求参数
		
		for(String key : strPamas.keySet()) {
			nvps.add(new BasicNameValuePair(key, strPamas.get(key)));
		}
		
//		nvps.add(new BasicNameValuePair("password", "secret"));
		httpPost.setEntity(new UrlEncodedFormEntity(nvps));
		try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
			try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
				System.out.println(response.getVersion()); // HTTP/1.1
				System.out.println(response.getCode()); // 200
				System.out.println(response.getReasonPhrase()); // OK

				HttpEntity entity = response.getEntity();
				// 获取响应信息
				result = EntityUtils.toString(entity);
				// 确保流被完全消费
				EntityUtils.consume(entity);
			}
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 简洁版本的post
	 * @param url
	 * @return
	 */
	public static String post(String url) {
        String result = null;
        Request request = Request.post(url);
        // POST 请求参数
        request.bodyForm(
            new BasicNameValuePair("BACKUP_NAME", "111"),
            new BasicNameValuePair("EMAIL", "tenie@tenie.net"),
            new BasicNameValuePair("PASSWORD", "mima"));
        try {
            result = request.execute().returnContent().asString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

 

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
//    	demo1();
//    	demo2();
//		demo_post();
		String val = post("http://127.0.0.1:8088/sqlucky/queryAllBackup");
		System.out.println("val = " + val);
	}
}
