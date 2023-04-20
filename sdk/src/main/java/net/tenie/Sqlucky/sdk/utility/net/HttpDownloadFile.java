package net.tenie.Sqlucky.sdk.utility.net;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSocket;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
//import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.ssl.SSLContexts;

public class HttpDownloadFile {
	
	//"http://127.0.0.1:8088/sqlucky/confDownload"
	public static void getInfo2(String url, Map<String, String> strPamas, String saveFile) {
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(SSLContexts.createDefault()){

		    @Override
		    protected void prepareSocket(SSLSocket socket) {

		        String hostname = socket.getInetAddress().getHostName();
		        if (hostname.endsWith("internal.system.com")){
		            socket.setEnabledProtocols(new String[] { "TLSv1", "TLSv1.1", "TLSv1.2", "TLSv1.3" });
		        }
		        else {
		            socket.setEnabledProtocols(new String[] {"TLSv1.3"});
		        }
		    }
		}; 
//		HttpClients.custom().
//		CloseableHttpClient httpClient2 = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		  
       
		//创建HttpClient对象
//        try( httpClient){
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

//            System.out.println("状态码: "+ response.getCode() +" | "+ response.getReasonPhrase());

            HttpEntity entity = response.getEntity(); 
            
            byte[] data =  EntityUtils.toByteArray(entity);
            Files.write(Paths.get(saveFile), data);
            
            //获取响应数据
//            String content = EntityUtils.toString(entity);
//            System.out.println("content:=" + content);

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
    
	}
	
 
}
