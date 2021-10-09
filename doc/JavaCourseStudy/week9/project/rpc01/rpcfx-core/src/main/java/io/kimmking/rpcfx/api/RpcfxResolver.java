package io.kimmking.rpcfx.api;

public interface RpcfxResolver<T> {

    T resolve(String serviceClass);

}
