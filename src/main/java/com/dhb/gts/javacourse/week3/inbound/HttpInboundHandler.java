package com.dhb.gts.javacourse.week3.inbound;

import com.dhb.gts.javacourse.week3.filter.HeaderHttpRequestFilter;
import com.dhb.gts.javacourse.week3.filter.HttpRequestFilter;
import com.dhb.gts.javacourse.week3.outbound.HttpOutBoundHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {
	
	private final List<String> proxyServer;
	private HttpOutBoundHandler handler;
	private HttpRequestFilter filter = new HeaderHttpRequestFilter();

	public HttpInboundHandler(List<String> proxyServer) {
		this.proxyServer = proxyServer;
		this.handler = new HttpOutBoundHandler(proxyServer);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		FullHttpRequest fullRequest = (FullHttpRequest) msg;
		handler.handle(fullRequest, ctx, filter);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
}
