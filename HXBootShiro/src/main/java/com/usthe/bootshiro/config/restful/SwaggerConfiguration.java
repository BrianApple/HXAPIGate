package com.usthe.bootshiro.config.restful;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  springdoc-openapi配置 默认地址http://localhost:18080/swagger-ui/index.html
 * @author tomsun28
 * @date 21:05 2018/3/17
 */
@Configuration
public class SwaggerConfiguration {

	@Bean
	public OpenAPI createRestApi() {
		return new OpenAPI().info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
				.title("bootshiroPro:" + " Restful APIs")
				.description("restful apis docs")
				.termsOfService("uiotcp.com").version("1.0");
	}


}
