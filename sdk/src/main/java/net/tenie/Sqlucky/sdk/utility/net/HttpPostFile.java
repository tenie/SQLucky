package net.tenie.Sqlucky.sdk.utility.net;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
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
 

public class HttpPostFile {
 
	public static void exec(String url, String filePath, Map<String, String> strPamas) {
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httppost = new HttpPost(url);

            FileBody bin = new FileBody(new File( filePath ));
            MultipartEntityBuilder meb =  MultipartEntityBuilder.create().addPart("file", bin);
           
            if(strPamas != null ) {
            	strPamas.forEach( (key, val)->{
                	 StringBody valBody = new StringBody(val , ContentType.TEXT_PLAIN);
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
        }catch(IOException e) {
        	e.printStackTrace();
        }
 
	}
	
	public static void post(String url, String user, String password, String filepath) throws IOException {
		  
        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
            final HttpPost httppost = new HttpPost(url);

            final FileBody bin = new FileBody(new File(filepath));
            final StringBody userVal = new StringBody(user, ContentType.TEXT_PLAIN);
            final StringBody passwordVal = new StringBody(password, ContentType.TEXT_PLAIN);

            final HttpEntity reqEntity = MultipartEntityBuilder.create()
                    .addPart("bin", bin)
                    .addPart("user", userVal)
                    .addPart("password", passwordVal)
                    .build();


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
	public static void demo_post() {
		try {
			post("http://127.0.0.1:8088/sqlucky/confUpload", "tenie", "mima", "D:\\myGit\\SQLucky\\TODO.md");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void demo2() throws IOException {
		  
	        try (final CloseableHttpClient httpclient = HttpClients.createDefault()) {
	            final HttpPost httppost = new HttpPost("http://127.0.0.1:8088/sqlucky/login");

	            final FileBody bin = new FileBody(new File("D:\\myGit\\SQLucky\\TODO.md"));
	            final StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);

	            final HttpEntity reqEntity = MultipartEntityBuilder.create()
	                    .addPart("bin", bin)
	                    .addPart("comment", comment)
	                    .build();


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
	
	
	public static void demo1() {
		 try {
	            String content = Request.post("http://127.0.0.1:8088/sqlucky/login")
	                    .bodyForm(Form.form().add("username", "vip").add("password", "secret").build())
	                    .execute().returnContent().asString();
	            
	            System.out.println("Content: {}"+ content);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
	

	public static void demo0() {


        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //以Post方式访问
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8088/sqlucky/login");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("username", "vip"));
        nvps.add(new BasicNameValuePair("password", "secret"));
        
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        try {
            //执行
            CloseableHttpResponse response = httpClient.execute(httpPost);

//            log.info("状态码: {}\t\t{}", response.getCode(), response.getReasonPhrase());

            HttpEntity entity = response.getEntity();

            //获取响应数据
            String content = EntityUtils.toString(entity);
//            log.info("content: {}", content);

            //清理
            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    
	}
    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
    	
//    	demo2();
    	demo_post();
    }
}
