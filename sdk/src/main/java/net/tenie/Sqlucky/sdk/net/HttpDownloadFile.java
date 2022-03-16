package net.tenie.Sqlucky.sdk.net;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;

public class HttpDownloadFile {
	
	
	public static void demo0() {


        //创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //以Post方式访问
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
		demo0();
	}
}
