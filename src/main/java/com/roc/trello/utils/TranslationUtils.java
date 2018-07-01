package com.roc.trello.utils;

import java.io.IOException;
import java.util.List;

import org.springframework.util.StringUtils;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.cloud.translate.Translate.TranslateOption;
//import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

public final class TranslationUtils {

	public static String translate(String text, String sourceLanguage, String targetLanguage) {

		Translate translate = TranslateOptions.getDefaultInstance().getService();
		Translation translation = translate.translate(text, TranslateOption.sourceLanguage(sourceLanguage),
				TranslateOption.targetLanguage(targetLanguage));
		return translation.getTranslatedText();

	}

	/**
	 * Example
	 * <p>
	 * text: Hi how you doing
	 * </p>
	 * <p>
	 * detected language : en
	 * </p>
	 * 
	 * @param text
	 *            Given text
	 * @return detected language
	 */
	public static String detectLanguage(String text) {
		if (StringUtils.isEmpty(text)) {
			return "Unknown";
		}
		/*try {
			List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();

			LanguageDetector languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
					.withProfiles(languageProfiles).build();

			TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();

			TextObject textObject = textObjectFactory.forText(text);

			Optional<LdLocale> lang = languageDetector.detect(textObject);

			if (lang.isPresent()) {
				return lang.get().getLanguage();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		return "Unknown";
	}

}
