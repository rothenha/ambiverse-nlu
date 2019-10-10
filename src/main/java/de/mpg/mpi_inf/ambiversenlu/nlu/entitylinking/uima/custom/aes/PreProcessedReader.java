package de.mpg.mpi_inf.ambiversenlu.nlu.entitylinking.uima.custom.aes;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.TokenForm;

public class PreProcessedReader extends JCasAnnotator_ImplBase {

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        String strText = aJCas.getDocumentText();

        String[] vTokenLines = strText.split("\n");

        int nOffsetLine = 0;
        int nOffsetSentenceStart = 0;
        for (String strToken: vTokenLines) {          
            if ( strToken.equals("") ) { // empty line: end of sentence
              Sentence sentence = new Sentence(aJCas, nOffsetSentenceStart, nOffsetLine);
              sentence.addToIndexes();

              nOffsetLine++;
              nOffsetSentenceStart = nOffsetLine;
            }
            else { 
              int nTokenStart = nOffsetLine;
              int nTokenEnd = nOffsetLine + strToken.length();

              Token token = new Token(aJCas, nTokenStart, nTokenEnd);
              TokenForm tokenForm = new TokenForm(aJCas, nTokenStart, nTokenEnd);
              tokenForm.setValue(strToken);
              token.setForm(tokenForm);

              token.addToIndexes();

              nOffsetLine += strToken.length() + 1;
          }
        }

        if ( nOffsetSentenceStart < nOffsetLine ) {
              Sentence sentence = new Sentence(aJCas, nOffsetSentenceStart, nOffsetLine);
              sentence.addToIndexes();
        }
    }

}
