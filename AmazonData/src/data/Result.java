package data;

import java.util.ArrayList;

public class Result implements Comparable<Result> {
	private Phrase				aspect;
	private Phrase				CorrectStatement;

	private PhrasePatternNode	phrasePattern;
	private RelationPatternNode	relationPattern;

	private ProbabilityNode		phraseProbabilityNode;
	private ProbabilityNode		relationProbabilityNode;

	private ArrayList<WordNode>	phraseNodes				= new ArrayList<WordNode>();
	private ArrayList<WordNode>	targetExitNodes;
	private Boolean				correct					= false;
	private Boolean				correctTargetExitNode	= false;
	private double				sortingValue			= 0.0;

	public Result() {

	}

	public void setSortingValue(double sortingValue) {
		this.sortingValue = sortingValue;
	}

	public Phrase getCorrectStatement() {
		return CorrectStatement;
	}

	public void setCorrectStatement(Phrase correctStatement) {
		CorrectStatement = correctStatement;
	}

	public Boolean getCorrectTargetExitNode() {
		return correctTargetExitNode;
	}

	public void setCorrectTargetExitNode(Boolean correctTargetExitNode) {
		this.correctTargetExitNode = correctTargetExitNode;
	}

	public Phrase getAspect() {
		return aspect;
	}

	public void setAspect(Phrase aspect) {
		this.aspect = aspect;
	}

	public Boolean getCorrect() {
		return correct;
	}

	public void setCorrect(Boolean correct) {
		this.correct = correct;
	}

	public ArrayList<WordNode> getPhraseNodes() {
		return phraseNodes;
	}

	public void setPhraseNodes(ArrayList<WordNode> phraseNodes) {
		this.phraseNodes = phraseNodes;
	}

	public PhrasePatternNode getPhrasePattern() {
		return phrasePattern;
	}

	public void setPhrasePattern(PhrasePatternNode phrasePattern) {
		this.phrasePattern = phrasePattern;
	}

	public RelationPatternNode getRelationPattern() {
		return relationPattern;
	}

	public void setRelationPattern(RelationPatternNode relationPattern) {
		this.relationPattern = relationPattern;
	}

	public ProbabilityNode getPhraseProbabilityNode() {
		return phraseProbabilityNode;
	}

	public void setPhraseProbabilityNode(ProbabilityNode phraseProbabilityNode) {
		this.phraseProbabilityNode = phraseProbabilityNode;
	}

	public ProbabilityNode getRelationProbabilityNode() {
		return relationProbabilityNode;
	}

	public void setRelationProbabilityNode(ProbabilityNode relationProbabilityNode) {
		this.relationProbabilityNode = relationProbabilityNode;
	}

	public double getSortingValue() {
		if (this.phraseProbabilityNode == null && this.relationProbabilityNode == null) {
			return 0;
		}
		else if (this.phraseProbabilityNode == null) {
			return this.relationProbabilityNode.getSortVariable();
		}
		else if (this.relationProbabilityNode == null) {
			return this.phraseProbabilityNode.getSortVariable();
		}
		else {
			return this.phraseProbabilityNode.getSortVariable() * this.relationProbabilityNode.getSortVariable();
		}
	}

	public double getAccuracy() {
		if (this.phraseProbabilityNode == null && this.relationProbabilityNode == null) {
			return 0;
		}
		else if (this.phraseProbabilityNode == null) {
			return this.relationProbabilityNode.getAccuracy();
		}
		else if (this.relationProbabilityNode == null) {
			return this.phraseProbabilityNode.getAccuracy();
		}
		else {
			return this.phraseProbabilityNode.getAccuracy() * this.relationProbabilityNode.getAccuracy();
		}
	}

	@Override
	public int compareTo(Result o) {
		if (this.getSortingValue() > o.getSortingValue())
			return -1;
		else if (this.getSortingValue() < o.getSortingValue())
			return 1;
		else
			return 0;
	}

	public Boolean checkResult(int noice) {
		this.correct = false;
		for (Phrase statement : this.aspect.getTargets()) {
			if ((this.phraseNodes.containsAll(statement.getWords()))
					&& Math.abs(statement.getWords().size() - phraseNodes.size()) <= noice) {
				this.CorrectStatement = statement;
				this.correct = true;
			}

		}
		return this.correct;
	}

	public String toString() {
		return this.aspect + " > " + this.CorrectStatement + " (" + this.phraseNodes + ") " + this.getAccuracy();

	}

	public ArrayList<WordNode> getTargetExitNodes() {
		return targetExitNodes;
	}

	public void setTargetExitNodes(ArrayList<WordNode> targetExitNodes) {
		this.targetExitNodes = targetExitNodes;
	}

	public Boolean checkTargetExitNodes(int noice) {
		this.correctTargetExitNode = false;
		for (Phrase statement : this.aspect.getTargets()) {
			if ((statement.getExitNodes().containsAll(this.targetExitNodes)
					|| this.targetExitNodes.containsAll(statement.getExitNodes()))
					&& Math.abs(statement.getExitNodes().size() - this.targetExitNodes.size()) <= noice) {
				this.correctTargetExitNode = true;
				this.CorrectStatement = statement;
				this.correct = true;
			}

		}
		return this.correctTargetExitNode;
	}

	public Boolean checkTargetExitNodesWithSurounding(int noice) {
		this.correctTargetExitNode = false;
		for (Phrase statement : this.aspect.getTargets()) {
			if ((statement.getExitNodes().containsAll(this.targetExitNodes)
					|| this.targetExitNodes.containsAll(statement.getExitNodes()))
					&& Math.abs(statement.getExitNodes().size() - this.targetExitNodes.size()) <= noice) {
				this.correctTargetExitNode = true;
			}

		}
		return this.correctTargetExitNode;
	}

	public Boolean checkPhraseVsGold(int noice) {
		this.correct = false;

		if ((this.getPhraseNodes().containsAll(this.CorrectStatement.getWords()))
				&& Math.abs(this.getPhraseNodes().size() - this.CorrectStatement.getWords().size()) <= noice) {
			this.correct = true;
		}

		return this.correct;
	}

	public Boolean compareExtitNodesWith(Result other) {
		return this.targetExitNodes.equals(other.targetExitNodes) && this.aspect == other.aspect;
	}

	public Boolean hasTargetNodes() {
		return this.getTargetExitNodes() != null && !this.getTargetExitNodes().isEmpty();
	}

	public boolean hasPhraseNodes() {
		return this.getPhraseNodes() != null && !this.getPhraseNodes().isEmpty();
	}

	public void setPhrasesSRL() {
		ArrayList<WordNode> subTreeNodes = new ArrayList<WordNode>();
		for (WordNode exitNode : this.getTargetExitNodes()) {
			subTreeNodes.addAll(exitNode.getSubtreeNodes());
		}
		this.setPhraseNodes(subTreeNodes);
	}

	public String getRelationAsText() {
		if (this.aspect != null) {
			if (this.CorrectStatement != null) {
				return this.aspect.getId() + ":" + this.CorrectStatement.getId();
			}
			else {
				return this.aspect.getId() + ":";
			}
		}
		return "";
	}
}
