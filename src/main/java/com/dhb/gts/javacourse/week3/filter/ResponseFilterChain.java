package com.dhb.gts.javacourse.week3.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.util.ArrayList;
import java.util.List;

public class ResponseFilterChain implements HttpResponseFilter{
	
	List<HttpResponseFilter> filters = new ArrayList<>();
	int index = 0;
	
	public ResponseFilterChain add(HttpResponseFilter filter) {
		filters.add(filter);
		return this;
	}
	
	
	
	@Override
	public boolean filter(FullHttpResponse response, ResponseFilterChain chain) {
		if(index == filters.size()) return false;
		HttpResponseFilter filter = filters.get(index);
		index ++;
		
		return filter.filter(response,chain);
	}
}
