package com.roc.trello.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.roc.trello.exception.RestException;
import com.roc.trello.service.DmsService;
import com.roc.trello.service.TrelloService;

@RestController
@RequestMapping("/api/v1/trello4carthage")
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
	@RequestMapping(value = "/board/details/async/{boardId}", method = RequestMethod.GET)
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

	@RequestMapping(value = "/board/details/{boardId}", method = RequestMethod.GET)
	public ResponseEntity<HashMap<String, String>> getBoardDetails(@PathVariable(name = "boardId") String boardId)
			throws RestException {
		try {

			HashMap<String, String> details = trelloService.getBoardDetail(boardId, writerName, applicationKey,
					accessToken);

			return new ResponseEntity<HashMap<String, String>>(details, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RestException(e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR, StringUtils.quote(""));
		}
	}

	@RequestMapping(value = "/board/details/{boardId}/doc", method = RequestMethod.GET)
	public ResponseEntity<Void> getBoardDetailsDoc(@PathVariable(name = "boardId") String boardId)
			throws RestException {
		try {

			HashMap<String, String> details = trelloService.getBoardDetail(boardId, writerName, applicationKey,
					accessToken);

			dmsService.generateDoc(details);
			return new ResponseEntity<Void>(HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RestException(e.getMessage(), e, HttpStatus.INTERNAL_SERVER_ERROR, StringUtils.quote(""));
		}
	}

}
