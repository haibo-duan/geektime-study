package com.dhb.hazelcast.demo;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HazelcastDemoApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;
	
	@Autowired
	private HazelcastInstance hazelcastInstance;

	@Test
	public void useCachedValue() {
		// given
		String isbn = "12345";
		String cachedValue = "cached-value";
		hazelcastInstance.getMap("books").put(isbn, cachedValue);

		// when
		String response = restTemplate.getForObject(String.format("http://localhost:%s/books/%s", port, isbn), String.class);

		// then
		assertThat(response).isEqualTo(cachedValue);
	}
}
