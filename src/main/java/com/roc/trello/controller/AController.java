package com.roc.trello.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AController {
	
	protected final String writerName = "أنيس الحجري";

	
	@Value( "${applicationKey}" )
	protected  String applicationKey;
	
	@Value( "${accessToken}" )
	protected  String accessToken;
	
	protected  final Properties properties = new Properties();
	

	
	@PostConstruct
	public Properties fetchProperties(){
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
