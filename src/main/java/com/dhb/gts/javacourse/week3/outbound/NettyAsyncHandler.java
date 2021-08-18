package com.dhb.gts.javacourse.week3.outbound;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;

import java.net.URI;
import java.util.List;

public class NettyAsyncHandler extends HttpOutBoundHandler {

	private final EventLoopGroup workGroup;
	private final Bootstrap bootstrap;


	public NettyAsyncHandler(List<String> backendUrls) {
		super(backendUrls);
		workGroup = new NioEventLoopGroup(1);
		bootstrap = new Bootstrap();
		bootstrap.group(workGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);
//		bootstrap.option(EpollChannelOption.SO_REUSEPORT, true);
		bootstrap.option(ChannelOption.SO_RCVBUF, 1024 * 1024);

	}

	@Override
	void fetchGet(FullHttpRequest inbound, ChannelHandlerContext ctx, String url) {
		try {
			URI uri = new URI(url);
			String domain = uri.getHost();
			Integer port = uri.getPort();
			DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri.toASCIIString());
			//如果请求的header为空 则构造header 反之将inbound请求的header传递到endpoint
			if (inbound.headers().isEmpty()) {
				request.headers().add("Host", domain);
				request.headers().add("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:44.0) Gecko/20100101 Firefox/44.0");
				request.headers().add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				request.headers().add("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
				request.headers().add("Connection", "keep-alive");
				request.headers().add("Cache-Control", "max-age=0");
			} else {
				request.headers().set(inbound.headers());
			}

			ChannelInitializer<SocketChannel> initializer = new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					//收到http响应 解码
					ch.pipeline().addLast(new HttpResponseDecoder());
					//发送的时候 通过http编码
					ch.pipeline().addLast(new HttpRequestEncoder());
					ch.pipeline().addLast(new GeneralHandler(inbound, ctx));
				}
			};
			bootstrap.handler(initializer);
			ChannelFuture f = bootstrap.connect(domain, port).sync();
			f.channel().writeAndFlush(request);
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
			workGroup.shutdownGracefully();
		}
	}
}
