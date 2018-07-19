package com.roc.trello.exception.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.roc.trello.exception.BusinessException;
import com.roc.trello.exception.RestError;
import com.roc.trello.exception.RestException;

@EnableWebMvc
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { DuplicateKeyException.class, org.springframework.dao.DuplicateKeyException.class })
	protected ResponseEntity<Object> handleMongoUnicityConstraintException(RuntimeException ex, WebRequest request) {
		String bodyOfResponse = "some unique value already tocken";
		HttpHeaders headers = new HttpHeaders();
		request.getHeaderNames().forEachRemaining(s -> headers.add(s, request.getHeader(s)));
		return handleExceptionInternal(ex, bodyOfResponse, headers, HttpStatus.CONFLICT, request);
	}

	@ExceptionHandler(RestException.class)
	@ResponseBody
	public ResponseEntity<RestError> handleControllerException(HttpServletRequest req, RestException ex,
			HttpServletResponse resp) {
		Optional<Throwable> optionalCause = Optional.of(ex.getCause());
		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		String technicalMessage = StringUtils.EMPTY;
		String functionalMessage = StringUtils.EMPTY;
		if (optionalCause.isPresent()) {
			Throwable cause = optionalCause.get();
			if (cause instanceof BusinessException) {
				Throwable rootCause = ((BusinessException) cause).getCause();
				technicalMessage = rootCause.getMessage();
				functionalMessage = ((BusinessException) cause).getFunctionalMessage();
				if (rootCause instanceof EmptyResultDataAccessException) {
					httpStatus = HttpStatus.NOT_FOUND;
				}
			} else if (cause instanceof IllegalArgumentException) {
				IllegalArgumentException argException = (IllegalArgumentException) cause;
				functionalMessage = argException.getMessage();
				httpStatus = HttpStatus.BAD_REQUEST;
			}
		}
		return response(httpStatus, technicalMessage, functionalMessage);
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		Map<String, String> responseBody = new HashMap<>();
		responseBody.put("path", request.getContextPath());
		responseBody.put("message", "Service non disponible momentanément (404).");
		return new ResponseEntity<Object>(responseBody, HttpStatus.NOT_FOUND);
	}

	private ResponseEntity<RestError> response(HttpStatus status, String technicalMessage, String functionalMessage) {
		return new ResponseEntity<RestError>(new RestError(status.value(), technicalMessage, functionalMessage),
				status);
	}
}
