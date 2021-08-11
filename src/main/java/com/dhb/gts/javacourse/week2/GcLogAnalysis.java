package com.dhb.gts.javacourse.week2;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class GcLogAnalysis {
	
	private static Random random = new Random();

	public static void main(String[] args) {
		//开始时间戳
		long startMillis = System.currentTimeMillis();
		//持续运行毫秒数
		long timeoutMillis = TimeUnit.SECONDS.toMillis(1);
		//结束时间戳
		long endMillis = startMillis + timeoutMillis;
		LongAdder counter = new LongAdder();
		System.out.println("正在执行... ...");
		//缓存一部分对象进入old区
		int cacheSize = 2000;
		Object[] cachedGarbags = new Object[cacheSize];
		//再指定时间范围内，持续循环
		while (System.currentTimeMillis() < endMillis) {
			//生成垃圾对象
			Object garbage = generateGarbage(100*1024);
			counter.increment();
			int randomIndex = random.nextInt(2*cacheSize);
			if(randomIndex < cacheSize) {
				cachedGarbags[randomIndex] = garbage;
			}
			
		}
		System.out.println("执行结束，共生成对象次数："+counter.intValue());
	}
	
	//生成对象的方法
	private static Object generateGarbage(int max) {
		int randomSize = random.nextInt(max);
		int type = randomSize % 4;
		Object result = null;
		switch (type) {
			case 0: 
				result = new int[randomSize];
				break;
			case 1:
				result  = new byte[randomSize];
				break;
			case 2:
				result = new double[randomSize];
				break;
			default:
				StringBuilder builder = new StringBuilder();
				String randomString = "randomString-Anything";
				while (builder.length() < randomSize) {
					builder.append(randomString);
					builder.append(max);
					builder.append(randomSize);
				}
				result = builder.toString();
				break;
		}
		return result;
	}
}
