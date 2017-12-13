import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.maltparser.core.helper.HashMap;

import com.google.gson.Gson;

import data.Phrase;
import data.Relation;
import data.Relations;
import data.Review;
import data.SRLReview;
import data.WordNode;
import enums.CompType;
import io.JsonReviewReader;
import utils.Comperers;
import utils.Evaluation;
import utils.ResultStats;

public class compera {

	public static void main(String[] args) throws FileNotFoundException {
		System.setOut(new PrintStream(new File("output-file.txt")));
		Gson gson = new Gson();
		JsonReviewReader reader = new JsonReviewReader(gson);
		HashMap<String, SRLReview> srlRevs = reader.getLabelledReviews();
		HashMap<String, Review> reviews = reader.getDependencyReviews();
		ArrayList<Review> reviewList = new ArrayList<Review>();
		ArrayList<Phrase> claims = new ArrayList<Phrase>();
		ArrayList<Phrase> premises = new ArrayList<Phrase>();
		Relations relations = new Relations();
		ArrayList<Relations> splitBy = new ArrayList<Relations>();

		for (String key : reviews.keySet()) {

			reviewList.add(reviews.get(key));
			Review review = reviews.get(key);
			review.setSentences(srlRevs.get(key).getSentences());
			review.initialize();
			if (review.getAllPhrases().getClaims().size() > 0) {
				claims.addAll(review.getAllPhrases().getClaims());
			}
			if (review.getAllPhrases().getPremises().size() > 0) {
				premises.addAll(review.getAllPhrases().getPremises());
			}

			for (Relation rel : review.getRelations()) {
				relations.add(rel);
				WordNode node = rel.getAspect().getExitNodes().get(0);
				// System.out.println(node.getSentence());
				// System.out.println(rel);

				WordNode result = node.getSentence().getWordBySRL(node);
				if (result == null) {
					if (rel.getAspect().getExitNodes().get(0).getPreviousID() == 0) {

					}
				}
				// System.out.println(result);//rel.getPatternRoot().print();
				// System.out.println(rel.getAspect().getSentence().getWordByAdjective(rel.getAspect()));
			}
		}

		Evaluation eval = new Evaluation();
		ResultStats stats;
		eval.printRelations(relations);
		/*
		stats = eval.getPhraseResultsWithSRL(relations, 1, 50000, 1);
		System.out.println("Recall:    " + stats.getRecall());
		System.out.println("Precision: " + stats.getPrecision());
		System.out.println("F1:        " + stats.getF1Score());
		System.out.println(stats.getResult());
		System.out.println("");
		int limit = 1;
		int topX = 50000;
		int noise = 1;
		double threshold = .1;
		
		//eval.getResultsList(relations, limit, topX, noise, threshold, CompType.ACCURACY, CompType.ACCURACY, "battery");
		// eval.getStats(reviewList);
		// System.out.println((relations.getClearedRelations()).all().size());
		// eval.latexTablePrint(relations, claims);
		relations = relations.getRelationsToAdjectives();
		ArrayList<Relations> clearedSplits = relations.getCleared10Split();
		ArrayList<Relations> relSplits = eval.splitByProduct(reviewList);
		
		ArrayList<Result> res1 = eval.get10FoldResultListFinal(relSplits, limit, topX, noise, threshold, CompType.BOTH,
				CompType.BOTH);
		ArrayList<Result> res2 = eval.get10FoldResultListFinal(relSplits, limit, topX, noise + 1, threshold,
				CompType.ACCURACY, CompType.ACCURACY);
		ArrayList<Result> resRel = eval.get10FoldRelationResultListFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY);
		ArrayList<Result> resAdj = eval.getResultListFromAdjective(claims);
		//System.out.println(relations.all().size());
		eval.printMcNemar(res1, res2);
		for (ArrayList<Review> rev : eval.splitReviewByProduct(reviewList).values()) {
			System.out.println(rev.get(0).getAsin() + ":");
			eval.getStats(rev);
		}
		
		//test(relSplits, claims);
		eval.latexTablePrint(clearedSplits, claims);
		/**/
	}

	public static void test(ArrayList<Relations> relations, ArrayList<Phrase> claims) {
		Comperers combs = new Comperers();

		Evaluation eval = new Evaluation();
		ResultStats stats;
		// eval.get10FoldResults(relations, 1, 1000);

		int limit = 1;
		int topX = 50000;
		int noise = 0;
		double threshold = .1;

		System.out.println("Config: ");
		System.out.println("Pattern min support: " + limit);
		System.out.println("Number of results: " + topX);
		System.out.println("Allowed noise: " + noise);
		System.out.println("Threshhold: " + threshold);
		System.out.println("");

		System.out.println("10 all:");
		stats = eval.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.ACCURACY,
				CompType.ACCURACY);
		System.out.println("Recall:    " + stats.getRecall());
		System.out.println("Precision: " + stats.getPrecision());
		System.out.println("F1:        " + stats.getF1Score());
		System.out.println(stats.getResult());
		System.out.println("");

		stats = eval.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.SUPPORT,
				CompType.SUPPORT);
		System.out.println("Recall:    " + stats.getRecall());
		System.out.println("Precision: " + stats.getPrecision());
		System.out.println("F1:        " + stats.getF1Score());
		System.out.println(stats.getResult());
		System.out.println("");

		stats = eval.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH, CompType.BOTH);
		System.out.println("Recall:    " + stats.getRecall());
		System.out.println("Precision: " + stats.getPrecision());
		System.out.println("F1:        " + stats.getF1Score());
		System.out.println(stats.getResult());
		System.out.println("");

		System.out.println(stats.getResult());

		System.out.println("\n10 relations:");
		System.out.println("\nbaseline:");
		int size = 0;
		for (Relations r : relations) {
			size += r.all().size();
		}
		eval.getResultFromAdjective(claims, size);
		System.out.println("");
		stats = eval.get10FoldRelationResultsFinal(relations, limit, topX, noise, threshold, CompType.ACCURACY);
		System.out.println("Recall:    " + stats.getRecall());
		System.out.println("Precision: " + stats.getPrecision());
		System.out.println("F1:        " + stats.getF1Score());
		System.out.println(stats.getResult());
		System.out.println("");

		stats = eval.get10FoldRelationResultsFinal(relations, limit, topX, noise, threshold, CompType.SUPPORT);
		System.out.println("Recall:    " + stats.getRecall());
		System.out.println("Precision: " + stats.getPrecision());
		System.out.println("F1:        " + stats.getF1Score());
		System.out.println(stats.getResult());
		System.out.println("");

		stats = eval.get10FoldRelationResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH);
		System.out.println("Recall:    " + stats.getRecall());
		System.out.println("Precision: " + stats.getPrecision());
		System.out.println("F1:        " + stats.getF1Score());
		System.out.println(stats.getResult());
		System.out.println("");

		System.out.println("\n10 phrases:");
		stats = eval.get10FoldPhraseResultsFinal(relations, limit, topX, noise, threshold, CompType.ACCURACY);
		System.out.println("Recall:    " + stats.getRecall());
		System.out.println("Precision: " + stats.getPrecision());
		System.out.println("F1:        " + stats.getF1Score());
		System.out.println(stats.getResult());
		System.out.println("");

		stats = eval.get10FoldPhraseResultsFinal(relations, limit, topX, noise, threshold, CompType.SUPPORT);
		System.out.println("Recall:    " + stats.getRecall());
		System.out.println("Precision: " + stats.getPrecision());
		System.out.println("F1:        " + stats.getF1Score());
		System.out.println(stats.getResult());
		System.out.println("");

		stats = eval.get10FoldPhraseResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH);
		System.out.println("Recall:    " + stats.getRecall());
		System.out.println("Precision: " + stats.getPrecision());
		System.out.println("F1:        " + stats.getF1Score());
		System.out.println(stats.getResult());
		System.out.println("");

		System.out.println("\n10 best:");
		stats = eval.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH, CompType.ACCURACY);
		System.out.println("Recall:    " + stats.getRecall());
		System.out.println("Precision: " + stats.getPrecision());
		System.out.println("F1:        " + stats.getF1Score());
		System.out.println(stats.getResult());

		System.out.println("\n10 SRL:");
		stats = eval.get10FoldResultsWithParametersSRL(relations, limit, topX, noise, threshold, combs.getNVC());
		System.out.println("Recall:    " + stats.getRecall());
		System.out.println("Precision: " + stats.getPrecision());
		System.out.println("F1:        " + stats.getF1Score());
		System.out.println(stats.getResult());

	}
	/*
		public static ArrayList<ProbabilityNode> initializeProbablilityNodes(Relations rels) {
	
			ArrayList<ProbabilityNode> pNodes = new ArrayList<ProbabilityNode>();
			ProbabilityNode pn;
			for (Relation rel : rels.all()) {
				pn = new ProbabilityNode(rel);
				//System.out.println(pn.getId() + ":");
				//pn.getTree().print();
				if (pNodes.contains(pn)) {
					pNodes.get(pNodes.indexOf(pn)).inc();
				}
				else {
					pNodes.add(pn);
				}
			}
			for (ProbabilityNode probabilityNode : pNodes) {
				probabilityNode.setAccuracyforPatterns(rels);
			}
			Collections.sort(pNodes);
			return pNodes;
		}
	*/
}
