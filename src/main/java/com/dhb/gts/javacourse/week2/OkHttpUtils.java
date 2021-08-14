package com.dhb.gts.javacourse.week2;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class OkHttpUtils {

	public static void main(String[] args) {
		System.out.println(OkHttpUtils.httpGet("http://127.0.0.1:8801"));
	}
	
	public static String httpGet(String url) {
		OkHttpClient client = new OkHttpClient();
		String result = null;
		try {
			Request request = new Request.Builder().url(url).build();
			Response response = client.newCall(request).execute();
			if(response.isSuccessful()){
				result = response.body().string();
			} else {
				System.out.println("请求失败。。。");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String httpPost(String url,String strParam) {
		OkHttpClient client = new OkHttpClient();
		String result = null;
		try {
			MediaType type = MediaType.Companion.parse("application/json;charset=utf-8");
			//create方法已经过时 用Compaion来解决
			RequestBody body = RequestBody.Companion.create(strParam,type);
			Request request = new Request.Builder()
					.url(url)
					.post(body)
					.build();
			Response response = client.newCall(request).execute();
			if(response.isSuccessful()) {
				result = response.body().string();
			}else {
				System.out.println("请求失败。。。");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
