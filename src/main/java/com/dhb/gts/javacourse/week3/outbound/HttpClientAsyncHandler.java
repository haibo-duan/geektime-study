package com.dhb.gts.javacourse.week3.outbound;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.List;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpClientAsyncHandler extends HttpOutBoundHandler{

	CloseableHttpAsyncClient httpClient;
	
	public HttpClientAsyncHandler(List<String> backendUrls) {
		super(backendUrls);
		//设置httpClient的配置
		
		IOReactorConfig ioConfig = IOReactorConfig.custom()
				.setConnectTimeout(1000)
				.setSoTimeout(1000)
				.setIoThreadCount(cores)
				.setRcvBufSize(32 * 1024)
				.build();
		httpClient = HttpAsyncClients.custom().setMaxConnTotal(40)
				.setMaxConnPerRoute(8)
				.setDefaultIOReactorConfig(ioConfig)
				.setKeepAliveStrategy((response,context) -> 6000)
				.build();
		httpClient.start();

	}

	@Override
	 void fetchGet(final FullHttpRequest inbound, final ChannelHandlerContext ctx, final String url) {
		final HttpGet httpGet = new HttpGet(url);

		httpGet.setHeader(HTTP.CONN_DIRECTIVE, HTTP.CONN_KEEP_ALIVE);
//		httpGet.setHeader("mao", inbound.headers().get("mao"));

		httpClient.execute(httpGet, new FutureCallback<HttpResponse>() {
			@Override
			public void completed(HttpResponse result) {
				handleResponse(inbound,ctx,result);
			}

			@Override
			public void failed(Exception ex) {
				httpGet.abort();
				ex.printStackTrace();
			}

			@Override
			public void cancelled() {
				httpGet.abort();
			}
		});

	}

	public void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final HttpResponse endpointResponse) {
		FullHttpResponse response = null;
		try{
			byte[] body = EntityUtils.toByteArray(endpointResponse.getEntity());

			response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));

			response.headers().set("Content-Type", "application/json");
			response.headers().setInt("Content-Length", Integer.parseInt(endpointResponse.getFirstHeader("Content-Length").getValue()));

		} catch (Exception e) {
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
			//ctx.close();
		}

	}
}
