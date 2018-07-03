package com.roc.trello.model;

import java.util.ArrayList;
import java.util.List;

public class RocCard {

	private String cardName, idCard, idList;

	private List<String> actions = new ArrayList<>();

	public RocCard() {

	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getIdList() {
		return idList;
	}

	public void setIdList(String idList) {
		this.idList = idList;
	}

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions.clear();
		this.actions.addAll(actions);
	}
	
	public void addAction(String action) {
		this.actions.add(action);
	}

	@Override
	public String toString() {
		return "RocCard [cardName=" + cardName + ", actions=" + actions.toString() + "]";
	}
	
	
}
