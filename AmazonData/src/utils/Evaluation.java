package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import data.Phrase;
import data.PhrasePatternNode;
import data.ProbabilityNode;
import data.Relation;
import data.RelationPatternNode;
import data.Relations;
import data.Result;
import data.Review;
import data.WordNode;
import enums.CompType;

public class Evaluation {
	public static ArrayList<ProbabilityNode> initializePhraseProbablilityNodes(ArrayList<Phrase> phrases) {

		ArrayList<ProbabilityNode> pNodes = new ArrayList<ProbabilityNode>();
		ProbabilityNode pn;
		int max = 0;
		for (Phrase p : phrases) {
			pn = new ProbabilityNode(p);
			// System.out.println(pn.getId() + ":");
			// pn.getTree().print();
			if (pNodes.contains(pn)) {
				pNodes.get(pNodes.indexOf(pn)).inc();
				p.setProbabilityNode(pNodes.get(pNodes.indexOf(pn)));
			}
			else {
				pNodes.add(pn);
				p.setProbabilityNode(pn);
			}
		}
		for (ProbabilityNode probabilityNode : pNodes) {
			probabilityNode.setAccuracyforPatterns(phrases);
			if (probabilityNode.getTotalSupport() > max) {
				max = probabilityNode.getTotalSupport();
			}

		}
		for (ProbabilityNode probabilityNode : pNodes) {
			probabilityNode.setMaxSupport(max);
		}

		return pNodes;
	}

	public static ArrayList<ProbabilityNode> initializeRelationProbablilityNodes(Relations rels) {

		ArrayList<ProbabilityNode> pNodes = new ArrayList<ProbabilityNode>();
		ProbabilityNode pn;
		int max = 0;
		for (Relation rel : rels.all()) {
			pn = new ProbabilityNode(rel);
			// System.out.println(pn.getId() + ":");
			// pn.getTree().print();
			if (pNodes.contains(pn)) {
				pNodes.get(pNodes.indexOf(pn)).inc();
			}
			else {
				pNodes.add(pn);
			}

		}
		for (ProbabilityNode probabilityNode : pNodes) {
			probabilityNode.setAccuracyforPatterns(rels);
			if (probabilityNode.getTotalSupport() > max) {
				max = probabilityNode.getTotalSupport();
			}

		}
		for (ProbabilityNode probabilityNode : pNodes) {
			probabilityNode.setMaxSupport(max);
		}
		return pNodes;
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public ResultStats evalutateCorrectResult(ArrayList<Result> results, int size) {
		ResultStats r = new ResultStats();
		r.setTotal(results.size());
		r.setCorrect(size);
		Collections.sort(results);
		for (Result result : results) {
			// System.out.println(result);
			if (result.getCorrect()) {
				r.addHit();
			}
			else {
				r.addMiss();
			}
		}
		System.out.println("Number of results: " + results.size() + "/" + size);
		System.out.println("Recall:    " + r.getRecall());
		System.out.println("Precision: " + r.getPrecision());
		System.out.println("F1:        " + r.getF1Score());
		System.out.println(r.getResult());
		return r;

	}

	public ResultStats evalutateResultsAt(ArrayList<Result> results, int size, int limit) {
		ResultStats r = new ResultStats();
		r.setTotal(results.size());
		r.setCorrect(size);
		int counter = 0;
		Collections.sort(results);
		for (Result result : results) {
			// System.out.println(result);
			counter++;
			if (result.getCorrect()) {
				r.addHit();
			}
			else {
				r.addMiss();
			}
			if (counter >= limit) {
				break;
			}
		} /*
			System.out.println("Number of results: " + results.size() + "/" + size);
			System.out.println("Recall:    " + r.getRecall());
			System.out.println("Precision: " + r.getPrecision());
			System.out.println("F1:        " + r.getF1Score());
			System.out.println(r.getResult());
			*/
		return r;

	}

	public ResultStats evalutateTargetExitNodesAt(ArrayList<Result> results, int size, int limit) {
		ResultStats r = new ResultStats();
		r.setTotal(results.size() > limit ? results.size() : limit);
		r.setCorrect(size);
		int counter = 0;
		Collections.sort(results);
		for (Result result : results) {
			// System.out.println(result);
			counter++;
			if (result.getCorrectTargetExitNode()) {
				r.addHit();
			}
			else {
				r.addMiss();
			}
			if (counter >= limit) {
				break;
			}
		}
		/*
			System.out.println("Number of results: " + results.size() + "/" + size);
			System.out.println("Recall:    " + r.getRecall());
			System.out.println("Precision: " + r.getPrecision());
			System.out.println("F1:        " + r.getF1Score());
			System.out.println(r.getResult());
			
			System.out.println("");
			*/
		return r;

	}

	public ArrayList<Result> get10FoldPhraseResultListFinal(Relations relations, int limit, int topX, int noise,
			double threshhold, CompType comp) {
		Relations clearedRelation = relations.getClearedRelations();
		ArrayList<Relations> relationsList = clearedRelation.split10();
		ArrayList<Result> resultList = new ArrayList<Result>();
		for (int i = 0; i < relationsList.size(); i++) {
			Relations testRelations = new Relations();
			Relations trainingRelations = new Relations();
			for (int j = 0; j < relationsList.size(); j++) {
				if (j == i) {
					testRelations = relationsList.get(j);
				}
				else {
					trainingRelations.append(relationsList.get(j));
				}
			}
			ArrayList<ProbabilityNode> phraseProbNodesInit = initializePhraseProbablilityNodes(
					trainingRelations.getStatements());
			ArrayList<ProbabilityNode> phraseProbNodes = new ArrayList<ProbabilityNode>();
			ArrayList<Result> results = new ArrayList<Result>();
			ArrayList<Result> allResults = new ArrayList<Result>();
			ArrayList<WordNode> resultWords = new ArrayList<WordNode>();

			for (ProbabilityNode probabilityNode : phraseProbNodesInit) {
				if (probabilityNode.getTotalSupport() >= limit) {
					probabilityNode.setSortVariable(comp);
					phraseProbNodes.add(probabilityNode);
				}
			}

			Collections.sort(phraseProbNodes);

			for (Phrase premisePhrase : testRelations.getStatements()) {
				resultWords = premisePhrase.getExitNodes();
				if (resultWords.size() > 1) {
					continue;
				}
				results = getFirstResultForPhrase(phraseProbNodes, resultWords);
				for (Result result : results) {

					result.setCorrectStatement(premisePhrase);
					result.setTargetExitNodes(resultWords);
					result.checkPhraseVsGold(noise);
					allResults.add(result);

				}

			}
			resultList.addAll(allResults);
		}
		return resultList;
	}

	public ResultStats get10FoldPhraseResultsFinal(ArrayList<Relations> relations, int limit, int topX, int noise,
			double threshhold, CompType comp) {
		ResultStats all = new ResultStats();

		for (int i = 0; i < relations.size(); i++) {
			Relations testRelations = new Relations();
			Relations trainingRelations = new Relations();
			for (int j = 0; j < relations.size(); j++) {
				if (j == i) {
					testRelations = relations.get(j);
				}
				else {
					trainingRelations.append(relations.get(j));
				}
			}
			ArrayList<ProbabilityNode> phraseProbNodesInit = initializePhraseProbablilityNodes(
					trainingRelations.getStatements());
			ArrayList<ProbabilityNode> phraseProbNodes = new ArrayList<ProbabilityNode>();
			ArrayList<Result> results = new ArrayList<Result>();
			ArrayList<Result> allResults = new ArrayList<Result>();
			ArrayList<WordNode> resultWords = new ArrayList<WordNode>();

			for (ProbabilityNode probabilityNode : phraseProbNodesInit) {
				if (probabilityNode.getTotalSupport() >= limit) {
					probabilityNode.setSortVariable(comp);
					phraseProbNodes.add(probabilityNode);
				}
			}

			Collections.sort(phraseProbNodes);

			for (Phrase premisePhrase : testRelations.getStatements()) {
				resultWords = premisePhrase.getExitNodes();
				if (resultWords.size() > 1) {
					continue;
				}
				results = getFirstResultForPhrase(phraseProbNodes, resultWords);
				for (Result result : results) {

					result.setCorrectStatement(premisePhrase);
					result.setTargetExitNodes(resultWords);
					result.checkPhraseVsGold(noise);
					allResults.add(result);

				}

			}
			all.add(evalutateResultsAt(allResults, testRelations.getStatements().size(), topX));

		}
		return all;
	}

	public ArrayList<Result> get10FoldRelationResultListFinal(Relations relations, int limit, int topX, int noice,
			double threshhold, CompType comp) {
		Boolean newResult;
		RelationPatternNode currentPattern;
		Result currentResult;
		Relations clearedRelation = relations.getClearedRelations();
		ArrayList<Result> resultList = new ArrayList<Result>();

		ArrayList<Relations> relationsList = clearedRelation.split10();
		for (int i = 0; i < relationsList.size(); i++) {
			Relations testRelations = new Relations();
			Relations trainingRelations = new Relations();
			for (int j = 0; j < relationsList.size(); j++) {
				if (j == i) {
					testRelations = relationsList.get(j);
				}
				else {
					trainingRelations.append(relationsList.get(j));
				}
			}

			ArrayList<ProbabilityNode> relationProbNodesInit = initializeRelationProbablilityNodes(trainingRelations);
			ArrayList<ProbabilityNode> relationProbNodes = new ArrayList<ProbabilityNode>();

			ArrayList<Result> allResults = new ArrayList<Result>();
			ArrayList<Result> currentResults = new ArrayList<Result>();

			for (ProbabilityNode probabilityNode : relationProbNodesInit) {
				if (probabilityNode.getTotalSupport() > limit) {
					probabilityNode.setSortVariable(comp);
					relationProbNodes.add(probabilityNode);
				}
			}

			Collections.sort(relationProbNodes);
			for (Phrase claim : testRelations.getAspects()) {
				double adjustedSV = 0.0;
				double currentSV = 0.0;
				int counter = -1;
				currentResults.clear();
				do {
					counter++;
					currentPattern = (RelationPatternNode) relationProbNodes.get(counter).getTree();
					currentResult = claim.getPatternTargetResult(currentPattern);
					currentResult.setRelationProbabilityNode(relationProbNodes.get(counter));
					if (currentResult.hasTargetNodes()) {
						currentSV = relationProbNodes.get(counter).getSortVariable();
						if (adjustedSV == 0.0) {
							adjustedSV = currentSV - (currentSV * threshhold);
						}
						if (currentSV >= adjustedSV) {
							currentResult.checkTargetExitNodes(0);
							newResult = true;
							for (Result res : currentResults) {
								if (res.compareExtitNodesWith(currentResult)) {
									newResult = false;
								}
							}
							if (newResult) {
								currentResults.add(currentResult);
								allResults.add(currentResult);
							}
						}
					}
					if (counter > relationProbNodes.size() - 2) {
						break;
					}
				} while (counter <= relationProbNodes.size() - 2);

			}
			resultList.addAll(allResults);

		}
		return resultList;
	}

	public ResultStats get10FoldRelationResultsFinal(ArrayList<Relations> relations, int limit, int topX, int noice,
			double threshhold, CompType comp) {
		ResultStats all = new ResultStats();
		Boolean newResult;
		RelationPatternNode currentPattern;
		Result currentResult;

		for (int i = 0; i < relations.size(); i++) {
			Relations testRelations = new Relations();
			Relations trainingRelations = new Relations();
			for (int j = 0; j < relations.size(); j++) {
				if (j == i) {
					testRelations = relations.get(j);
				}
				else {
					trainingRelations.append(relations.get(j));
				}
			}

			ArrayList<ProbabilityNode> relationProbNodesInit = initializeRelationProbablilityNodes(trainingRelations);
			ArrayList<ProbabilityNode> relationProbNodes = new ArrayList<ProbabilityNode>();

			ArrayList<Result> allResults = new ArrayList<Result>();
			ArrayList<Result> currentResults = new ArrayList<Result>();

			for (ProbabilityNode probabilityNode : relationProbNodesInit) {
				if (probabilityNode.getTotalSupport() > limit) {
					probabilityNode.setSortVariable(comp);
					relationProbNodes.add(probabilityNode);
				}
			}

			Collections.sort(relationProbNodes);
			for (Phrase claim : testRelations.getAspects()) {
				double adjustedSV = 0.0;
				double currentSV = 0.0;
				int counter = -1;
				currentResults.clear();
				do {
					counter++;
					currentPattern = (RelationPatternNode) relationProbNodes.get(counter).getTree();
					currentResult = claim.getPatternTargetResult(currentPattern);
					currentResult.setRelationProbabilityNode(relationProbNodes.get(counter));
					if (currentResult.hasTargetNodes()) {
						currentSV = relationProbNodes.get(counter).getSortVariable();
						if (adjustedSV == 0.0) {
							adjustedSV = currentSV - (currentSV * threshhold);
						}
						if (currentSV >= adjustedSV) {
							currentResult.checkTargetExitNodes(0);
							newResult = true;
							for (Result res : currentResults) {
								if (res.compareExtitNodesWith(currentResult)) {
									newResult = false;
								}
							}
							if (newResult) {
								currentResults.add(currentResult);
								allResults.add(currentResult);
							}
						}
					}
					if (counter > relationProbNodes.size() - 2) {
						break;
					}
				} while (counter <= relationProbNodes.size() - 2);

			}
			all.add(evalutateTargetExitNodesAt(allResults, testRelations.all().size(), topX));

		}
		return all;
	}

	public ArrayList<Result> get10FoldResultListFinal(ArrayList<Relations> relations, int limit, int topX, int noise,
			double threshhold, CompType compRelation, CompType compPhrase) {
		RelationPatternNode currentPattern;
		Result currentResult;
		ArrayList<Result> resultList = new ArrayList<Result>();

		for (int i = 0; i < relations.size(); i++) {
			Relations testRelations = new Relations();
			Relations trainingRelations = new Relations();
			for (int j = 0; j < relations.size(); j++) {
				if (j == i) {
					testRelations = relations.get(j);
				}
				else {
					trainingRelations.append(relations.get(j));
				}
			}

			ArrayList<ProbabilityNode> relationProbNodesInit = initializeRelationProbablilityNodes(trainingRelations);
			ArrayList<ProbabilityNode> relationProbNodes = new ArrayList<ProbabilityNode>();
			for (ProbabilityNode probabilityNode : relationProbNodesInit) {
				if (probabilityNode.getTotalSupport() > limit) {
					probabilityNode.setSortVariable(compRelation);
					relationProbNodes.add(probabilityNode);
				}
			}

			ArrayList<ProbabilityNode> phraseProbNodesInit = initializePhraseProbablilityNodes(
					trainingRelations.getStatements());
			ArrayList<ProbabilityNode> phraseProbNodes = new ArrayList<ProbabilityNode>();
			for (ProbabilityNode probabilityNode : phraseProbNodesInit) {
				if (probabilityNode.getTotalSupport() >= limit) {
					probabilityNode.setSortVariable(compPhrase);
					phraseProbNodes.add(probabilityNode);
				}
			}

			ArrayList<Result> allResults = new ArrayList<Result>();
			ArrayList<Result> currentResults = new ArrayList<Result>();

			Collections.sort(relationProbNodes);
			Collections.sort(phraseProbNodes);
			for (Phrase claim : testRelations.getAspects()) {
				double adjustedSV = 0.0;
				double currentSV = 0.0;
				int counter = -1;
				currentResults.clear();
				do {
					counter++;
					currentPattern = (RelationPatternNode) relationProbNodes.get(counter).getTree();
					currentResult = claim.getPatternTargetResult(currentPattern);
					currentResult.setRelationProbabilityNode(relationProbNodes.get(counter));
					if (currentResult.hasTargetNodes()) {
						currentSV = relationProbNodes.get(counter).getSortVariable();
						if (adjustedSV == 0.0) {
							adjustedSV = currentSV - (currentSV * threshhold);
						}
						if (currentSV >= adjustedSV) {

							currentResult = getPhraseProbResult(phraseProbNodes, currentResult);// getResultForPhraseWithThreshhold(phraseProbNodes,
																								// resultWords,
																								// threshhold);
							currentResult.checkResult(noise);
							allResults.add(currentResult);

						}
					}
					if (counter > relationProbNodes.size() - 2) {
						break;
					}
				} while (counter <= relationProbNodes.size() - 2);

			}
			resultList.addAll(allResults);
		}
		return resultList;
	}

	public ResultStats get10FoldResults(Relations relations, int limit, int topX) {
		ArrayList<Relations> relationsList = relations.split10();
		for (int i = 0; i < relationsList.size(); i++) {
			Relations testRelations = new Relations();
			Relations trainingRelations = new Relations();
			for (int j = 0; j < relationsList.size(); j++) {
				if (j == i) {
					testRelations = relationsList.get(j);
				}
				else {
					trainingRelations.append(relationsList.get(j));
				}
			}

			ArrayList<ProbabilityNode> relationProbNodesInit = initializeRelationProbablilityNodes(trainingRelations);
			ArrayList<ProbabilityNode> relationProbNodes = new ArrayList<ProbabilityNode>();
			ArrayList<ProbabilityNode> phraseProbNodesInit = initializePhraseProbablilityNodes(
					trainingRelations.getStatements());
			ArrayList<ProbabilityNode> phraseProbNodes = new ArrayList<ProbabilityNode>();
			ArrayList<Result> results = new ArrayList<Result>();
			ArrayList<Result> allResults = new ArrayList<Result>();
			ArrayList<WordNode> resultWords = new ArrayList<WordNode>();

			for (ProbabilityNode probabilityNode : relationProbNodesInit) {
				if (probabilityNode.getTotalSupport() > limit) {
					relationProbNodes.add(probabilityNode);
				}
			}
			for (ProbabilityNode probabilityNode : phraseProbNodesInit) {
				if (probabilityNode.getTotalSupport() >= limit) {
					phraseProbNodes.add(probabilityNode);
				}
			}
			Collections.sort(relationProbNodes);
			Collections.sort(phraseProbNodes);

			for (Phrase claim : testRelations.getAspects()) {

				int counter = -1;

				if (claim.getExitNodes().size() > 1) {
					continue;
				}

				do {
					counter++;
					resultWords = claim
							.getPatternTarget((RelationPatternNode) relationProbNodes.get(counter).getTree());
					if (resultWords != null && !resultWords.isEmpty()) {
						results = getFirstResultForPhrase(phraseProbNodes, resultWords);
						for (Result result : results) {
							result.setAspect(claim);
							result.setRelationProbabilityNode(relationProbNodes.get(counter));
							result.checkResult(1);
							allResults.add(result);
						}

					}
					if (counter > relationProbNodes.size() - 2) {
						break;
					}
				} while (counter <= relationProbNodes.size() - 2);

			}
			System.out.println("");
			evalutateResultsAt(allResults, testRelations.all().size(), topX);

		}
		return new ResultStats();
	}

	public ResultStats get10FoldResultsFinal(ArrayList<Relations> relations, int limit, int topX, int noise,
			double threshhold, CompType compRelation, CompType compPhrase) {

		RelationPatternNode currentPattern;
		Result currentResult;
		ResultStats all = new ResultStats();
		for (int i = 0; i < relations.size(); i++) {

			Relations testRelations = new Relations();
			Relations trainingRelations = new Relations();
			for (int j = 0; j < relations.size(); j++) {
				if (j == i) {
					testRelations = relations.get(j);
				}
				else {
					trainingRelations.append(relations.get(j));
				}
			}

			ArrayList<ProbabilityNode> relationProbNodesInit = initializeRelationProbablilityNodes(trainingRelations);
			ArrayList<ProbabilityNode> relationProbNodes = new ArrayList<ProbabilityNode>();
			for (ProbabilityNode probabilityNode : relationProbNodesInit) {
				if (probabilityNode.getTotalSupport() > limit) {
					probabilityNode.setSortVariable(compRelation);
					relationProbNodes.add(probabilityNode);
				}
			}

			ArrayList<ProbabilityNode> phraseProbNodesInit = initializePhraseProbablilityNodes(
					trainingRelations.getStatements());
			ArrayList<ProbabilityNode> phraseProbNodes = new ArrayList<ProbabilityNode>();
			for (ProbabilityNode probabilityNode : phraseProbNodesInit) {
				if (probabilityNode.getTotalSupport() >= limit) {
					probabilityNode.setSortVariable(compPhrase);
					phraseProbNodes.add(probabilityNode);
				}
			}

			Collections.sort(relationProbNodes);
			Collections.sort(phraseProbNodes);

			ArrayList<Result> allResults = new ArrayList<Result>();
			ArrayList<Result> currentResults = new ArrayList<Result>();

			for (Phrase claim : testRelations.getAspects()) {
				double adjustedSV = 0.0;
				double currentSV = 0.0;
				int counter = -1;
				currentResults.clear();
				do {
					counter++;
					currentPattern = (RelationPatternNode) relationProbNodes.get(counter).getTree();
					currentResult = claim.getPatternTargetResult(currentPattern);
					currentResult.setRelationProbabilityNode(relationProbNodes.get(counter));
					if (currentResult.hasTargetNodes()) {
						currentSV = relationProbNodes.get(counter).getSortVariable();
						if (adjustedSV == 0.0) {
							adjustedSV = currentSV - (currentSV * threshhold);
						}
						if (currentSV >= adjustedSV) {

							currentResult = getPhraseProbResult(phraseProbNodes, currentResult);// getResultForPhraseWithThreshhold(phraseProbNodes,
																								// resultWords,
																								// threshhold);
							currentResult.checkResult(noise);
							allResults.add(currentResult);

						}
					}
					if (counter > relationProbNodes.size() - 2) {
						break;
					}
				} while (counter <= relationProbNodes.size() - 2);

			}
			all.add(evalutateResultsAt(allResults, testRelations.all().size(), topX));
		}
		return all;
	}

	public ResultStats get10FoldResultsWithParametersSRL(ArrayList<Relations> relations, int limit, int topX, int noise,
			double threshhold, Comparator<ProbabilityNode> comp) {

		ResultStats all = new ResultStats();
		RelationPatternNode currentPattern;
		Result currentResult;

		for (int i = 0; i < relations.size(); i++) {
			Relations testRelations = new Relations();
			Relations trainingRelations = new Relations();
			for (int j = 0; j < relations.size(); j++) {
				if (j == i) {
					testRelations = relations.get(j);
				}
				else {
					trainingRelations.append(relations.get(j));
				}
			}

			ArrayList<ProbabilityNode> relationProbNodesInit = initializeRelationProbablilityNodes(trainingRelations);
			ArrayList<ProbabilityNode> relationProbNodes = new ArrayList<ProbabilityNode>();
			for (ProbabilityNode probabilityNode : relationProbNodesInit) {
				if (probabilityNode.getTotalSupport() > limit) {
					relationProbNodes.add(probabilityNode);
				}
			}

			ArrayList<Result> allResults = new ArrayList<Result>();
			ArrayList<Result> currentResults = new ArrayList<Result>();

			Collections.sort(relationProbNodes, comp);
			for (Phrase claim : testRelations.getAspects()) {
				double adjustedAccuracy = 0.0;
				double currentAccuracy = 0.0;
				int counter = -1;
				currentResults.clear();
				do {
					counter++;
					currentPattern = (RelationPatternNode) relationProbNodes.get(counter).getTree();
					currentResult = claim.getPatternTargetResult(currentPattern);
					if (currentResult.hasTargetNodes()) {
						currentAccuracy = relationProbNodes.get(counter).getAccuracy();
						if (adjustedAccuracy == 0.0) {
							adjustedAccuracy = currentAccuracy - (currentAccuracy * threshhold);
						}
						if (currentAccuracy >= adjustedAccuracy) {

							currentResult.setPhrasesSRL();
							if (currentResult.hasPhraseNodes()) {
								currentResult.checkResult(noise);
								allResults.add(currentResult);
							}

						}
					}
					if (counter > relationProbNodes.size() - 2) {
						break;
					}
				} while (counter <= relationProbNodes.size() - 2);

			}
			all.add(evalutateResultsAt(allResults, testRelations.all().size(), topX));
		}
		return all;
	}

	public ResultStats getAllPhrasesAboveAccuracyThreshhold(ArrayList<Phrase> phrasesTraining,
			ArrayList<Phrase> phrasesTest, double threshhold, int limit, int distance) {
		ArrayList<ProbabilityNode> pNodesInit = initializePhraseProbablilityNodes(phrasesTraining);
		ArrayList<ProbabilityNode> pNodes = new ArrayList<ProbabilityNode>();
		for (ProbabilityNode probabilityNode : pNodesInit) {
			if (probabilityNode.getTotalSupport() > limit) {
				pNodes.add(probabilityNode);
			}
		}
		ResultStats r = new ResultStats();
		r.setTotal(phrasesTest.size());
		double adjustedAccuracy = 0.0;
		double currentAccuracy = 0.0;
		for (Phrase phrase : phrasesTest) {
			if (!pNodes.contains(phrase.getProbabilityNode())) {
				r.setTotal(r.getTotal() - 1);
				continue;
			}
			phrase.setFound(false);
			int i = -1;
			ArrayList<WordNode> results = new ArrayList<WordNode>();
			do {
				i++;
				for (WordNode exitNode : phrase.getExitNodes()) {

					results = exitNode.getAllNodes((PhrasePatternNode) pNodes.get(i).getTree());
					if (results != null && !results.isEmpty()) {

						currentAccuracy = pNodes.get(i).getAccuracy();
						if (adjustedAccuracy == 0.0) {
							adjustedAccuracy = currentAccuracy;
						}
						if (currentAccuracy > adjustedAccuracy - (adjustedAccuracy * threshhold)) {

							if (!phrase.isFound()) {
								if (phrase.getWords().containsAll(results)
										&& Math.abs(phrase.getWords().size() - results.size()) < distance) {
									// results.containsAll(phrase.getWords())) {
									r.addHit();
									phrase.setFound(true);
								}
								else {
									r.addMiss();
								}
							}
						}
					}
				}

				if (i > pNodes.size() - 2) {
					break;
				}
			} while (i <= pNodes.size() - 2);

		}
		System.out.println("Recall:    " + r.getRecall());
		System.out.println("Precision: " + r.getPrecision());
		System.out.println("F1:        " + r.getF1Score());
		System.out.println("");
		return r;

	}

	public ResultStats getAllRelationsAboveAccuracyThreshhold(Relations relations, ArrayList<Phrase> claims,
			double threshhold) {
		ArrayList<ProbabilityNode> pNodes = initializeRelationProbablilityNodes(relations);

		ResultStats r = new ResultStats();
		r.setTotal(relations.all().size());
		double adjustedAccuracy = 0.0;
		double currentAccuracy = 0.0;
		boolean found = false;
		for (Phrase claim : claims) {

			int i = -1;
			ArrayList<WordNode> results = new ArrayList<WordNode>();
			ArrayList<Relation> aspectRelations = relations.getByAspect(claim);
			if (aspectRelations != null) {
				for (Relation relation : aspectRelations) {
					relation.setFound(false);
				}

			}
			if (claim.getExitNodes().size() > 1) {
				if (aspectRelations != null) {
					r.setTotal(r.getTotal() - aspectRelations.size());
				}
				continue;
			}

			do {
				i++;
				results = claim.getPatternTarget((RelationPatternNode) pNodes.get(i).getTree());
				found = false;
				if (aspectRelations != null && results != null && !results.isEmpty()) {
					currentAccuracy = pNodes.get(i).getAccuracy();
					if (adjustedAccuracy == 0.0) {
						adjustedAccuracy = currentAccuracy;
					}
					if (currentAccuracy >= adjustedAccuracy - (adjustedAccuracy * threshhold)) {

						// check if result matches any relation
						for (Relation relation : aspectRelations) {
							if (!relation.isFound()) {
								if (results.containsAll(relation.getStatement().getExitNodes())) {
									found = true;
									relation.setFound(true);
								}

							}
						}
						if (found) {
							r.addHit();
						}
						else {
							r.addMiss();
						}
					}

				}
				if (i > pNodes.size() - 2) {
					break;
				}
			} while (i <= pNodes.size() - 2);
		}
		System.out.println("Recall:    " + r.getRecall());
		System.out.println("Precision: " + r.getPrecision());
		System.out.println("F1:        " + r.getF1Score());
		System.out.println("");
		return r;
	}

	public ArrayList<Result> getFirstResultForPhrase(ArrayList<ProbabilityNode> pNodes, ArrayList<WordNode> words) {

		ArrayList<Result> results = new ArrayList<Result>();
		ProbabilityNode currentProbNode;

		int i = -1;
		ArrayList<WordNode> resultWordNode = new ArrayList<WordNode>();
		for (WordNode exitNode : words) {
			i = 0;
			do {
				i++;

				currentProbNode = pNodes.get(i);
				resultWordNode = exitNode.getAllNodes((PhrasePatternNode) currentProbNode.getTree());
				if (resultWordNode != null && !resultWordNode.isEmpty()) {
					Result r = new Result();
					r.setPhraseProbabilityNode(currentProbNode);
					r.setPhraseNodes(resultWordNode);
					results.add(r);
					break;
				}

				if (i > pNodes.size() - 2) {
					break;
				}
			} while (i <= pNodes.size() - 2);
		}
		return results;
	}

	public String getLatexTableString(String columnName, Double p, Double r, Double f1, Double p10, Double p20,
			Double p50) {
		return columnName + " & $" + round(p, 2) + "$ & $" + round(r, 2) + "$ & $" + round(f1, 2) + "$ & $"
				+ round(p10, 2) + "$ & $" + round(p20, 2) + "$ & $" + round(p50, 2) + "$ \\\\ \\hline";
	}

	public Result getPhraseProbResult(ArrayList<ProbabilityNode> pNodes, Result input) {

		ProbabilityNode currentProbNode;
		int i = -1;
		ArrayList<WordNode> resultWordNode = new ArrayList<WordNode>();
		for (WordNode exitNode : input.getTargetExitNodes()) {
			i = 0;
			do {
				i++;

				currentProbNode = pNodes.get(i);
				resultWordNode = exitNode.getAllNodes((PhrasePatternNode) currentProbNode.getTree());
				if (resultWordNode != null && !resultWordNode.isEmpty()) {
					input.setPhraseProbabilityNode(currentProbNode);
					input.setPhraseNodes(resultWordNode);
					break;
				}

				if (i > pNodes.size() - 2) {
					break;
				}
			} while (i <= pNodes.size() - 2);
		}
		return input;
	}

	public ResultStats getPhraseResultsWithSRL(Relations relations, int limit, int topX, int noise) {
		ResultStats all = new ResultStats();
		Relations clearedRelation = relations.getClearedRelations();
		ArrayList<WordNode> resultWords;
		Result currentResult = new Result();
		ArrayList<Result> allResults = new ArrayList<Result>();
		for (Phrase premisePhrase : clearedRelation.getStatements()) {

			resultWords = premisePhrase.getExitNodes();
			if (resultWords.size() == 1) {
				currentResult = new Result();
				currentResult.setCorrectStatement(premisePhrase);
				currentResult.setTargetExitNodes(premisePhrase.getExitNodes());
				currentResult.setPhrasesSRL();
				currentResult.checkPhraseVsGold(noise);

				allResults.add(currentResult);

			}
			else {
				System.out.print("ajklsdfhgjhkgb");
			}

		}
		all.add(evalutateResultsAt(allResults, clearedRelation.all().size(), topX));
		return all;
	}

	public ResultStats getResultFromSRL(ArrayList<Phrase> claims, int total) {
		ResultStats r = new ResultStats();
		r.setTotal(total);
		WordNode target = null;
		Boolean found = false;
		for (Phrase claim : claims) {
			for (WordNode word : claim.getWords()) {
				target = getWordBySRL(word);
				if (target != null) {
					break;
				}
			}
			if (target != null) {
				for (Phrase premise : claim.getTargets()) {
					found = false;
					for (WordNode premiseNode : premise.getExitNodes()) {
						found = premiseNode.checkForPredecessor(target);
						if (found) {
							r.addHit();
						}
					}
					if (!found) {
						r.addMiss();
					}
				}
			}

		}
		System.out.println("Recall:    " + r.getRecall());
		System.out.println("Precision: " + r.getPrecision());
		System.out.println("F1:        " + r.getF1Score());
		System.out.println("");
		return r;
	}

	public ArrayList<Result> getResultListFromAdjective(ArrayList<Phrase> claims) {
		ArrayList<Result> results = new ArrayList<Result>();
		Result r;
		WordNode adjective;
		for (Phrase claim : claims) {
			adjective = claim.getSentence().getWordByType(claim, "ADJECTIVE");
			for (Phrase premise : claim.getTargets()) {
				if (premise.contains(adjective)) {
					r = new Result();
					r.setAspect(claim);
					r.setCorrectStatement(premise);
					r.setCorrectTargetExitNode(true);
					r.setCorrect(true);
					results.add(r);
				}
				else {
				}

			}
		}
		return results;
	}

	public ArrayList<Result> getResults(Relations relations, ArrayList<Phrase> claims, ArrayList<Phrase> premises,
			int limit) {
		ArrayList<ProbabilityNode> relationProbNodes = initializeRelationProbablilityNodes(relations);

		ArrayList<ProbabilityNode> phraseProbNodesInit = initializePhraseProbablilityNodes(premises);
		ArrayList<ProbabilityNode> phraseProbNodes = new ArrayList<ProbabilityNode>();
		ArrayList<Result> results = new ArrayList<Result>();
		ArrayList<Result> allResults = new ArrayList<Result>();
		ArrayList<WordNode> resultWords = new ArrayList<WordNode>();

		for (ProbabilityNode probabilityNode : phraseProbNodesInit) {
			if (probabilityNode.getTotalSupport() > limit) {
				phraseProbNodes.add(probabilityNode);
			}
		}
		Collections.sort(phraseProbNodes);

		for (Phrase claim : claims) {

			int i = -1;

			if (claim.getExitNodes().size() > 1) {
				continue;
			}

			do {
				i++;
				resultWords = claim.getPatternTarget((RelationPatternNode) relationProbNodes.get(i).getTree());
				if (resultWords != null && !resultWords.isEmpty()) {

					results = getFirstResultForPhrase(phraseProbNodes, resultWords);
					for (Result result : results) {
						result.setAspect(claim);
						result.setRelationProbabilityNode(relationProbNodes.get(i));
						result.checkResult(0);
						allResults.add(result);
						System.out.println(result);
					}
				}
				if (i > relationProbNodes.size() - 2) {
					break;
				}
			} while (i <= relationProbNodes.size() - 2);

		}
		return allResults;
	}

	public ArrayList<Result> getResultsList(Relations relations, int limit, int topX, int noise, double threshhold,
			CompType compRelation, CompType compPhrase, String aspect) {

		RelationPatternNode currentPattern;
		Result currentResult;
		Relations clearedRelation = relations.getClearedRelations();
		ArrayList<Relations> relationsList = clearedRelation.split10();

		Relations testRelations = new Relations();
		Relations trainingRelations = new Relations();
		for (int i = 0; i < relationsList.size(); i++) {
			ArrayList<Result> allResults = new ArrayList<Result>();
			for (int j = 0; j < relationsList.size(); j++) {
				if (j == i) {
					testRelations = relationsList.get(j);
				}
				else {
					trainingRelations.append(relationsList.get(j));
				}
			}

			ArrayList<ProbabilityNode> relationProbNodesInit = initializeRelationProbablilityNodes(trainingRelations);
			ArrayList<ProbabilityNode> relationProbNodes = new ArrayList<ProbabilityNode>();
			for (ProbabilityNode probabilityNode : relationProbNodesInit) {
				if (probabilityNode.getTotalSupport() > limit) {
					probabilityNode.setSortVariable(compRelation);
					relationProbNodes.add(probabilityNode);
				}
			}

			ArrayList<ProbabilityNode> phraseProbNodesInit = initializePhraseProbablilityNodes(
					trainingRelations.getStatements());
			ArrayList<ProbabilityNode> phraseProbNodes = new ArrayList<ProbabilityNode>();
			for (ProbabilityNode probabilityNode : phraseProbNodesInit) {
				if (probabilityNode.getTotalSupport() >= limit) {
					probabilityNode.setSortVariable(compPhrase);
					phraseProbNodes.add(probabilityNode);
				}
			}

			ArrayList<Result> currentResults = new ArrayList<Result>();

			Collections.sort(relationProbNodes);
			Collections.sort(phraseProbNodes);
			for (Phrase claim : testRelations.getAspects()) {
				double adjustedSV = 0.0;
				double currentSV = 0.0;
				int counter = -1;
				currentResults.clear();
				do {
					counter++;
					currentPattern = (RelationPatternNode) relationProbNodes.get(counter).getTree();
					if (claim.getId().equals("A16PH8I60IGM4F-claim-2")) {
						if (claim.getPatternTargetResult(currentPattern).hasTargetNodes()) {

							System.out.println("");
						}
					}
					currentResult = claim.getPatternTargetResult(currentPattern);
					currentResult.setRelationProbabilityNode(relationProbNodes.get(counter));
					if (currentResult.hasTargetNodes()) {
						currentSV = relationProbNodes.get(counter).getSortVariable();
						if (adjustedSV == 0.0) {
							adjustedSV = currentSV - (currentSV * threshhold);
						}
						if (currentSV >= adjustedSV) {

							currentResult = getPhraseProbResult(phraseProbNodes, currentResult);// getResultForPhraseWithThreshhold(phraseProbNodes,
																								// resultWords,
																								// threshhold);
							currentResult.checkResult(noise);
							allResults.add(currentResult);
							currentResult = new Result();
						}
					}
					if (counter > relationProbNodes.size() - 2) {
						break;
					}
				} while (counter <= relationProbNodes.size() - 2);

			}
			Collections.sort(allResults);
			this.printResultsforAspect(allResults, aspect);
			System.out.println("");
		}

		return null;
	}

	public void getStats(ArrayList<Review> reviews) {
		ArrayList<Phrase> claims = new ArrayList<Phrase>();
		ArrayList<Phrase> premises = new ArrayList<Phrase>();
		Relations relations = new Relations();

		for (Review review : reviews) {
			if (review.getAllPhrases().getClaims().size() > 0) {
				claims.addAll(review.getAllPhrases().getClaims());
			}

			if (review.getAllPhrases().getPremises().size() > 0) {
				premises.addAll(review.getAllPhrases().getPremises());
			}
			for (Relation rel : review.getRelations()) {
				relations.add(rel);
			}
		}
		int counterClaims = 0;
		for (Phrase claim : claims) {
			if (claim.getExitNodes().size() > 1) {
				counterClaims++;
			}
		}
		int counterPremises = 0;
		for (Phrase premise : premises) {
			if (premise.getExitNodes().size() > 1) {
				counterPremises++;
			}
		}
		System.out.println("claims: ");
		System.out.println(claims.size() + " (" + counterClaims + ")");
		System.out.println("premises:");
		System.out.println(premises.size() + " (" + counterPremises + ")");
		System.out.println("relations:");
		System.out.println(relations.all().size());
	}

	public ResultStats getSubtreePhrases(ArrayList<Phrase> phrases, int limit) {

		ArrayList<WordNode> results = new ArrayList<WordNode>();
		ResultStats r = new ResultStats();
		r.setTotal(phrases.size());
		for (Phrase phrase : phrases) {
			for (WordNode exitNode : phrase.getExitNodes()) {

				results = exitNode.getSubtreeNodes();
				if (results != null && !results.isEmpty()) {
					if (phrase.getWords().containsAll(results)
							&& Math.abs(phrase.getWords().size() - results.size()) < limit) {
						// results.containsAll(phrase.getWords())) {
						r.addHit();
						phrase.setFound(true);
					}
					else {
						r.addMiss();
					}
				}
			}

		}

		System.out.println("Recall:    " + r.getRecall());
		System.out.println("Precision: " + r.getPrecision());
		System.out.println("F1:        " + r.getF1Score());
		System.out.println("");
		return r;

	}

	public WordNode getWordBySRL(WordNode aspect) {
		WordNode current = null;
		WordNode prev = aspect;
		WordNode result = null;
		while (current != prev && result == null) {
			current = prev;
			prev = current.getPredecessor();
			for (WordNode wordNode : current.getArgumentRoots()) {
				result = wordNode.getOtherArguments(current);
			}

		}
		return result;
	}

	public void latexTablePrint(ArrayList<Relations> relations, ArrayList<Phrase> claims) {
		int limit = 5;
		int topX = 500000;
		int noise = 0;
		double threshold = .05;

		System.out.println("limit: " + limit);
		System.out.println("topX: " + topX);
		System.out.println("noise: " + noise);
		System.out.println("threshold: " + threshold);

		ResultStats allAccN0 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.ACCURACY,
				CompType.ACCURACY);
		ResultStats allSupN0 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.SUPPORT,
				CompType.SUPPORT);
		ResultStats allBothN0 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.BOTH);
		ResultStats allBesthN0 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.ACCURACY);

		topX = 10;
		ResultStats allAccN0at10 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY, CompType.ACCURACY);
		ResultStats allSupN0at10 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT, CompType.SUPPORT);
		ResultStats allBothN0at10 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.BOTH);
		ResultStats allBesthN0at10 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.ACCURACY);

		topX = 20;
		ResultStats allAccN0at20 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY, CompType.ACCURACY);
		ResultStats allSupN0at20 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT, CompType.SUPPORT);
		ResultStats allBothN0at20 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.BOTH);
		ResultStats allBesthN0at20 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.ACCURACY);

		topX = 50;
		ResultStats allAccN0at50 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY, CompType.ACCURACY);
		ResultStats allSupN0at50 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT, CompType.SUPPORT);
		ResultStats allBothN0at50 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.BOTH);
		ResultStats allBesthN0at50 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.ACCURACY);

		System.out.println("all n0");
		System.out.println(this.getLatexTableString("accuracy", allAccN0.getPrecision(), allAccN0.getRecall(),
				allAccN0.getF1Score(), allAccN0at10.getPrecision(), allAccN0at20.getPrecision(),
				allAccN0at50.getPrecision()));
		System.out.println(this.getLatexTableString("support", allSupN0.getPrecision(), allSupN0.getRecall(),
				allSupN0.getF1Score(), allSupN0at10.getPrecision(), allSupN0at20.getPrecision(),
				allSupN0at50.getPrecision()));
		System.out.println(this.getLatexTableString("acc. \\& sup.", allBothN0.getPrecision(), allBothN0.getRecall(),
				allBothN0.getF1Score(), allBothN0at10.getPrecision(), allBothN0at20.getPrecision(),
				allBothN0at50.getPrecision()));
		System.out.println(this.getLatexTableString("best", allBesthN0.getPrecision(), allBesthN0.getRecall(),
				allBesthN0.getF1Score(), allBesthN0at10.getPrecision(), allBesthN0at20.getPrecision(),
				allBesthN0at50.getPrecision()));
		System.out.println("");

		topX = 500000;
		noise = 1;
		ResultStats allAccN1 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.ACCURACY,
				CompType.ACCURACY);
		ResultStats allSupN1 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.SUPPORT,
				CompType.SUPPORT);
		ResultStats allBothN1 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.BOTH);
		ResultStats allBesthN1 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.ACCURACY);

		topX = 10;
		ResultStats allAccN1at10 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY, CompType.ACCURACY);
		ResultStats allSupN1at10 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT, CompType.SUPPORT);
		ResultStats allBothN1at10 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.BOTH);
		ResultStats allBesthN1at10 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.ACCURACY);

		topX = 20;
		ResultStats allAccN1at20 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY, CompType.ACCURACY);
		ResultStats allSupN1at20 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT, CompType.SUPPORT);
		ResultStats allBothN1at20 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.BOTH);
		ResultStats allBesthN1at20 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.ACCURACY);

		topX = 50;
		ResultStats allAccN1at50 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY, CompType.ACCURACY);
		ResultStats allSupN1at50 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT, CompType.SUPPORT);
		ResultStats allBothN1at50 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.BOTH);
		ResultStats allBesthN1at50 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.ACCURACY);

		System.out.println("all n1");
		System.out.println(this.getLatexTableString("accuracy", allAccN1.getPrecision(), allAccN1.getRecall(),
				allAccN1.getF1Score(), allAccN1at10.getPrecision(), allAccN1at20.getPrecision(),
				allAccN1at50.getPrecision()));
		System.out.println(this.getLatexTableString("support", allSupN1.getPrecision(), allSupN1.getRecall(),
				allSupN1.getF1Score(), allSupN1at10.getPrecision(), allSupN1at20.getPrecision(),
				allSupN1at50.getPrecision()));
		System.out.println(this.getLatexTableString("acc. \\& sup.", allBothN1.getPrecision(), allBothN1.getRecall(),
				allBothN1.getF1Score(), allBothN1at10.getPrecision(), allBothN1at20.getPrecision(),
				allBothN1at50.getPrecision()));
		System.out.println(this.getLatexTableString("best", allBesthN1.getPrecision(), allBesthN1.getRecall(),
				allBesthN1.getF1Score(), allBesthN1at10.getPrecision(), allBesthN1at20.getPrecision(),
				allBesthN1at50.getPrecision()));

		System.out.println("");

		topX = 500000;
		noise = 2;
		ResultStats allAccN2 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.ACCURACY,
				CompType.ACCURACY);
		ResultStats allSupN2 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.SUPPORT,
				CompType.SUPPORT);
		ResultStats allBothN2 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.BOTH);
		ResultStats allBesthN2 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.ACCURACY);

		topX = 10;
		ResultStats allAccN2at10 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY, CompType.ACCURACY);
		ResultStats allSupN2at10 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT, CompType.SUPPORT);
		ResultStats allBothN2at10 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.BOTH);
		ResultStats allBesthN2at10 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.ACCURACY);

		topX = 20;
		ResultStats allAccN2at20 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY, CompType.ACCURACY);
		ResultStats allSupN2at20 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT, CompType.SUPPORT);
		ResultStats allBothN2at20 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.BOTH);
		ResultStats allBesthN2at20 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.ACCURACY);

		topX = 50;
		ResultStats allAccN2at50 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY, CompType.ACCURACY);
		ResultStats allSupN2at50 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT, CompType.SUPPORT);
		ResultStats allBothN2at50 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.BOTH);
		ResultStats allBesthN2at50 = this.get10FoldResultsFinal(relations, limit, topX, noise, threshold, CompType.BOTH,
				CompType.ACCURACY);

		System.out.println("all n2");
		System.out.println(this.getLatexTableString("accuracy", allAccN2.getPrecision(), allAccN2.getRecall(),
				allAccN2.getF1Score(), allAccN2at10.getPrecision(), allAccN2at20.getPrecision(),
				allAccN2at50.getPrecision()));
		System.out.println(this.getLatexTableString("support", allSupN2.getPrecision(), allSupN2.getRecall(),
				allSupN2.getF1Score(), allSupN2at10.getPrecision(), allSupN2at20.getPrecision(),
				allSupN2at50.getPrecision()));
		System.out.println(this.getLatexTableString("acc. \\& sup.", allBothN2.getPrecision(), allBothN2.getRecall(),
				allBothN2.getF1Score(), allBothN2at10.getPrecision(), allBothN2at20.getPrecision(),
				allBothN2at50.getPrecision()));
		System.out.println(this.getLatexTableString("best", allBesthN2.getPrecision(), allBesthN2.getRecall(),
				allBesthN2.getF1Score(), allBesthN2at10.getPrecision(), allBesthN2at20.getPrecision(),
				allBesthN2at50.getPrecision()));
		System.out.println("");

		// Relations:

		topX = 50000;
		ResultStats relationAcc = this.get10FoldRelationResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY);
		ResultStats relationSup = this.get10FoldRelationResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT);
		ResultStats relationBoth = this.get10FoldRelationResultsFinal(relations, limit, topX, noise, threshold,
				CompType.BOTH);
		topX = 10;
		ResultStats relationAccat10 = this.get10FoldRelationResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY);
		ResultStats relationSupat10 = this.get10FoldRelationResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT);
		ResultStats relationBothat10 = this.get10FoldRelationResultsFinal(relations, limit, topX, noise, threshold,
				CompType.BOTH);

		topX = 20;
		ResultStats relationAccat20 = get10FoldRelationResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY);
		ResultStats relationSupat20 = this.get10FoldRelationResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT);
		ResultStats relationBothat20 = this.get10FoldRelationResultsFinal(relations, limit, topX, noise, threshold,
				CompType.BOTH);

		topX = 50;
		ResultStats relationAccat50 = this.get10FoldRelationResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY);
		ResultStats relationSupat50 = this.get10FoldRelationResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT);
		ResultStats relationBothat50 = this.get10FoldRelationResultsFinal(relations, limit, topX, noise, threshold,
				CompType.BOTH);

		String line;
		System.out.println("relations");
		System.out.println("");
		line = "accuracy & $" + round(relationAcc.getPrecision(), 2) + "$ & $" + round(relationAcc.getRecall(), 2)
				+ "$ & $" + round(relationAcc.getF1Score(), 2) + "$ & $" + round(relationAccat10.getPrecision(), 2)
				+ "$ & $" + round(relationAccat20.getPrecision(), 2) + "$ & $"
				+ round(relationAccat50.getPrecision(), 2) + "$ \\\\ \\hline";
		System.out.println(line);
		line = "support & $" + round(relationSup.getPrecision(), 2) + "$ & $" + round(relationSup.getRecall(), 2)
				+ "$ & $" + round(relationSup.getF1Score(), 2) + "$ & $" + round(relationSupat10.getPrecision(), 2)
				+ "$ & $" + round(relationSupat20.getPrecision(), 2) + "$ & $"
				+ round(relationSupat50.getPrecision(), 2) + "$ \\\\ \\hline";
		System.out.println(line);
		line = "acc. \\& sup. & $" + round(relationBoth.getPrecision(), 2) + "$ & $"
				+ round(relationBoth.getRecall(), 2) + "$ & $" + round(relationBoth.getF1Score(), 2) + "$ & $"
				+ round(relationBothat10.getPrecision(), 2) + "$ & $" + round(relationBothat20.getPrecision(), 2)
				+ "$ & $" + round(relationBothat50.getPrecision(), 2) + "$ \\\\ \\hline";
		System.out.println(line);
		System.out.println("");

		topX = 500000;
		noise = 0;
		System.out.println("phrases");
		ResultStats phraseAccN0 = this.get10FoldPhraseResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY);
		ResultStats phraseSupN0 = this.get10FoldPhraseResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT);
		ResultStats phraseBothN0 = this.get10FoldPhraseResultsFinal(relations, limit, topX, noise, threshold,
				CompType.BOTH);

		topX = 10;
		ResultStats phraseAccN0at10 = this.get10FoldPhraseResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY);
		ResultStats phraseSupN0at10 = this.get10FoldPhraseResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT);
		ResultStats phraseBothN0at10 = this.get10FoldPhraseResultsFinal(relations, limit, topX, noise, threshold,
				CompType.BOTH);

		topX = 20;
		ResultStats phraseAccN0at20 = this.get10FoldPhraseResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY);
		ResultStats phraseSupN0at20 = this.get10FoldPhraseResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT);
		ResultStats phraseBothN0at20 = this.get10FoldPhraseResultsFinal(relations, limit, topX, noise, threshold,
				CompType.BOTH);

		topX = 50;
		ResultStats phraseAccN0at50 = this.get10FoldPhraseResultsFinal(relations, limit, topX, noise, threshold,
				CompType.ACCURACY);
		ResultStats phraseSupN0at50 = this.get10FoldPhraseResultsFinal(relations, limit, topX, noise, threshold,
				CompType.SUPPORT);
		ResultStats phraseBothN0at50 = this.get10FoldPhraseResultsFinal(relations, limit, topX, noise, threshold,
				CompType.BOTH);

		System.out.println("phrase n0");
		System.out.println(this.getLatexTableString("accuracy", phraseAccN0.getPrecision(), phraseAccN0.getRecall(),
				phraseAccN0.getF1Score(), phraseAccN0at10.getPrecision(), phraseAccN0at20.getPrecision(),
				phraseAccN0at50.getPrecision()));
		System.out.println(this.getLatexTableString("support", phraseSupN0.getPrecision(), phraseSupN0.getRecall(),
				phraseSupN0.getF1Score(), phraseSupN0at10.getPrecision(), phraseSupN0at20.getPrecision(),
				phraseSupN0at50.getPrecision()));
		System.out.println(this.getLatexTableString("acc. \\& sup.", phraseBothN0.getPrecision(),
				phraseBothN0.getRecall(), phraseBothN0.getF1Score(), phraseBothN0at10.getPrecision(),
				phraseBothN0at20.getPrecision(), phraseBothN0at50.getPrecision()));

		System.out.println("");
	}

	public void printFischer(ArrayList<Result> a, ArrayList<Result> b, int total) {
		ArrayList<String> resultsA = new ArrayList<String>();
		ArrayList<String> resultsB = new ArrayList<String>();
		String key;
		int totalAB = 0;
		int totalAnB = 0;
		int totalnAB = 0;
		int totalnAnB = total;
		for (Result result : a) {
			if (result.getCorrect()) {
				resultsA.add(result.getRelationAsText());
			}
		}
		totalnAnB -= resultsA.size();
		totalAnB = resultsA.size();
		for (Result result : b) {
			if (result.getCorrect()) {
				key = result.getRelationAsText();
				if (resultsA.contains(key)) {
					totalAB++;
					totalAnB--;
					resultsA.remove(key);
				}
				else {
					resultsB.add(key);
					totalnAnB--;
				}
			}
		}
		totalnAB = resultsB.size();
		int sizeA = resultsA.size();
		int sizeB = resultsB.size();
		double res = Math.pow(Math.abs((sizeA - sizeB)) - 1, 2) / (sizeA + sizeB);
		printMatrix(totalAB, totalnAB, totalAnB, totalnAnB);
		printMatrix(totalAB, totalnAB, totalAnB, totalnAnB);
		System.out.println(totalAB + " | " + totalnAB);
		System.out.println(totalAnB + " | " + totalnAnB);
		if ((sizeA + sizeB) < 25) {
			System.out.println("sample size too small");
		}
		System.out.println(res);

		if (res > 3.84) {
			System.out.println("significant");
		}
	}

	public void printMatrix(int a, int b, int c, int d) {
		System.out.println("x <- matric(c(" + a + "," + b + ", " + c + ", " + d + ",2,2))");
	}

	public void printMcNemar(ArrayList<Result> a, ArrayList<Result> b) {
		ArrayList<String> resultsA = new ArrayList<String>();
		ArrayList<String> resultsB = new ArrayList<String>();
		String key;

		for (Result result : a) {
			if (result.getCorrect()) {
				resultsA.add(result.getRelationAsText());
			}
		}

		for (Result result : b) {
			if (result.getCorrect()) {
				key = result.getRelationAsText();
				if (resultsA.contains(key)) {
					resultsA.remove(key);
				}
				else {
					resultsB.add(key);
				}
			}
		}
		int sizeA = resultsA.size();
		int sizeB = resultsB.size();
		double res = Math.pow(Math.abs((sizeA - sizeB)) - 1, 2) / (sizeA + sizeB);
		if ((sizeA + sizeB) < 25) {
			System.out.println("sample size too small");
		}
		System.out.println(res);

		if (res > 3.84) {
			System.out.println("significant");
		}
		System.out.println(sizeA + ", " + sizeB);
	}

	public void printResultListRelation(ArrayList<Result> a, ArrayList<Result> b) {
		HashMap<String, String> results = new HashMap<String, String>();
		String key;
		for (Result result : a) {
			results.put(result.getRelationAsText(), (result.getCorrectTargetExitNode() == true ? "1" : "0") + "; 0");

		}
		for (Result result : b) {
			key = result.getRelationAsText();
			if (results.containsKey(key)) {
				results.put(key, results.get(key).substring(0, results.get(key).length() - 1)
						+ (result.getCorrectTargetExitNode() == true ? "1" : "0"));
			}
			else {
				results.put(key, "0; " + (result.getCorrectTargetExitNode() == true ? "1" : "0"));
			}
		}
		for (String currentKey : results.keySet()) {
			System.out.println(currentKey + "; " + results.get(currentKey));
		}
	}

	public void printResultsforAspect(ArrayList<Result> allResults, String aspect) {

		String line = "";
		ArrayList<WordNode> phrase;
		for (Result result : allResults) {
			if (result.getAspect().getText().contains(aspect)) {
				if (result.getAspect().getId().equals("A16PH8I60IGM4F-claim-2")) {
					result.getRelationPattern().print();
					line = "";
				}
				line = result.getAspect().getText() + " &";
				phrase = result.getPhraseNodes();
				Collections.sort(phrase);
				for (WordNode wordNode : phrase) {
					line += " " + wordNode.getText();
				}
				line += "% & ";
				if (result.getCorrectStatement() != null) {
					line += result.getCorrectStatement().getText();
				}
				line += " " + result.getAspect().getId();
				System.out.println(line);
			}
		}

	}

	public ResultStats testingReviewRaters(Review[] rev1, Review[] rev2) {
		ResultStats res = new ResultStats();
		ArrayList<Phrase> done = new ArrayList<Phrase>();
		ArrayList<Phrase> phraseList1 = new ArrayList<Phrase>();
		ArrayList<Phrase> phraseList2 = new ArrayList<Phrase>();
		Review currentRev1;
		Review currentRev2;
		Boolean found = false;
		int countCorrect = 0;
		int count1 = 0;
		int count2 = 0;

		if (rev1.length != rev2.length) {
			return res;
		}
		for (int i = 0; i < rev1.length; i++) {
			currentRev1 = rev1[i];
			currentRev2 = rev2[i];
			currentRev1.initialize();
			currentRev2.initialize();
			phraseList1 = currentRev1.getAllPhrases().getWordList();
			phraseList2 = currentRev2.getAllPhrases().getWordList();
			count1 += phraseList1.size();
			count2 += phraseList2.size();
			countCorrect = 0;
			for (Phrase phrase1 : phraseList1) {
				found = false;
				for (Phrase phrase2 : phraseList2) {
					System.out.println(phrase1 + " " + phrase2);
					if (phrase1.equals(phrase2) || phrase1.getText().contains(phrase2.getText())
							|| phrase1.getText().contains(phrase2.getText())) {
						countCorrect++;
						done.add(phrase2);
						res.addHit();
						found = true;
						break;
					}
				}
				if (!found)
					res.addMiss();
			}

			res.setTotal(res.getTotal() + (phraseList1.size() - countCorrect) + (phraseList2.size() - countCorrect)
					+ countCorrect);

		}
		res.setCorrect(res.getTotal());
		System.out.println(count1 + " : " + count2);
		return res;
	}

	public ResultStats testingReviewRatersExactHit(Review[] rev1, Review[] rev2) {
		ResultStats res = new ResultStats();
		ArrayList<Phrase> done = new ArrayList<Phrase>();
		ArrayList<Phrase> phraseList1 = new ArrayList<Phrase>();
		ArrayList<Phrase> phraseList2 = new ArrayList<Phrase>();
		Review currentRev1;
		Review currentRev2;
		Boolean found = false;
		int countCorrect = 0;
		int count1 = 0;
		int count2 = 0;

		if (rev1.length != rev2.length) {
			return res;
		}
		for (int i = 0; i < rev1.length; i++) {
			currentRev1 = rev1[i];
			currentRev2 = rev2[i];
			currentRev1.initialize();
			currentRev2.initialize();
			phraseList1 = currentRev1.getAllPhrases().getWordList();
			phraseList2 = currentRev2.getAllPhrases().getWordList();
			count1 += phraseList1.size();
			count2 += phraseList2.size();
			countCorrect = 0;
			for (Phrase phrase1 : phraseList1) {
				found = false;
				for (Phrase phrase2 : phraseList2) {
					System.out.println(phrase1 + " " + phrase2);
					if (done.contains(phrase2)) {
						continue;
					}
					if (phrase1.equals(phrase2)) {
						countCorrect++;
						done.add(phrase2);
						res.addHit();
						found = true;
						break;
					}
				}
				if (!found)
					res.addMiss();
			}

			res.setTotal(res.getTotal() + (phraseList1.size() - countCorrect) + (phraseList2.size() - countCorrect)
					+ countCorrect);

		}
		res.setCorrect(res.getTotal());
		System.out.println(count1 + " : " + count2);
		return res;
	}

	public ResultStats getResultFromAdjective(ArrayList<Phrase> claims, int total) {
		ResultStats r = new ResultStats();
		r.setTotal(total);
		r.setCorrect(total);
		WordNode adjective;
		for (Phrase claim : claims) {
			adjective = claim.getSentence().getWordByType(claim, "ADJECTIVE");
			for (Phrase premise : claim.getTargets()) {
				if (premise.contains(adjective)) {
					r.addHit();
				}
				else {
					r.addMiss();
				}

			}
		}
		System.out.println("Recall:    " + r.getRecall());
		System.out.println("Precision: " + r.getPrecision());
		System.out.println("F1:        " + r.getF1Score());
		System.out.println("");
		return r;
	}

	public ArrayList<Relations> splitByProduct(ArrayList<Review> reviews) {
		HashMap<String, Relations> relations = new HashMap<String, Relations>();
		String key;
		Relations currentRelations;
		for (Review review : reviews) {
			key = review.getAsin();

			if (relations.containsKey(key)) {
				currentRelations = relations.get(key);
				currentRelations.addAll(review.getRelations());
				relations.put(key, currentRelations);
			}
			else {
				currentRelations = new Relations();
				currentRelations.addAll(review.getRelations());
				relations.put(key, currentRelations);
			}
		}
		ArrayList<Relations> rels = new ArrayList<Relations>();
		for (Relations r : relations.values()) {

			rels.add(r);
		}
		return rels;
	}

	public HashMap<String, ArrayList<Review>> splitReviewByProduct(ArrayList<Review> reviews) {
		HashMap<String, ArrayList<Review>> reviewMap = new HashMap<String, ArrayList<Review>>();
		String key;
		ArrayList<Review> currentReviews;
		for (Review review : reviews) {
			key = review.getAsin();

			if (reviewMap.containsKey(key)) {
				currentReviews = reviewMap.get(key);
				currentReviews.add(review);
				reviewMap.put(key, currentReviews);
			}
			else {
				currentReviews = new ArrayList<Review>();
				currentReviews.add(review);
				reviewMap.put(key, currentReviews);
			}
		}

		return reviewMap;
	}

	public static void printRelations(Relations relations) {
		Relations clearedRelation = relations.getClearedRelations();

		for (Relation rel : clearedRelation.all()) {
			rel.getLinkRec();
			System.out.println(
					rel.getAspectID() + "-" + rel.getStatementID() + ", " + rel.getPatternRoot().printSequence());
		}

	}
}