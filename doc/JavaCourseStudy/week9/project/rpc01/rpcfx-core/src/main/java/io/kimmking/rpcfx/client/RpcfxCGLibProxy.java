package io.kimmking.rpcfx.client;

import com.alibaba.fastjson.JSON;
import io.kimmking.rpcfx.api.Filter;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResponse;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.io.IOException;
import java.lang.reflect.Method;

@Slf4j
public class RpcfxCGLibProxy implements MethodInterceptor {

	public static final MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

	private final Class<?> serviceClass;
	private final String url;
	private final Filter[] filters;

	public <T> RpcfxCGLibProxy(Class<T> serviceClass, String url, Filter... filters) {
		this.serviceClass = serviceClass;
		this.url = url;
		this.filters = filters;
	}

	public Object createProxy(Class target,RpcfxCGLibProxy proxy) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(target);
		enhancer.setCallback(proxy);
		return enhancer.create();
	}

	@Override
	public Object intercept(Object o, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {

		// 加filter地方之二
		// mock == true, new Student("hubao");

		RpcfxRequest request = new RpcfxRequest();
		request.setServiceClass(this.serviceClass.getName());
		request.setMethod(method.getName());
		request.setParams(params);

		if (null!=filters) {
			for (Filter filter : filters) {
				if (!filter.filter(request)) {
					return null;
				}
			}
		}

		RpcfxResponse response = post(request, url);

		// 加filter地方之三
		// Student.setTeacher("cuijing");

		// 这里判断response.status，处理异常
		// 考虑封装一个全局的RpcfxException
		log.info("**************intercept");
		return JSON.parse(response.getResult().toString());
	}

	private RpcfxResponse post(RpcfxRequest req, String url) throws IOException {
		String reqJson = JSON.toJSONString(req);
		System.out.println("req json: "+reqJson);

		// 1.可以复用client
		// 2.尝试使用httpclient或者netty client
		OkHttpClient client = new OkHttpClient();
		final Request request = new Request.Builder()
				.url(url)
				.post(RequestBody.create(JSONTYPE, reqJson))
				.build();
		String respJson = client.newCall(request).execute().body().string();
		System.out.println("resp json: "+respJson);
		return JSON.parseObject(respJson, RpcfxResponse.class);
	}
	
	
}
