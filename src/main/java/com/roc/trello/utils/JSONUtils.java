package com.roc.trello.utils;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

public final class JSONUtils {

	private static final Gson gson = new Gson();


	private JSONUtils() {
	}

	public static boolean isJSONValid(String jsonInString) {
		try {
			gson.fromJson(jsonInString, Object.class);
			return true;
		} catch (com.google.gson.JsonSyntaxException ex) {
			return false;
		}
	}

	public static  <T> List<T> jsonArrayToObjectList(String json, Class<T> tClass) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		List<T> ts = mapper.readValue(json, new TypeReference<List<T>>() {
		});
		return ts;
	}
}
