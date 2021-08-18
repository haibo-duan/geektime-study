package com.dhb.gts.javacourse.week3.inbound;

import com.dhb.gts.javacourse.week3.filter.HeaderHttpRequestFilter;
import com.dhb.gts.javacourse.week3.filter.HttpRequestFilter;
import com.dhb.gts.javacourse.week3.filter.RequestFilterChain;
import com.dhb.gts.javacourse.week3.outbound.HttpClientAsyncHandler;
import com.dhb.gts.javacourse.week3.outbound.HttpOutBoundHandler;
import com.dhb.gts.javacourse.week3.outbound.NettyAsyncHandler;
import com.dhb.gts.javacourse.week3.outbound.OkHttpAsyncHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {
	
	private final List<String> proxyServer;
	private HttpOutBoundHandler handler;
	private RequestFilterChain requestChain = new RequestFilterChain();

	public HttpInboundHandler(List<String> proxyServer) {
		this.proxyServer = proxyServer;
		this.handler = new NettyAsyncHandler(proxyServer);
		requestChain.add(new HeaderHttpRequestFilter());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		FullHttpRequest fullRequest = (FullHttpRequest) msg;
		handler.handle(fullRequest, ctx, requestChain);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
}
