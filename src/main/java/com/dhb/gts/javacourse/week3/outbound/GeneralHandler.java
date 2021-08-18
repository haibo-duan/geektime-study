package com.dhb.gts.javacourse.week3.outbound;

import com.dhb.gts.javacourse.week3.filter.HttpResponseFilter;
import com.dhb.gts.javacourse.week3.filter.ResponseFilterChain;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;

import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class GeneralHandler extends ChannelInboundHandlerAdapter {

	private final FullHttpRequest fullRequest;
	private final ChannelHandlerContext inboundCtx;
	private final Map<String, String> head = new HashMap<>();
	private final ResponseFilterChain responseChain;
	private Integer resLength = 0;
	private String respContent = "";

	public GeneralHandler(FullHttpRequest fullRequest, ChannelHandlerContext inboundCtx, ResponseFilterChain responseChain) {
		this.fullRequest = fullRequest;
		this.inboundCtx = inboundCtx;
		this.responseChain = responseChain;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof HttpResponse) {
			HttpResponse response = (HttpResponse) msg;
			for (Map.Entry entry : response.headers().entries()) {
				head.put((String) entry.getKey(), (String) entry.getValue());
			}
			if (response.headers().get("Content-Length") != null) {
				resLength = Integer.parseInt(response.headers().get("Content-Length"));
			}
		}

		if (msg instanceof HttpContent) {
			HttpContent content = (HttpContent) msg;
			ByteBuf buf = content.content();
			respContent += buf.toString(CharsetUtil.UTF_8);
			System.out.println("*************" + buf.toString(io.netty.util.CharsetUtil.UTF_8));
			((HttpContent) msg).release();
			if (respContent.getBytes().length >= resLength || !buf.isReadable()) {
				ctx.channel().close();
				handleResponse(fullRequest, inboundCtx);
			}
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		Channel channel = ctx.channel();
		if (channel.isActive()) {
			System.out.println("出现异常，关闭连接。");
			channel.close();
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	public void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) {
		FullHttpResponse response = null;
		try {
			byte[] body = respContent.getBytes();
			System.out.println("GeneralHandler response:" + respContent);
			response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));

			if (head.isEmpty()) {
				response.headers().set("Content-Type", "application/json");
				response.headers().setInt("Content-Length", resLength);
			} else {
				for (Map.Entry<String, String> entry : head.entrySet()) {
					response.headers().set(entry.getKey(), entry.getValue());
				}
			}
			response.headers().setInt("Content-Length", resLength);
		} catch (Exception e) {
			e.printStackTrace();
			response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
			ctx.close();
		} finally {
			responseChain.filter(response,responseChain);
			if (fullRequest != null) {
				if (!HttpUtil.isKeepAlive(fullRequest)) {
					ctx.write(response).addListener(ChannelFutureListener.CLOSE);
				} else {
					//response.headers().set(CONNECTION, KEEP_ALIVE);
					ctx.write(response);
				}
			}
			ctx.flush();
		}
	}
}
