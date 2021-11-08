package com.dhb.kmq.v3.demo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpClientUtils {
	

	public static void main(String[] args) {
		System.out.println(HttpClientUtils.httpGet("http://127.0.0.1:8801"));
	}
	
	private static RequestConfig config = null;
	static {
		config = RequestConfig.custom().setSocketTimeout(10000)
				.setConnectTimeout(10000)
				.setConnectionRequestTimeout(10000)
				.build();
	}
	
	public static String httpGet(String url) {

		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet request = new HttpGet(url);
		request.setConfig(config);
		String result = null;
		try {
			CloseableHttpResponse response = client.execute(request);

			//请求成功
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity,"utf-8");
			}else {
				System.out.println("请求失败！");
			}
		} catch (IOException e) {
			System.out.println("提交失败！");
		} finally {
			request.releaseConnection();
		}
		return result;
	}
	
	public static String httpPost(String url,String strParam) {
		String result = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(config);

		try {
			if(null != strParam) {
				StringEntity entity = new StringEntity(strParam,"utf-8");
				entity.setContentEncoding("UTF-8");
				entity.setContentType("application/x-www-form-urlencoded");
				httpPost.setEntity(entity);
			}
			CloseableHttpResponse response = httpClient.execute(httpPost);
			//请求发送成功
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(),"utf-8");
			}else {
				System.out.println("请求失败！url is ["+url+"] 请求错误类型："+response.getStatusLine().getStatusCode() );
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("请求异常,"+e.getMessage());
		} finally {
			httpPost.releaseConnection();
		}
		return result;
	}

}
