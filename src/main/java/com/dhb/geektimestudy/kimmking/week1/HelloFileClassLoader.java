package com.dhb.geektimestudy.kimmking.week1;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HelloFileClassLoader extends ClassLoader {

	public static final String XLASS_NAME = "Hello.xlass";


	public static void main(String[] args) {

		try {
			Class clazz = new HelloFileClassLoader().findClass("Hello");
			Method hello = clazz.getDeclaredMethod("hello");
			hello.invoke(clazz.newInstance());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}


	}

	/**
	 * 解码 采用位运算
	 * @param source
	 * @return
	 */
	private byte[] decode(byte[] source) {
		for (int i = 0; i < source.length; i++) {
			source[i] = (byte) (0xFFFF ^ source[i]);
		}
		return source;
	}


	/**
	 * 根据传入的文件路径得到文件的二进制内容
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	private byte[] getContent(String filePath) throws IOException {
		FileInputStream fis = new FileInputStream(filePath);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i;
		while ((i = fis.read()) != -1) {
			baos.write(i);
		}
		fis.close();
		return baos.toByteArray();
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try {
			byte[] content = getContent(this.getClass().getClassLoader().getResource(XLASS_NAME).getPath());
			byte[] bytes = decode(content);
			return defineClass(name, bytes, 0, bytes.length);
		} catch (Exception e) {
			throw new ClassNotFoundException(name, e);
		}
	}
}
