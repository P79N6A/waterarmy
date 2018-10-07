package com.xiaopeng.waterarmy;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("com.xiaopeng.waterarmy.model.mapper")
public class WaterarmyApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaterarmyApplication.class, args);
	}
}
