package io.kimmking.rpcfx.client;

import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResponse;
import okhttp3.MediaType;

import java.io.IOException;

public abstract class RequestHandler {

	public static final int CORES = Runtime.getRuntime().availableProcessors();
	
	public static final String MEDIA_TYPE = "application/json; charset=utf-8";
	
	public abstract RpcfxResponse handler(final RpcfxRequest request,final  String url) throws Throwable;

	
}
