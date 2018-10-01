package com.xiaopeng.waterarmy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.xiaopeng.waterarmy.model.mapper")
public class WaterarmyApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaterarmyApplication.class, args);
	}
}
