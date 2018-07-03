package com.roc.trello.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Text;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roc.trello.enums.BoardKeyEnum;
import com.roc.trello.model.BoardDataSource;
import com.roc.trello.model.RocList;
import com.roc.trello.utils.JSONUtils;

@Service("dmsService")
public class DmsService {

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
	private void replacePlaceholder(WordprocessingMLPackage template, String name, String placeholder)
			throws UnsupportedEncodingException {
		List<Object> texts = getAllElementFromObject(template.getMainDocumentPart(), Text.class);

		for (Object text : texts) {
			Text textElement = (Text) text;

			if (textElement.getValue().trim().equals(placeholder.trim())) {
				System.out.println(placeholder + " , Value ===> " + textElement.getValue());
				textElement.setValue(name);
			}
		}
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
			replacePlaceholder(template, String.valueOf(dataSource.getPROJECT_TITLE()), BoardKeyEnum.PROJECT_TITLE.name());
			replacePlaceholder(template, String.valueOf(dataSource.getWRITER_NAME()), BoardKeyEnum.WRITER_NAME.name());
			
			HashMap<String, LinkedHashMap<String, Object>> docContent = new ObjectMapper()
					.readValue(details.get(BoardKeyEnum.DOC_CONTENT.name()), HashMap.class);

			System.out.println("########DOC_CONTENT#####" + docContent.toString());

			docContent.entrySet().forEach(entry -> {

				String key = entry.getKey();
				LinkedHashMap<String, Object> value = entry.getValue();
				value.entrySet().forEach(cardEntry -> {
					try {
						replacePlaceholder(template, String.valueOf(cardEntry.getValue()), cardEntry.getKey());
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				});

			});
			;

			writeDocxToStream(template,
					System.getProperty("user.dir") + "\\generated\\" + details.get("PROJECT_TITLE") + ".docx");

		} catch (Docx4JException | IOException e) {
			e.printStackTrace();
		}

	}

}
