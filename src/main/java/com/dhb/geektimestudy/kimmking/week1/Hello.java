package com.dhb.geektimestudy.kimmking.week1;

public class Hello {

	private static final int min = 100;
	private static final int max = 1000;
	
	public static void main(String[] args) {
		Hello.findLotus();
	}
	
	public static void findLotus() {
		
		for(int i=min;i<max;i++) {
			int first = i/100;
			int second = i/10%10;
			int third = i%10;
			if(first*first*first + second*second*second + third*third*third == i) {
				System.out.println(i);
			}
		}
	}
}
