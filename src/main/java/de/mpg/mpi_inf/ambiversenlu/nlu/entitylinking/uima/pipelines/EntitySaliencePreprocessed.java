package de.mpg.mpi_inf.ambiversenlu.nlu.entitylinking.uima.pipelines;

import de.mpg.mpi_inf.ambiversenlu.nlu.entitylinking.uima.components.Component;
import de.mpg.mpi_inf.ambiversenlu.nlu.language.Language;
import de.mpg.mpi_inf.ambiversenlu.nlu.ner.util.KnowNERLanguage;

import java.util.HashSet;
import java.util.Set;

public class EntitySaliencePreprocessed extends Pipeline  {

    @Override void addSteps() {
        addstep("first", Component.LANGUAGE_IDENTIFICATION.name());
        for (Language language : Language.activeLanguages()) {
            if (language.isGerman()) {
                generateGermanSteps();
            } else if (language.isChinese()) {
                generateChineseSteps();
            } else if (KnowNERLanguage.supports(language)) {
                String upperCaseLanguage = language.name().toUpperCase();
                addstep(upperCaseLanguage, Component.PREPROCESSED_READER.name());

                String next = upperCaseLanguage + "_POS";
                addstep(Component.PREPROCESSED_READER.name(), next);

                if (KnowNERLanguage.requiresLemma(language)) {
                    addstep(next, upperCaseLanguage + "_LEMMATIZER");
                    next = upperCaseLanguage + "_LEMMATIZER";
                }

                addstep(next, Component.KNOW_NER_KB.name());
                addstep(Component.KNOW_NER_KB.name(), Component.AIDA_USE_RESULTS.name());
                addstep(Component.AIDA_USE_RESULTS.name(), Component.SALIENCE.name());
            } else {
                throw new UnsupportedOperationException("Language " + language + " is not supported!");
            }
        }
    }

    @Override public Set<Language> supportedLanguages() {
        return new HashSet<>(Language.activeLanguages());
    }

    private void generateGermanSteps() {
        addstep("DE", Component.PREPROCESSED_READER.name());
        addstep(Component.PREPROCESSED_READER.name(), Component.DE_POS.name());
        addstep(Component.DE_POS.name(), Component.DE_LEMMATIZER.name());
        // addstep(Component.DE_LEMMATIZER.name(), Component.KNOW_NER_NED.name());
        // addstep(Component.KNOW_NER_NED.name(), Component.DE_NER.name());
        addstep(Component.DE_LEMMATIZER.name(), Component.DE_KNOW_NER_KB.name());
        addstep(Component.DE_KNOW_NER_KB.name(), Component.DE_NER.name());
        addstep(Component.DE_NER.name(), Component.DE_NER2.name());
        addstep(Component.DE_NER2.name(), Component.AIDA_USE_RESULTS.name());
        addstep(Component.AIDA_USE_RESULTS.name(), Component.SALIENCE.name());
    }

    private void generateChineseSteps() {
        addstep("ZH", Component.PREPROCESSED_READER.name());
        addstep(Component.PREPROCESSED_READER.name(), Component.ZH_POS.name());
        addstep(Component.ZH_POS.name(), Component.ZH_NER.name());
        addstep(Component.ZH_NER.name(), Component.AIDA_USE_RESULTS.name());
        addstep(Component.AIDA_USE_RESULTS.name(), Component.SALIENCE.name());
    }
}
