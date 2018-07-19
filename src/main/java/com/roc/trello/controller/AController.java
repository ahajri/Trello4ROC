package com.roc.trello.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;

public abstract class AController {

	protected final String writerName = "أنيس الحجري";

	protected static final String ROC_TOKEN = "roc-token";
	protected static final String API_KEY = "apiKey";

	@Value("${applicationKey}")
	protected String applicationKey;

	@Value("${accessToken}")
	protected String accessToken;

	protected final Properties properties = new Properties();

	@PostConstruct
	public Properties fetchProperties() {
		try {
			File file = ResourceUtils.getFile("classpath:application.properties");
			InputStream in = new FileInputStream(file);
			properties.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

}
