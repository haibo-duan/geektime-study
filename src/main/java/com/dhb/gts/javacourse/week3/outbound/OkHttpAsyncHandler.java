package com.dhb.gts.javacourse.week3.outbound;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class OkHttpAsyncHandler extends HttpOutBoundHandler{

	OkHttpClient httpClient;
	
	public OkHttpAsyncHandler(List<String> backendUrls) {
		super(backendUrls);
		
		httpClient = new OkHttpClient.Builder()
		.connectTimeout(1000, TimeUnit.MILLISECONDS)
				.callTimeout(1000, TimeUnit.MILLISECONDS)
				.readTimeout(1000, TimeUnit.MILLISECONDS)
				.writeTimeout(1000, TimeUnit.MILLISECONDS)
		.build();
		
		httpClient.dispatcher().setMaxRequests(cores);
	}

	@Override
	void fetchGet(FullHttpRequest inbound, ChannelHandlerContext ctx, String url) {
		Request request = new Request.Builder()
				.url(url)
				.build();
		httpClient.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(@NotNull Call call, @NotNull IOException e) {
				e.printStackTrace();
			}

			@Override
			public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
				handleResponse(inbound,ctx,response);
			}
		});
	}

	public void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final Response endpointResponse) {
		FullHttpResponse response = null;
		try {
			byte[] body  = endpointResponse.body().bytes();
			response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));

			response.headers().set("Content-Type", "application/json");
			response.headers().setInt("Content-Length", Integer.parseInt(endpointResponse.header("Content-Length")));
		} catch (IOException e) {
			e.printStackTrace();
			response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
			exceptionCaught(ctx, e);
		} finally {
			if (fullRequest != null) {
				if (!HttpUtil.isKeepAlive(fullRequest)) {
					ctx.write(response).addListener(ChannelFutureListener.CLOSE);
				} else {
					//response.headers().set(CONNECTION, KEEP_ALIVE);
					ctx.write(response);
				}
			}
			ctx.flush();
		}
	}
}
