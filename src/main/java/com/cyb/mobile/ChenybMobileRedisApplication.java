package com.cyb.mobile;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cyb.mobile.mapper")
public class ChenybMobileRedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChenybMobileRedisApplication.class, args);
	}

}
