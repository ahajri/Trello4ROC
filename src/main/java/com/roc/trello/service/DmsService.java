package com.roc.trello.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBElement;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Text;
import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.roc.trello.enums.BoardKeyEnum;
import com.roc.trello.model.BoardDataSource;
import com.roc.trello.model.RocCard;
import com.roc.trello.model.RocList;
import com.roc.trello.utils.JSONUtils;

@Service("dmsService")
public class DmsService {

	private static final String DOCX_EXTENSION = ".docx";
	private static final String TEMPLATE_NAME = "Template.docx";

	private WordprocessingMLPackage getTemplate(String templatePath) throws Docx4JException, FileNotFoundException {
		WordprocessingMLPackage template = WordprocessingMLPackage.load(new FileInputStream(new File(templatePath)));

		return template;
	}

	private void writeDocxToStream(WordprocessingMLPackage template, String target)
			throws IOException, Docx4JException {
		File f = new File(target);
		f.createNewFile();
		template.save(f);

	}

	/**
	 * 
	 * @param template
	 * @param name
	 * @param placeholder
	 * @throws UnsupportedEncodingException
	 */
	private void replacePlaceholder(WordprocessingMLPackage template, String name, String placeholder) {
		List<Object> texts = getAllElementFromObject(template.getMainDocumentPart(), Text.class);

		for (Object text : texts) {
			Text textElement = (Text) text;
			// System.out.println("Text element: " + textElement.getValue());
			if (textElement.getValue().trim().equals(placeholder.trim())) {
				// System.out.println(placeholder + " , Value ===> " + textElement.getValue());
				textElement.setValue(name);
				return;
			}
		}
	}

	private void insertListParagraph(WordprocessingMLPackage template, HashMap<String, Object> tList) {
		MainDocumentPart mdp = template.getMainDocumentPart();

		String listName = (String) tList.get(BoardKeyEnum.listName.name());
		List<String> cards = (List<String>) tList.get(BoardKeyEnum.cards.name());
		
		mdp.addStyledParagraphOfText("Title-Inserted",
				"Select all, then hit F9 in Word to see your pictures, or programmatically add them first");

		mdp.createParagraphOfText("simple field:");
		
		mdp.createParagraphOfText("complex field:");
	}

	/**
	 * 
	 * @param obj
	 * @param toSearch
	 * @return
	 */
	private static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
		List<Object> result = new ArrayList<Object>();
		if (obj instanceof JAXBElement)
			obj = ((JAXBElement<?>) obj).getValue();

		if (obj.getClass().equals(toSearch))
			result.add(obj);
		else if (obj instanceof ContentAccessor) {
			List<?> children = ((ContentAccessor) obj).getContent();
			for (Object child : children) {
				result.addAll(getAllElementFromObject(child, toSearch));
			}

		}
		return result;
	}

	public void generateDoc(HashMap<String, String> details) {

		WordprocessingMLPackage template;
		try {
			String templatePath = DmsService.class.getClassLoader().getResource(TEMPLATE_NAME).getFile();
			template = getTemplate(templatePath);

			// System.out.println(details);

			BoardDataSource dataSource = new BoardDataSource();
			dataSource.setDATE(details.get(BoardKeyEnum.DATE.name()));
			dataSource.setMEMBERS(details.get(BoardKeyEnum.MEMBERS.name()));
			dataSource.setPROJECT_TITLE(details.get(BoardKeyEnum.PROJECT_TITLE.name()));
			dataSource.setWRITER_NAME(details.get(BoardKeyEnum.WRITER_NAME.name()));

			replacePlaceholder(template, String.valueOf(dataSource.getDATE()), BoardKeyEnum.DATE.name());
			replacePlaceholder(template, String.valueOf(dataSource.getMEMBERS()), BoardKeyEnum.MEMBERS.name());
			replacePlaceholder(template, String.valueOf(dataSource.getPROJECT_TITLE()),
					BoardKeyEnum.PROJECT_TITLE.name());

			replacePlaceholder(template, String.valueOf(dataSource.getWRITER_NAME()), BoardKeyEnum.WRITER_NAME.name());

			Map<String, HashMap<String, Object>> listMap = new ObjectMapper()
					.readValue(details.get(BoardKeyEnum.DOC_CONTENT.name()), Map.class);

			System.out.println("##listMap##" + listMap.values());

			listMap.values().stream().forEach(tList -> {
				insertListParagraph(template, tList);
			});
			/*listMap.values().stream().forEach(tList -> {
				System.out.println("##List =====> " + tList.get("listName"));
				replacePlaceholder(template, String.valueOf(tList.get("listName")), BoardKeyEnum.listName.name());
				((List<HashMap<String, Object>>) tList.get("cards")).stream().forEach(c -> {

					replacePlaceholder(template, String.valueOf(c.get("cardName")), "cardName");

					List<String> actions = (List<String>) c.get("actions");

					System.out.println("###actions###" + actions.toString());

					final StringBuilder sb = new StringBuilder();

					actions.stream().forEach(a -> sb.append(a).append("\n"));

					replacePlaceholder(template, sb.toString(), "actions");
				});

			});*/

			/*
			 * Iterator<Entry<String, RocList>> tListIterator =
			 * listMap.entrySet().iterator(); while (tListIterator.hasNext()) {
			 * Entry<String, RocList> entry = tListIterator.next(); RocList tList =
			 * entry.getValue(); replacePlaceholder(template, tList.getListName(),
			 * BoardKeyEnum.listName.name()); tList.getCards().stream().forEach(c -> {
			 * replacePlaceholder(template, c.getCardName(), "cardName");
			 * replacePlaceholder(template, StringUtils.join(c.getActions(), ", "),
			 * "actions"); }); }
			 */

			// System.out.println("########DOC_CONTENT#####" + docContent.toString());

			listMap.entrySet().forEach(entry -> {

				String key = entry.getKey();
				/*
				 * LinkedHashMap<String, Object> trelloList = entry.getValue();
				 * List<LinkedHashMap> cards = (List<LinkedHashMap>) trelloList.get("cards");
				 * 
				 * cards.stream().forEach(c -> { replacePlaceholder(template,
				 * c.get("cardName").toString(), "cardName");
				 * replacePlaceholder(template,StringUtils.join(c.get("actions"), ", ") ,
				 * "actions");
				 * 
				 * });
				 */

				/*
				 * value.entrySet().forEach(cardEntry -> {
				 * 
				 * value.entrySet().forEach(list -> { try { System.out.println(list.getKey() +
				 * "====>" + String.valueOf(list.getValue()));
				 * 
				 * } catch (UnsupportedEncodingException e) { e.printStackTrace(); } });
				 * 
				 * });
				 */

			});
			;

			writeDocxToStream(template, System.getProperty("user.dir") + "\\generated\\"
					+ details.get(BoardKeyEnum.PROJECT_TITLE.name()) + DOCX_EXTENSION);

		} catch (Docx4JException | IOException e) {
			e.printStackTrace();
		}

	}

}
