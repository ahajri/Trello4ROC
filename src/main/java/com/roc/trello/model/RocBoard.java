package com.roc.trello.model;

import java.util.ArrayList;
import java.util.List;

public class RocBoard {
	
	private String idBoard, boardName;
	List<String> members;

	private List<RocList> lists = new ArrayList<>();

	public String getIdBoard() {
		return idBoard;
	}

	public void setIdBoard(String idBoard) {
		this.idBoard = idBoard;
	}

	public void setBoardName(String boardName) {
		this.boardName = boardName;
	}
	
	public String getBoardName() {
		return boardName;
	}

	public List<RocList> getLists() {
		return lists;
	}

	public List<String> getMembers() {
		return members;
	}
	
	public void setMembers(List<String> members) {
		this.members = members;
	}
	public void setLists(List<RocList> lists) {
		this.lists.clear();
		this.lists.addAll(lists);
	}

	public void addList(RocList list) {
		this.lists.add(list);
	}

}
