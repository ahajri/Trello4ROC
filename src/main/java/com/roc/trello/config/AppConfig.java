package com.roc.trello.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.roc.trello.inteceptor.AuthInterceptor;
 
@Configuration
public class AppConfig extends WebMvcConfigurerAdapter {
	
	@Autowired
	AuthInterceptor authInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor);
	}
}
