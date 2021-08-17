package com.dhb.gts.javacourse.week3.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface HttpRequestFilter {
	
	void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx);
}
