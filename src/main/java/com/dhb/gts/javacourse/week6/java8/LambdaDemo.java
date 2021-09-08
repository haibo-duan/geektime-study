package com.dhb.gts.javacourse.week6.java8;

import java.util.Arrays;

public class LambdaDemo {

	public static void main(String[] args) {
		LambdaDemo demo = new LambdaDemo();
		
		MathOperation op = new MathOperation<Integer>(){
			@Override
			public Integer operation(int a,int v) {
				return 1;
			}
		};
		
		MathOperation op1 = (a,b)->1;

		MathOperation op2 = new MathOperation<Integer>() {
			@Override
			public Integer operation(int a, int b) {
				return a+b;
			}
		};
		
		MathOperation addition = (int a,int b) -> a+b;
		
		MathOperation subtration = (int a,int b) -> a - b;
		
		MathOperation mutiplication = (int a,int b) -> {
			return a*b;
		};
		
		MathOperation division = (int a,int b) -> a/b;

		System.out.println("10 + 5 = "+demo.operate(10,5,addition));
		System.out.println("10 - 5 = "+demo.operate(10,5,subtration));
		System.out.println("10 * 5 = "+demo.operate(10,5,mutiplication));
		System.out.println("10 / 5 = "+demo.operate(10,5,division));

		System.out.println("10 ^ 5 = "+demo.operate(10,5,(a,b)->Math.pow(a,b)));
		
		Runnable task = () -> System.out.println("111");

		GreetingService greetingService1 = message ->
				System.out.println("Hello"+message);
		
		GreetingService greetingService2 = (message)-> {
			System.out.println(message);
		};

		GreetingService greetingService3 = System.out::println;

		Arrays.asList(1,2,3).forEach(x-> System.out.println(x+3));
		Arrays.asList(1,2,3).forEach(LambdaDemo::println);

		greetingService1.sayMessage("1111");
		greetingService1.sayMessage("1234");
		greetingService1.sayMessage("ddggg");
	}

	private static void println(int x) {
		System.out.println(x+3);
	}
	
	interface MathOperation<T> {
		T operation(int a,int b);
	}
	
	interface GreetingService{
		void sayMessage(String message);
	}
	
	private <T> T operate(int a,int b,MathOperation<T> mathOperation) {
		return mathOperation.operation(a,b);
	}
}
