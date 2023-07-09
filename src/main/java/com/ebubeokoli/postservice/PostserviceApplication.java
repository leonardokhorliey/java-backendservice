package com.ebubeokoli.postservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class PostserviceApplication {

	public static void main(String[] args) {
		System.out.println("Starting");
		SpringApplication.run(PostserviceApplication.class, args);

		System.out.println("Below");
	}

}
