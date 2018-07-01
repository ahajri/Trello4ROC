package com.roc.trello.service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Argument;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.roc.trello.enums.BoardKeyEnum;
import com.roc.trello.model.RocCard;
import com.roc.trello.model.RocList;

@Service
public class TrelloService {

	protected static final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-YYYY");

	protected final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * 
	 * @param boardId:
	 *            Trello board id
	 * @param writerName
	 *            document writer name
	 * @param applicationKey:
	 *            Trello application Key
	 * @param accessToken
	 *            Trello access token
	 * @return Future Map of Borad details
	 */

	@Async
	public CompletableFuture<HashMap<String, String>> getBoardDetailAsync(String boardId, final String writerName,
			final String applicationKey, final String accessToken) {

		HashMap<String, String> details = getBoardDetail(boardId, writerName, applicationKey, accessToken);
		return CompletableFuture.completedFuture(details);
	}

	/**
	 * 
	 * @param boardId
	 * @param writerName
	 * @param applicationKey
	 * @param accessToken
	 * @return map board details
	 */
	public HashMap<String, String> getBoardDetail(String boardId, final String writerName, final String applicationKey,
			final String accessToken) {
		long startTime = System.currentTimeMillis();

		Trello trelloApi = new TrelloImpl(applicationKey, accessToken);

		
		
		Board board = trelloApi.getBoard(boardId);

		List<String> members = new ArrayList<>();

		board.fetchMembers().stream().forEach(m -> {
			members.add(m.getFullName());
		});

		String projectTitle = board.getName();

		Argument problemsCardNameArg = new Argument("", "");

		List<TList> lists = board.fetchLists(problemsCardNameArg);

		Map<String, RocList> rocListMap = new HashMap<>();

		lists.stream().forEach(tl -> {
			RocList rocList = new RocList();
			rocList.setIdList(tl.getId());
			rocList.setListName(tl.getName());
			rocListMap.put(tl.getId(), rocList);

		});

		List<Card> cards = board.fetchCards(new Argument("idBoard", boardId));

		cards.stream().forEach(c -> {

			RocCard rocCard = new RocCard();

			rocCard.setIdCard(c.getId());
			rocCard.setIdList(c.getIdList());
			rocCard.setCardName(c.getName());

			c.getActions(new Argument("", "")).stream().forEach(a -> {
				String text = a.getData().getText();
				//String language = TranslationUtils.detectLanguage(text);
					rocCard.addAction(text);

			});

			rocListMap.get(c.getIdList()).addCard(rocCard);
		});

		HashMap<String, String> details = new HashMap<>();

		details.put(BoardKeyEnum.DATE.name(), sdf.format(new Date()));
		details.put(BoardKeyEnum.PROJECT_TITLE.name(), projectTitle);
		details.put(BoardKeyEnum.DESCRIPTION.name(), board.getDesc());
		details.put(BoardKeyEnum.WRITER_NAME.name(), writerName);
		details.put(BoardKeyEnum.MEMBERS.name(), StringUtils.arrayToCommaDelimitedString(members.toArray()));
		details.put(BoardKeyEnum.DOC_CONTENT.name(), gson.toJson(rocListMap));

		//System.out.println(details.toString());
		
		long millsDiff = System.currentTimeMillis() - startTime;

		Duration d = Duration.ofMillis(millsDiff);

		String durationTime = LocalTime.MIDNIGHT.plus(d).format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		System.out.println("Execution time ====>" + durationTime);
		return details;
	}

	

}
