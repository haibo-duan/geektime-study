package com.dhb.gts.javacourse.week3.router;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class RoundRibbonHttpEndpointRouter implements HttpEndpointRouter {

	private static volatile AtomicInteger index = new AtomicInteger(0);

	public static void main(String[] args) {
		List<String> list = new ArrayList<>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");
		RoundRibbonHttpEndpointRouter router = new RoundRibbonHttpEndpointRouter();
		IntStream.range(1, 100).forEach((i) -> {
			System.out.println(router.route(list));
		});
	}

	@Override
	public String route(List<String> endpoints) {
		String result = null;
		if (index.get() >= endpoints.size()) {
			index.set(0);
		}
		result = endpoints.get(index.getAndIncrement());
		return result;
	}
}
