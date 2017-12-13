package srl;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;
import static org.apache.uima.fit.pipeline.SimplePipeline.runPipeline;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpParser;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpPosTagger;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpSegmenter;
import de.tudarmstadt.ukp.dkpro.core.clearnlp.ClearNlpSemanticRoleLabeler;
import de.tudarmstadt.ukp.dkpro.core.io.text.TextReader;

public class CreateSRLs {

	public static void main(String[] args) {
		try {
			runPipeline(
					createReaderDescription(TextReader.class, TextReader.PARAM_SOURCE_LOCATION, "train_small2.txt",
							TextReader.PARAM_LANGUAGE, "en"),
					createEngineDescription(ClearNlpSegmenter.class), createEngineDescription(ClearNlpPosTagger.class),
					createEngineDescription(ClearNlpLemmatizer.class), createEngineDescription(ClearNlpParser.class),
					createEngineDescription(ClearNlpSemanticRoleLabeler.class),
					createEngineDescription(JsonWordWriter.class, JsonWordWriter.PARAM_TARGET_LOCATION, "."));
		} catch (ResourceInitializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UIMAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
