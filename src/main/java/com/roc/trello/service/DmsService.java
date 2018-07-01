package com.roc.trello.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Text;
import org.springframework.stereotype.Service;

@Service("dmsService")
public class DmsService {

	private WordprocessingMLPackage getTemplate(String name) throws Docx4JException, FileNotFoundException {
		WordprocessingMLPackage template = WordprocessingMLPackage.load(new FileInputStream(new File(name)));
		
		return template;
	}

	private void writeDocxToStream(WordprocessingMLPackage template, String target)
			throws IOException, Docx4JException {
		File f = new File(target);
		f.createNewFile();
		System.out.println("##############" + f.getPath());
		template.save(f);

	}

	private void replacePlaceholder(WordprocessingMLPackage template, String name, String placeholder) {
		List<Object> texts = getAllElementFromObject(template.getMainDocumentPart(), Text.class);

		for (Object text : texts) {
			Text textElement = (Text) text;
			//System.out.println(placeholder+" , Value ===> " + textElement.getValue());
			if (textElement.getValue().equals(placeholder)) {
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
			template = getTemplate("D:\\DEV\\Spring\\workspace\\Trello4ROC\\src\\main\\resources\\Template.docx");
			
			System.out.println(details);
			
			details.entrySet().forEach(entry -> {
				replacePlaceholder(template, entry.getValue(), entry.getKey());
			});;
			
			writeDocxToStream(template, "D:\\DEV\\Spring\\workspace\\Trello4ROC\\generated\\"+ details.get("PROJECT_TITLE")+".docx");
		} catch (Docx4JException |IOException e) {
			e.printStackTrace();
		}

	}

}
