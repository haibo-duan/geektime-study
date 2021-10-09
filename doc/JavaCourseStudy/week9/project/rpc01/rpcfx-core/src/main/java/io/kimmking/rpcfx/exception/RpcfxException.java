package io.kimmking.rpcfx.exception;

public class RpcfxException extends Exception{

	public RpcfxException() {
	}

	public RpcfxException(String message) {
		super(message);
	}

	public RpcfxException(String message, Throwable cause) {
		super(message, cause);
	}

	public RpcfxException(Throwable cause) {
		super(cause);
	}

	public RpcfxException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
