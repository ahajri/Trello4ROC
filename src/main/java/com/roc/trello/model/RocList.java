package com.roc.trello.model;

import java.util.ArrayList;
import java.util.List;

public class RocList {

	private String idList, listName;

	private List<RocCard> cards = new ArrayList<>();

	public RocList() {

	}

	public String getIdList() {
		return idList;
	}

	public void setIdList(String idList) {
		this.idList = idList;
	}

	public String getListName() {
		return listName;
	}
	
	public void setListName(String listName) {
		this.listName = listName;
	}

	public List<RocCard> getCards() {
		return cards;
	}

	public void setCards(List<RocCard> cards) {
		this.cards.clear();
		this.cards.addAll(cards);
	}

	public void addCard(RocCard card) {
		this.cards.add(card);
	}

}
