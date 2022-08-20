package com.usthe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.usthe.bootshiro.support.XssSqlStringJsonSerializer;
import org.mybatis.spring.annotation.MapperScan;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

/**
 * api路径：  http://localhost:8080/swagger-ui.html
 * 
 * 注意：所有的url必须在平台的API管理管理中先添加后开发
 * 目前没有正确添加到平台的api会被第三方随意访问！！！
 * 
 * @author yangcheng
 * @date
 */
@SpringBootApplication
@MapperScan("com.usthe.bootshiro.dao")
@EnableCaching
@ServletComponentScan
public class BootshiroApplication {

	public static void main(String[] args) {
		try {
			SpringApplication.run(BootshiroApplication.class, args);
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	@Bean
	@Primary
	public ObjectMapper xssObjectMapper(Jackson2ObjectMapperBuilder builder) {
		// 解析器
		ObjectMapper objectMapper = builder.createXmlMapper(false).build();
		// 注册XSS SQL 解析器
		SimpleModule xssModule = new SimpleModule("XssStringJsonSerializer");
		xssModule.addSerializer(new XssSqlStringJsonSerializer());
		objectMapper.registerModule(xssModule);
		return objectMapper;
	}

}
