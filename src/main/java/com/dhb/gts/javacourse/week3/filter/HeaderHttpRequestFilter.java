package com.dhb.gts.javacourse.week3.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class HeaderHttpRequestFilter implements HttpRequestFilter{
	

	@Override
	public boolean filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx,RequestFilterChain chain) {
		if(!fullRequest.headers().contains("token")){
			fullRequest.headers().set("token","12345678");
		}
		if(!fullRequest.headers().contains("xjava")){
			fullRequest.headers().set("xjava","ok");
		}
		chain.filter(fullRequest,ctx,chain);
		return true;
	}
}
