package com.roc.trello.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class BoardUtils {

	/**
	 * 
	 * @param boardKey
	 * @param accessKey
	 * @param token
	 * @return ID Borad
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static String getBordId(String boardKey, String accessKey, String token)
			throws MalformedURLException, IOException {
		String sURL = "https://trello.com/b/" + boardKey + "/reports.json?key=" + accessKey + "&token=" + token; // hT5Kue8j

		URL url = new URL(sURL);
		URLConnection request = url.openConnection();
		request.connect();

		// Convert to a JSON object to print data
		JsonParser jp = new JsonParser(); // from gson
		JsonElement boardRoot = jp.parse(new InputStreamReader((InputStream) request.getContent()));
		JsonObject boardObj = boardRoot.getAsJsonObject();

		String idBoard = boardObj.get("id").getAsString();

		return idBoard;
	}

}
