package io.kimmking.rpcfx.client;

import com.alibaba.fastjson.JSON;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class NettyHandler extends RequestHandler{

	private final EventLoopGroup workGroup;
	private final Bootstrap bootstrap;
	


	public NettyHandler() {
		workGroup = new NioEventLoopGroup(1);
		bootstrap = new Bootstrap();
		bootstrap.group(workGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
		bootstrap.option(ChannelOption.SO_RCVBUF, 1024 * 1024);
	}

	@Override
	public RpcfxResponse handler(RpcfxRequest rpcfxRequest, String url) throws Exception {
		try {
			String reqJson = JSON.toJSONString(rpcfxRequest);
			ByteBuf bbuf = Unpooled.copiedBuffer(reqJson, StandardCharsets.UTF_8);
			URI uri = new URI(url);
			String domain = uri.getHost();
			Integer port = uri.getPort();
			DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uri.toASCIIString());
			//如果请求的header为空 则构造header 反之将inbound请求的header传递到endpoint
			request.headers().add(HttpHeaderNames.HOST, domain);
			request.headers().add(HttpHeaderNames.USER_AGENT, "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:44.0) Gecko/20100101 Firefox/44.0");
			request.headers().add(HttpHeaderNames.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			request.headers().add(HttpHeaderNames.ACCEPT_LANGUAGE, "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
			request.headers().add(HttpHeaderNames.CONNECTION, "keep-alive");
			request.headers().add(HttpHeaderNames.CACHE_CONTROL, "max-age=0");
			request.headers().add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
			request.headers().add(HttpHeaderNames.CONTENT_LENGTH,bbuf.readableBytes());
			request.content().clear().writeBytes(bbuf);
			String respJson = "";
			GeneralHandler generalHandler = new GeneralHandler(Thread.currentThread());
			ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					//收到http响应 解码
					ch.pipeline().addLast(new HttpResponseDecoder());
					//发送的时候 通过http编码
					ch.pipeline().addLast(new HttpRequestEncoder());
					ch.pipeline().addLast(generalHandler);
				}
			};
			bootstrap.handler(initializer);
			ChannelFuture f = bootstrap.connect(domain, port).sync();
			f.channel().writeAndFlush(request);
//			LockSupport.park();
			log.info("RpcfxResponse handler parking ......");
			f.channel().closeFuture().sync();
			return JSON.parseObject(generalHandler.getRespContent(), RpcfxResponse.class);
		} catch (Exception e) {
			e.printStackTrace();
			workGroup.shutdownGracefully();
			throw e;
		}
	}
	
	private static class GeneralHandler extends ChannelInboundHandlerAdapter{

		private Integer resLength = 0;
		private String respContent = "";

		private Thread requestThread;
		public GeneralHandler(final Thread requestThread) {
			this.requestThread = requestThread;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			if (msg instanceof HttpResponse) {
				HttpResponse response = (HttpResponse) msg;
				if (response.headers().get("Content-Length") != null) {
					resLength = Integer.parseInt(response.headers().get("Content-Length"));
					log.info("Content-Length is : {}",resLength);
				}
			}
			if (msg instanceof HttpContent) {
				HttpContent content = (HttpContent) msg;
				ByteBuf buf = content.content();
				respContent += buf.toString(CharsetUtil.UTF_8);
				((HttpContent) msg).release();
				if (respContent.getBytes().length >= resLength || !buf.isReadable()) {
					ctx.channel().close();
					log.info("respContent is : {}",respContent);
//					LockSupport.unpark(requestThread);
					log.info("RpcfxResponse handler unpark ......");
				}
			}
		}

		public String getRespContent() {
			return respContent;
		}
	}
}
