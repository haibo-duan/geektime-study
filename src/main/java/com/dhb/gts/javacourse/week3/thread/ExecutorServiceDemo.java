package com.dhb.gts.javacourse.week3.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ExecutorServiceDemo {

	
	public static void main(String[] args) throws Exception{
		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(16);

		
		String str = executorService.submit(new Callable<String>(){

			@Override
			public String call() throws Exception {
				return "I am a task, which submited by the so called laoda, and run by those anonymous workers";
			}
		}).get();
		System.out.println("str="+str);
	}
	
	
	
	
}
