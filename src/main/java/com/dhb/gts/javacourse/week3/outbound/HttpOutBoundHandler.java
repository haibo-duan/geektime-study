package com.dhb.gts.javacourse.week3.outbound;

import com.dhb.gts.javacourse.week3.filter.HeaderHttpResponseFilter;
import com.dhb.gts.javacourse.week3.filter.HttpRequestFilter;
import com.dhb.gts.javacourse.week3.filter.HttpResponseFilter;
import com.dhb.gts.javacourse.week3.filter.RequestFilterChain;
import com.dhb.gts.javacourse.week3.filter.ResponseFilterChain;
import com.dhb.gts.javacourse.week3.router.HttpEndpointRouter;
import com.dhb.gts.javacourse.week3.router.RoundRibbonHttpEndpointRouter;
import com.dhb.gts.javacourse.week3.router.WeightRoundHttpEndpointRouter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class HttpOutBoundHandler {

	private List<String> backendUrls;
	private ExecutorService proxyService;

	ResponseFilterChain responseChain;
//	HttpEndpointRouter router = new RoundRibbonHttpEndpointRouter();
	HttpEndpointRouter router = new WeightRoundHttpEndpointRouter(Arrays.asList("- server01,40","- server02,60"));
		
	int cores = Runtime.getRuntime().availableProcessors();

	public HttpOutBoundHandler(List<String> backendUrls) {
		this.backendUrls = backendUrls.stream().map(this::formatUrl).collect(Collectors.toList());
		
		long keepAliveTime = 1000;
		int queueSize = 2048;
		RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();

		proxyService = new ThreadPoolExecutor(cores,cores,keepAliveTime, TimeUnit.MILLISECONDS,
				new ArrayBlockingQueue<>(queueSize),
				new NamedThreadFactory("proxyService"), handler);

		//采用责任链模式维护filter
		responseChain = new ResponseFilterChain();
		responseChain.add(new HeaderHttpResponseFilter());
	}

	 String formatUrl(String backend) {
		return backend.endsWith("/")?backend.substring(0,backend.length()-1):backend;
	}

	public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, RequestFilterChain chain){
		String backendUrl = router.route(this.backendUrls);
		final String url = backendUrl + fullRequest.uri();
		chain.filter(fullRequest, ctx,chain);
		proxyService.submit(()->fetchGet(fullRequest, ctx, url));
	}


	abstract void fetchGet(final FullHttpRequest inbound, final ChannelHandlerContext ctx, final String url);
	

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
