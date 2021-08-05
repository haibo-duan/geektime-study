package com.dhb.geektimestudy.kimmking.week1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class HelloFileClassLoader extends ClassLoader{
	public static void main(String[] args) {

		try {
			new HelloFileClassLoader().findClass("Hello").newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}
	
	private byte[] decode(byte[] source) {
		for(int i=0;i<source.length;i++) {
			source[i] = (byte)(0xFFFF ^ source[i]);
		}
		return source;
	}
	
	
	
	private byte[] getContent(String filePath) throws IOException{
		File file = new File(filePath);
		long fileSize = file.length();
		FileInputStream fis = new FileInputStream(file);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i;
		while ((i = fis.read()) !=- 1){
			baos.write(i);
		}
		fis.close();
		return baos.toByteArray();
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {

		byte[] bytes = new byte[0];
		try {
			byte[] content = getContent(this.getClass().getClassLoader().getResource("Hello.xlass").getPath());
			bytes = decode(content);
			System.out.println(Base64.getEncoder().encodeToString(bytes));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return defineClass(name,bytes,0,bytes.length);
	}
}
