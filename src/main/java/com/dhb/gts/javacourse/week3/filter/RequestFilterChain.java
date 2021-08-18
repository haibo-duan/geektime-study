package com.dhb.gts.javacourse.week3.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.ArrayList;
import java.util.List;

public class RequestFilterChain implements HttpRequestFilter{
	
	List<HttpRequestFilter> filters = new ArrayList<>();
	int index = 0;
	
	public RequestFilterChain add(HttpRequestFilter filter) {
		filters.add(filter);
		return this;
	}

	@Override
	public boolean filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx, RequestFilterChain chain) {
		if(index == filters.size()) return false;
		HttpRequestFilter filter = filters.get(index);
		index ++;
		
		return filter.filter(fullRequest,ctx,chain);
	}
}
