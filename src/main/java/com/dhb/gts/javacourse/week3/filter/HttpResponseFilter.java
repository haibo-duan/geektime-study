package com.dhb.gts.javacourse.week3.filter;

import io.netty.handler.codec.http.FullHttpResponse;

public interface HttpResponseFilter {

    boolean filter(FullHttpResponse response,ResponseFilterChain chain);

}
