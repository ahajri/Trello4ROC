package com.roc.trello.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;

import com.roc.trello.utils.HttpUtils;

@Service
public class AuthService {

	
	public HashMap<String, Object> authenticate(String apiKey, String token) throws URISyntaxException, IOException {
		String authUrl = "https://api.trello.com/1/members/me/?key=" + apiKey + "&token=" + token;
		return doGet(authUrl);
		
	}
	

	public HashMap<String, Object> authenticateUser(String username) throws MalformedURLException, ClientProtocolException, URISyntaxException, IOException {
		String authUrl = "https://api.trello.com/1/members/"+username;
		//"https://api.trello.com/1/members/anishajri1"
		return doGet(authUrl);
	}
	
	@SuppressWarnings("unchecked")
	private HashMap<String, Object> doGet(String authUrl)
			throws URISyntaxException, MalformedURLException, IOException, ClientProtocolException {
		HttpUriRequest request;
		request = new HttpGet(HttpUtils.buildParamUrl(authUrl, new HashMap<>()));
		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		return HttpUtils.retrieveResourceFromResponse(response, HashMap.class);
	}
}
