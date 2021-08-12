package com.dhb.gts.javacourse.week2;


import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.TimeUnit;

/**
*@author haibo.duan@100credit.com
*@description 
*@date  2021/8/12 20:15
*/
public class HttpHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		try {
			FullHttpRequest fullRequest = (FullHttpRequest) msg;
			String uri = fullRequest.uri();
			if(uri.contains("/test") ) {
				handlerTest(fullRequest,ctx,"Hello ...");
			}else {
				handlerTest(fullRequest,ctx,"other ...");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}
	
	private void handlerTest(FullHttpRequest fullRequest,ChannelHandlerContext ctx,String responseBody) {
		FullHttpResponse response = null;
		
		try {
			String value = responseBody;
			TimeUnit.MILLISECONDS.sleep(1);
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.wrappedBuffer(value.getBytes()));
			response.headers().set("Content-Type","application/json");
			response.headers().setInt("Content-Length",response.content().readableBytes());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("处理出错："+e.getMessage());
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.NO_CONTENT);
		} finally {
			if(fullRequest != null) {
				if(!HttpUtil.isKeepAlive(fullRequest)) {
					ctx.write(response).addListener(ChannelFutureListener.CLOSE);
				}else {
					response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
					ctx.write(response);
				}
			}
		}

	}
}
