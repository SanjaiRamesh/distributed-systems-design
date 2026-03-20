package com.design.circutebreaker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.ServerRequest;
import java.util.function.Function;
@SpringBootApplication
public class CircutebreakerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CircutebreakerApplication.class, args);
	}

	@Bean
	public Function<ServerRequest, String> userKeyResolver() {
		// Limits based on the "user" header; defaults to "anonymous"
		return request -> request.headers().firstHeader("user") != null
				? request.headers().firstHeader("user")
				: "anonymous";
	}
}
