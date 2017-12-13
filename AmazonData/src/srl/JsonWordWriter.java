package srl;

import static org.apache.uima.fit.util.JCasUtil.indexCovered;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import data.ReviewSentence;
import data.SRLReview;
import data.SRLSemanticArgument;
import data.SRLSemanticPredicate;
import data.WordNode;
import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemanticArgument;
import de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemanticPredicate;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

@TypeCapability(
		inputs = { "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
				"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures",
				"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma",
				"de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency",
				"de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemanticPredicate",
				"de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemanticArgument" })

@SuppressWarnings("unused")
public class JsonWordWriter extends JCasFileWriter_ImplBase {
	private static final String	UNUSED					= "_";
	public static final String	PARAM_ENCODING			= ComponentParameters.PARAM_SOURCE_ENCODING;
	@ConfigurationParameter(
			name = PARAM_ENCODING, mandatory = true, defaultValue = "UTF-8")
	private String				encoding;
	private String				docID;

	public static final String	PARAM_FILENAME_SUFFIX	= "filenameSuffix";
	@ConfigurationParameter(
			name = PARAM_FILENAME_SUFFIX, mandatory = true, defaultValue = ".json")
	private String				filenameSuffix;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		GsonBuilder gBuilder = new GsonBuilder();
		gBuilder.excludeFieldsWithoutExposeAnnotation();
		gBuilder.setPrettyPrinting();
		Gson gson = gBuilder.create();
		try {
			write(aJCas, gson);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
		}
	}

	private void write(JCas aJCas, Gson gson) throws IOException {
		String fileName = getRelativePath(aJCas);
		int counter = 0;

		Map<Token, Collection<SemanticPredicate>> predIdx = indexCovered(aJCas, Token.class, SemanticPredicate.class);

		Map<SemanticArgument, Collection<Token>> argIdx = indexCovered(aJCas, SemanticArgument.class, Token.class);

		JsonWriter writer = new JsonWriter(new FileWriter(new File(fileName + filenameSuffix)));
		writer.setIndent("  ");
		SRLReview review = new SRLReview(fileName);
		for (Sentence sentence : select(aJCas, Sentence.class)) {
			ReviewSentence outSentence = new ReviewSentence(counter);
			counter++;

			HashMap<Token, Row> ctokens = new LinkedHashMap<Token, Row>();
			List<Token> tokens = selectCovered(Token.class, sentence);

			List<MorphologicalFeatures> morphology = selectCovered(MorphologicalFeatures.class, sentence);
			boolean useFeats = tokens.size() == morphology.size();
			List<SemanticPredicate> preds = selectCovered(SemanticPredicate.class, sentence);

			for (int i = 0; i < tokens.size(); i++) {
				Row row = new Row();
				row.id = i + 1;
				row.token = tokens.get(i);
				row.args = new SemanticArgument[preds.size()];
				if (useFeats) {
					row.feats = morphology.get(i);
				}

				// If there are multiple semantic predicates for the current
				// token, then
				// we keep only the first
				Collection<SemanticPredicate> predsForToken = predIdx.get(row.token);
				if (predsForToken != null && !predsForToken.isEmpty()) {
					row.pred = predsForToken.iterator().next();
				}
				ctokens.put(row.token, row);
			}

			// Dependencies
			for (Dependency rel : selectCovered(Dependency.class, sentence)) {
				ctokens.get(rel.getDependent()).deprel = rel;
			}
			// Semantic arguments
			for (int p = 0; p < preds.size(); p++) {
				FSArray args = preds.get(p).getArguments();
				for (SemanticArgument arg : select(args, SemanticArgument.class)) {
					for (Token t : argIdx.get(arg)) {
						Row row = ctokens.get(t);
						row.args[p] = arg;
					}
				}
			}

			for (Row row : ctokens.values()) {
				String lemma = (row.token.getLemma() != null) ? row.token.getLemma().getValue() : UNUSED;
				String pos = UNUSED;
				String cpos = UNUSED;
				if (row.token.getPos() != null) {
					POS posAnno = row.token.getPos();
					pos = posAnno.getPosValue();
					if (!(posAnno instanceof POS)) {
						cpos = posAnno.getClass().getSimpleName();
					}
					else {
						cpos = pos;
					}
				}

				int head = 0;
				String deprel = UNUSED;
				if (row.deprel != null) {
					deprel = row.deprel.getDependencyType();
					head = ctokens.get(row.deprel.getGovernor()).id;
					if (head == row.id) {
						// ROOT dependencies may be modeled as a loop, ignore
						// these.
						head = 0;
					}
				}
				SRLSemanticPredicate outPred = null;
				String fillpred = UNUSED;
				String pred = UNUSED;
				StringBuilder apreds = new StringBuilder();
				Boolean isPredicate = row.pred != null;
				FSArray test;
				if (isPredicate) {
					fillpred = "Y";
					pred = row.pred.getCategory();
					outPred = new SRLSemanticPredicate(pred);
					for (SemanticArgument arg : select(row.pred.getArguments(), SemanticArgument.class)) {
						for (Token t : argIdx.get(arg)) {
							outPred.addArgument(new SRLSemanticArgument(ctokens.get(t).id, arg.getRole()));
						}
					}
				}

				for (SemanticArgument arg : row.args) {
					if (apreds.length() > 0) {
						apreds.append('\t');
					}
					apreds.append(arg != null ? arg.getRole() : UNUSED);
				}
				/*
				aOut.printf("%d\t%s\t%s\t%s\t%s\t_\t%d\t%s\t%d\t%d\t%s\n", row.id, row.token.getCoveredText(), lemma,
						cpos, pos, head, deprel, row.token.getBegin(), row.token.getEnd(), docID, row.token.getType());
				*/
				WordNode word = new WordNode(row.token.getBegin(), row.token.getEnd(), sentence.getId(), row.id,
						row.token.getCoveredText(), pos, head, deprel, isPredicate);
				if (outPred != null) {
					word.setPredicate(outPred);
				}
				outSentence.addWord(word);
			}
			outSentence.setStart(sentence.getBegin());
			outSentence.setEnd(sentence.getEnd());
			review.addSentence(outSentence);
		}
		gson.toJson(review, SRLReview.class, writer);
		writer.close();
	}

	private static final class Row {
		int						id;
		Token					token;
		MorphologicalFeatures	feats;
		Dependency				deprel;
		SemanticPredicate		pred;
		SemanticArgument[]		args;	// These are the arguments roles for the
															// current token!
	}
}
