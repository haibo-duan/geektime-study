package com.dhb.gts.javacourse.week3.inbound;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import java.util.List;

public class HttpInboundServer {
	//server的端口
	private int port;
	//server代理的服务列表
	private List<String> proxyServers;

	public HttpInboundServer(int port, List<String> proxyServers) {
		this.port = port;
		this.proxyServers = proxyServers;
	}
	
	public void run() throws Exception{
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(16);

		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.option(ChannelOption.SO_BACKLOG,128)
					.childOption(ChannelOption.TCP_NODELAY,true)
					.childOption(ChannelOption.SO_KEEPALIVE,true)
					.childOption(ChannelOption.SO_REUSEADDR,true)
					.childOption(ChannelOption.SO_RCVBUF,32*1024)
					.childOption(ChannelOption.SO_SNDBUF,32*1024)
					.childOption(EpollChannelOption.SO_REUSEPORT,true)
					.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

			bootstrap.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler())
					.childHandler(new HttpInboundInitializer(this.proxyServers));

			Channel channel = bootstrap.bind(port).sync().channel();
			System.out.println("启动netty 服务端，监听地址和端口为 http://127.0.0.1:"+port+"/");
			channel.closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
