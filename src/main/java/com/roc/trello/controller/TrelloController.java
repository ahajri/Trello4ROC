package com.roc.trello.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.roc.trello.exception.RestException;
import com.roc.trello.service.DmsService;
import com.roc.trello.service.TrelloService;
import com.roc.trello.utils.BoardUtils;

@RestController
@RequestMapping("/trello4roc")
public class TrelloController extends AController {

	@Autowired
	private TrelloService trelloService;

	@Autowired
	private DmsService dmsService;

	/**
	 * 
	 * 
	 * @param cardId
	 * @return list of card members
	 * @throws RestException
	 */
	@RequestMapping(value = "/b/details/async/{boardId}", method = RequestMethod.GET)
	public ResponseEntity<HashMap<String, String>> getBoardDetailsAsync(@PathVariable(name = "boardId") String boardId)
			throws RestException {
		try {

			CompletableFuture<HashMap<String, String>> futureDetails = trelloService.getBoardDetailAsync(boardId,
					writerName, applicationKey, accessToken);

			futureDetails.thenApplyAsync(d -> {

				dmsService.generateDoc(d);
				return new ResponseEntity<HashMap<String, String>>(HttpStatus.OK);

			}).exceptionally(e -> {
				final HashMap<String, String> errorMap = new HashMap<>();
				errorMap.put("errorMsg", e.getMessage());
				return new ResponseEntity<HashMap<String, String>>(errorMap, HttpStatus.INTERNAL_SERVER_ERROR);
			});

			return new ResponseEntity<HashMap<String, String>>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RestException(e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR, StringUtils.quote(""));
		}
	}

	@RequestMapping(value = "/b/details/{boardId}", method = RequestMethod.GET)
	public ResponseEntity<HashMap<String, String>> getBoardDetails(@PathVariable(name = "boardId") String boardId)
			throws RestException {
		try {

			String idBoard = BoardUtils.getBordId(boardId,applicationKey,accessToken);

			System.out.println(boardId + "####" + idBoard);

			HashMap<String, String> details = trelloService.getBoardDetail(idBoard, writerName, applicationKey,
					accessToken);

			return new ResponseEntity<HashMap<String, String>>(details, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RestException(e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR, StringUtils.quote(""));
		}
	}

	@RequestMapping(value = "/b/{boardId}/doc", method = RequestMethod.GET)
	public ResponseEntity<Void> getBoardDetailsDoc(@PathVariable(name = "boardId") String boardId)
			throws RestException {
		try {
			String idBoard = BoardUtils.getBordId(boardId,applicationKey,accessToken);


			HashMap<String, String> details = trelloService.getBoardDetail(idBoard, writerName, applicationKey,
					accessToken);

			dmsService.generateDoc(details);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RestException(e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR, StringUtils.quote(""));
		}
	}

}
