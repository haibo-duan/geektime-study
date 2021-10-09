package io.kimmking.rpcfx.demo.provider;

import io.kimmking.rpcfx.api.RpcfxResolver;

import java.util.Map;

public class ReflectResolver<T>  implements RpcfxResolver{
	

	@Override
	public T resolve(String serviceClass) {
		try {
			Class<?> clazz = Class.forName(serviceClass.replaceAll(".api.",".provider.")+"Impl");
			Object obj = clazz.newInstance();
			T t = (T) obj;
			return t;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
