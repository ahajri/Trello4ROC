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

import com.roc.trello.exception.BusinessException;
import com.roc.trello.utils.HttpUtils;

@Service
public class AuthService {

	/**
	 * Authenticate using apiKey and token
	 * 
	 * @param apiKey
	 * @param token
	 * @return Map of JSON data
	 * @throws BusinessException
	 */
	public HashMap<String, Object> authenticate(String apiKey, String token) throws BusinessException {
		String authUrl = "https://api.trello.com/1/members/me/?key=" + apiKey + "&token=" + token;
		try {
			return doGetWithParams(authUrl, new HashMap<>());
		} catch (URISyntaxException | IOException e) {
			throw new BusinessException(e, "Authentication failed ");
		}

	}

	/**
	 * Authentication by Trello Username
	 * 
	 * @param username:
	 *            Trello username
	 * @return Map of JSON data
	 * @throws BusinessException
	 */
	public HashMap<String, Object> authenticateUser(String username) throws BusinessException {
		String authUrl = "https://api.trello.com/1/members/" + username;
		try {
			return doGetWithParams(authUrl, new HashMap<>());
		} catch (URISyntaxException | IOException e) {
			throw new BusinessException(e, "Authentication failed for user: " + username);
		}
	}

	@SuppressWarnings({ "unchecked" })
	private HashMap<String, Object> doGetWithParams(String authUrl, HashMap<String, Object> params)
			throws URISyntaxException, MalformedURLException, IOException, ClientProtocolException {
		HttpUriRequest request = new HttpGet(HttpUtils.buildParamUrl(authUrl, params));
		HttpResponse response = HttpClientBuilder.create().build().execute(request);
		return HttpUtils.retrieveResourceFromResponse(response, HashMap.class);
	}
}
