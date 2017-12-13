package srl;

import static org.apache.commons.io.IOUtils.closeQuietly;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemanticArgument;
import de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemanticPredicate;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

@SuppressWarnings("unused")
public class TestWriter extends JCasFileWriter_ImplBase {
	private static final String	UNUSED					= "_";
	public static final String	PARAM_ENCODING			= ComponentParameters.PARAM_SOURCE_ENCODING;
	@ConfigurationParameter(
			name = PARAM_ENCODING, mandatory = true, defaultValue = "UTF-8")
	private String				encoding;
	private String				docID;

	public static final String	PARAM_FILENAME_SUFFIX	= "filenameSuffix";
	@ConfigurationParameter(
			name = PARAM_FILENAME_SUFFIX, mandatory = true, defaultValue = ".conll")
	private String				filenameSuffix;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		PrintWriter out = null;
		try {
			out = new PrintWriter(new OutputStreamWriter(getOutputStream(aJCas, filenameSuffix), encoding));
			convert(aJCas, out);
		} catch (Exception e) {
			throw new AnalysisEngineProcessException(e);
		} finally {
			closeQuietly(out);
		}
	}

	private void convert(JCas aJCas, PrintWriter aOut) {
		for (Sentence sentence : select(aJCas, Sentence.class)) {
			HashMap<Token, Row> ctokens = new LinkedHashMap<Token, Row>();

			// Tokens
			Iterator<Token> tokens = selectCovered(Token.class, sentence).iterator();
			for (int i = 1; tokens.hasNext(); i++) {
				Row row = new Row();
				row.id = i;
				row.token = tokens.next();
				ctokens.put(row.token, row);
				if (i == 1)
					docID = row.token.getCoveredText();
			}

			// Dependencies
			for (Dependency rel : selectCovered(Dependency.class, sentence)) {
				ctokens.get(rel.getDependent()).deprel = rel;
			}
			// Write sentence in CONLL 2006 format
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

				aOut.printf("%d\t%s\t%s\t%s\t%s\t_\t%d\t%s\t%d\t%d\t%s\n", row.id, row.token.getCoveredText(), lemma,
						cpos, pos, head, deprel, row.token.getBegin(), row.token.getEnd(), docID, row.token.getType(),
						row.token.getId());

			}

			aOut.println();
		}
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
