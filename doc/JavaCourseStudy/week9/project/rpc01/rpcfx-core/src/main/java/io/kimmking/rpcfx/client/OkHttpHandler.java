package io.kimmking.rpcfx.client;

import com.alibaba.fastjson.JSON;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResponse;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class OkHttpHandler extends RequestHandler {

	OkHttpClient httpClient;

	public static final MediaType JSONTYPE = MediaType.get(MEDIA_TYPE);

	public OkHttpHandler() {
		httpClient = new OkHttpClient.Builder()
				.connectTimeout(1000, TimeUnit.MILLISECONDS)
				.callTimeout(1000, TimeUnit.MILLISECONDS)
				.readTimeout(1000, TimeUnit.MILLISECONDS)
				.writeTimeout(1000, TimeUnit.MILLISECONDS)
				.build();
		httpClient.dispatcher().setMaxRequests(CORES);
	}

	@Override
	public RpcfxResponse handler(RpcfxRequest rpcfxRequest, String url) throws IOException {
		String reqJson = JSON.toJSONString(rpcfxRequest);
		System.out.println("req json: " + reqJson);
		// 1.可以复用client
		// 2.尝试使用httpclient或者netty client
		final Request request = new Request.Builder()
				.url(url)
				.post(RequestBody.create(JSONTYPE, reqJson))
				.build();
		String respJson = httpClient.newCall(request).execute().body().string();
		System.out.println("resp json: " + respJson);
		return JSON.parseObject(respJson, RpcfxResponse.class);
	}
}
