package net.tenie.Sqlucky.sdk.utility.net;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

public class HttpDownloadFile {
	
	//"http://127.0.0.1:8088/sqlucky/confDownload"
	public static void getInfo(String url, Map<String, String> strPamas) {

       
		//创建HttpClient对象
        try( CloseableHttpClient httpClient = HttpClients.createDefault()){
        	
            //以Get方式访问
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<>();
            if(strPamas != null ) {
            	strPamas.forEach( (key, val)->{
            		  nvps.add(new BasicNameValuePair(key, val)); 
                });
            }
            
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            //执行
            CloseableHttpResponse response = httpClient.execute(httpPost);

            System.out.println("状态码: "+ response.getCode() +" | "+ response.getReasonPhrase());

            HttpEntity entity = response.getEntity(); 
            
//            byte[] data =  EntityUtils.toByteArray(entity);
//            Files.write(Paths.get("D:\\file.txt"), data);

            //获取响应数据
            String content = EntityUtils.toString(entity);
            System.out.println("content:=" + content);

            //清理
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
	}
	
	public static void demo0() {


        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //以Get方式访问
        HttpGet httpPost = new HttpGet("http://127.0.0.1:8088/sqlucky/confDownload");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("username", "vip"));
        nvps.add(new BasicNameValuePair("password", "secret"));
        
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));

        try {
            //执行
            CloseableHttpResponse response = httpClient.execute(httpPost);

//            log.info("状态码: {}\t\t{}", response.getCode(), response.getReasonPhrase());

            HttpEntity entity = response.getEntity(); 
            byte[] data =  EntityUtils.toByteArray(entity);
            Files.write(Paths.get("D:\\file.txt"), data);

            //获取响应数据
//            String content = EntityUtils.toString(entity);
//            log.info("content: {}", content);

            //清理
            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        catch (ParseException e) {
//            e.printStackTrace();
//        }
    
	}
	
	public static void main(String[] args) {
//		demo0();
		Map<String , String> hm = new HashMap<>();
    	hm.put("userName", "sa");
    	hm.put("password", "123");
    	hm.put("configName", "h2db4.mv.dbs");
    	
		HttpDownloadFile.getInfo("http://127.0.0.1:8088/sqlucky/confInfo", hm);
	}
}
