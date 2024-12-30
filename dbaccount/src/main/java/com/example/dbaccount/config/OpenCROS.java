package com.example.dbaccount.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class OpenCROS implements WebMvcConfigurer{
	

	 @Override
	    public void addCorsMappings(CorsRegistry registry) {
	        // Cho phép tất cả các yêu cầu từ mọi domain
	        registry.addMapping("/**")
	                .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080") // Địa chỉ domain bạn muốn cho phép
	                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
	                .allowedHeaders("*")
	                .allowCredentials(true)
	                .maxAge(3600);
	    }

}
