package com.dhb.hazelcast.demo.controller;

import com.dhb.hazelcast.demo.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
@Slf4j
public class BookController {

	private final BookService bookService;

	BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@GetMapping("/{isbn}")
	public String getBookNameByIsbn(@PathVariable("isbn") String isbn) {
		StopWatch watch = new StopWatch();
		watch.start();
		String result = bookService.getBookNameByIsbn(isbn);
		watch.stop();
		log.info("getBookNameByIsbn cost {} ",watch.prettyPrint());
		return result;
	}
}
