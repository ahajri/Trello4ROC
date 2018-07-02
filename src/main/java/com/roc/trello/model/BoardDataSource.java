package com.roc.trello.model;

import java.util.HashMap;

public class BoardDataSource {

	private String PROJECT_TITLE, MEMBERS, DATE, WRITER_NAME;
	HashMap<String, Object> DOC_CONTENT;
	
	
	public BoardDataSource() {
		super();
	}


	public String getPROJECT_TITLE() {
		return PROJECT_TITLE;
	}


	public void setPROJECT_TITLE(String pROJECT_TITLE) {
		PROJECT_TITLE = pROJECT_TITLE;
	}


	public String getMEMBERS() {
		return MEMBERS;
	}


	public void setMEMBERS(String mEMBERS) {
		MEMBERS = mEMBERS;
	}


	public String getDATE() {
		return DATE;
	}


	public void setDATE(String dATE) {
		DATE = dATE;
	}


	public String getWRITER_NAME() {
		return WRITER_NAME;
	}


	public void setWRITER_NAME(String wRITER_NAME) {
		WRITER_NAME = wRITER_NAME;
	}


	public HashMap<String, Object> getDOC_CONTENT() {
		return DOC_CONTENT;
	}


	public void setDOC_CONTENT(HashMap<String, Object> dOC_CONTENT) {
		DOC_CONTENT = dOC_CONTENT;
	}
	
	
	
	
	
}
