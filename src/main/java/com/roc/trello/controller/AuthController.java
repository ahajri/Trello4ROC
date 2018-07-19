package com.roc.trello.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.roc.trello.exception.BusinessException;
import com.roc.trello.exception.RestException;
import com.roc.trello.service.AuthService;

@RestController
public class AuthController extends AController {

	@Autowired
	AuthService authService;

	@RequestMapping(value = "/auth/{apiKey}/{token}", method = RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> authenticate(@PathVariable(name = API_KEY) String apiKey,
			@PathVariable(name = "token") String token, HttpServletResponse response) throws RestException {

		HashMap<String, Object> resource;
		try {
			resource = authService.authenticate(apiKey, token);
			response.addHeader(API_KEY, apiKey);
			response.addHeader(ROC_TOKEN, token);

		} catch (URISyntaxException | IOException e) {
			throw new RestException("Authentification erroné", e, HttpStatus.NOT_FOUND, null);
		}
		return new ResponseEntity<>(resource, HttpStatus.OK);
	}

	@RequestMapping(value = "/auth/{username}", method = RequestMethod.GET)
	public ResponseEntity<HashMap<String, Object>> authenticateUser(@PathVariable(name = "username") String username,
			HttpServletResponse response,HttpServletRequest request) throws RestException {
		HashMap<String, Object> resource;
		try {
			Cookie cookie = new Cookie("ajs_user_id", "%225a57dedf16b8e92e146d0bf8%22");
			Arrays.asList(request.getCookies()).add(cookie);
			resource = authService.authenticateUser(username);
			
			boolean isConfirmed = (boolean) resource.get("confirmed");
			if (isConfirmed) {
				throw new RestException("User not confirmed yet", new BusinessException("User not confirmed yet"),
						HttpStatus.NOT_FOUND, null);
			}
		} catch (URISyntaxException | IOException e) {
			throw new RestException("Unknown username", e, HttpStatus.NOT_FOUND, null);
		}
		return new ResponseEntity<>(resource, HttpStatus.OK);
	}

}
