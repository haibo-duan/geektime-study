package com.dhb.gts.javacourse.week3.filter;

import io.netty.handler.codec.http.FullHttpResponse;

public class HeaderHttpResponseFilter implements HttpResponseFilter {
    @Override
    public boolean filter(FullHttpResponse response,ResponseFilterChain chain) {
        if(response.headers().contains("xjava")){
            response.headers().remove("xjava");
        }
        if(!response.headers().contains("token")){
            response.headers().remove("12345678");
        }
        chain.filter(response,chain);
        return true;
    }
}

