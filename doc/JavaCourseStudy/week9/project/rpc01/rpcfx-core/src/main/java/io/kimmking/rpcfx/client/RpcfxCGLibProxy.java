package io.kimmking.rpcfx.client;

import com.alibaba.fastjson.JSON;
import io.kimmking.rpcfx.api.Filter;
import io.kimmking.rpcfx.api.RpcfxRequest;
import io.kimmking.rpcfx.api.RpcfxResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Slf4j
public class RpcfxCGLibProxy implements MethodInterceptor {

	private final Class<?> serviceClass;
	private final String url;
	private final Filter[] filters;

	public <T> RpcfxCGLibProxy(Class<T> serviceClass, String url, Filter... filters) {
		this.serviceClass = serviceClass;
		this.url = url;
		this.filters = filters;
	}

	public Object createProxy(Class target, RpcfxCGLibProxy proxy) {
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

		if (null != filters) {
			for (Filter filter : filters) {
				if (!filter.filter(request)) {
					return null;
				}
			}
		}


		RequestHandler handler = new NettyHandler();
		RpcfxResponse response = handler.handler(request, url);

		// 加filter地方之三
		// Student.setTeacher("cuijing");

		// 这里判断response.status，处理异常
		// 考虑封装一个全局的RpcfxException
		return JSON.parse(response.getResult().toString());
	}

}
