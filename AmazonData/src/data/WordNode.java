package data;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.annotations.Expose;

import utils.WordTypes;

public class WordNode implements Comparable<WordNode> {
	@Expose
	private Integer					start;
	@Expose
	private Integer					end;
	@Expose
	private String					review;
	@Expose
	private String					text;
	@Expose
	private int						id				= 0;
	@Expose
	private String					type;
	@Expose
	private int						previousID;
	@Expose
	private String					previousLink;
	@Expose
	private boolean					isPredicate;

	@Expose
	private SRLSemanticPredicate	predicate		= null;

	private WordNode				predecessor		= null;

	private ArrayList<WordNode>		successors		= new ArrayList<WordNode>();
	private ArrayList<WordNode>		argumentRoots	= new ArrayList<WordNode>();
	private String					tag				= "";
	private ReviewSentence			sentence		= null;

	private SRLSemanticArgument		srlArgument		= null;

	public WordNode() {

	}

	public WordNode(int start, int end, String review, int id, String text, String type, int previousID,
			String previousLink, boolean isPredicate) {
		this.start = start;
		this.end = end;
		this.review = review;
		this.id = id;
		this.text = text;
		this.type = type;
		this.previousID = previousID;
		this.previousLink = previousLink;
		this.isPredicate = isPredicate;
	}

	public WordNode(String type) {
		this.type = type;
	}

	public void addArgumentRoots(WordNode argumentRoot) {
		this.argumentRoots.add(argumentRoot);
	}

	public void addSucessors(WordNode word) {
		this.successors.add(word);
	}

	public Boolean equals(WordNode other) {
		return this.text.equals(other.getText());
	}

	public ArrayList<WordNode> getArgumentRoots() {
		return argumentRoots;
	}

	public ArrayList<SRLSemanticArgument> getArguments() {
		return this.predicate.getArguments();
	}

	public int getEnd() {
		return end;
	}

	public int getId() {
		return id;
	}

	public WordNode getPredecessor() {
		return this.predecessor;
	}

	public int getPreviousID() {
		return previousID;
	}

	public String getRawType() {
		return this.type;
	}

	public String getReview() {
		return review;
	}

	public int getStart() {
		return this.start;
	}

	public ArrayList<WordNode> getSuccessors() {
		return this.successors;
	}

	public String getText() {
		return text;
	}

	public String getType() {
		// return this.type;
		return this.getType(this.type);
	}

	public boolean isPredicate() {
		return isPredicate;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPredcessor(WordNode word) {
		this.predecessor = word;
	}

	public void setPredicate(SRLSemanticPredicate predicate) {
		this.predicate = predicate;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return this.text + " (" + this.start + "," + this.end + ")";
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String searchPathByTag(String searched, WordNode pathNode) {
		String result = "";
		if (this.tag.equals(searched)) {
			return this.tag;
		}
		else {
			this.tag = "";
			if (this.predecessor != pathNode && this.predecessor != this) {
				this.tag = this.predecessor.searchPathByTag(searched, this);
			}
			if (!this.successors.isEmpty()) {
				for (WordNode wordNode : successors) {
					if (wordNode != pathNode && wordNode != this) {
						result = wordNode.searchPathByTag(searched, this);
					}
					if (result != "") {
						this.tag = result;
					}
				}
			}
			return this.tag;
		}
	}

	public ArrayList<WordNode> recursiveGetNextNode(PatternNode tn) {
		ArrayList<WordNode> result = new ArrayList<WordNode>();
		PatternNode prev = tn.getPrevious();
		if (this.getType().equals(tn.getType())) {
			if (prev != null && prev != tn) {
				return this.predecessor.recursiveGetNextNode(prev);
			}
			else if (!tn.getSuccessors().isEmpty()) {
				for (WordNode wordNode : this.getSuccessors()) {

					for (PatternNode succTN : tn.getSuccessors()) {
						result.addAll(wordNode.recursiveGetNextNode(succTN));
					}
				}
			}
			else {
				result.add(this);
			}
		}
		return result;
	}

	public void initialize(ReviewSentence reviewSentence) {
		this.sentence = reviewSentence;
		if (this.predicate != null) {
			predicate.initialize(reviewSentence);
		}
	}

	public void setSRLArgument(SRLSemanticArgument argument) {
		this.srlArgument = argument;
	}

	public SRLSemanticArgument getSRLArgument() {
		return this.srlArgument;
	}

	public ReviewSentence getSentence() {
		return this.sentence;
	}

	public WordNode getOtherArguments(WordNode w) {
		WordNode returnWord = null;
		if (this.isPredicate) {
			if (w.getSRLArgument() == null) {
				System.out.println("adhlsfgdrjhkä");
			}
			returnWord = this.predicate.getOtherArgumentWord(w.getSRLArgument());
		}
		return returnWord;
	}

	public String getType(String type) {
		//return this.type;

		if (type.startsWith("NN")) {
			return WordTypes.NOUN.toString();
		}
		if (type.startsWith("VB")) {
			return WordTypes.VERB.toString();
		}
		else if (type.startsWith("JJ")) {
			return WordTypes.ADJECTIVE.toString();
		}
		else if (type.startsWith("RB")) {
			return WordTypes.ADVERB.toString();
		}
		else if (type.equals("IN")) {
			return WordTypes.PREPOSITION.toString();
		}
		else if (type.equals("DT")) {
			return WordTypes.DETERMINER.toString();
		}
		else if (type.equals("CD")) {
			return WordTypes.NUMERALS.toString();
		}
		else if (type.equals("KOKOM")) {
			return WordTypes.PARTICLES.toString();
		}
		else if (type.equals("CC")) {
			return WordTypes.CONJUNCTION.toString();
		}
		else if (type.equals("PDT")) {
			return WordTypes.PREDETERMINER.toString();
		}
		else if (type.startsWith("PP")) {
			return WordTypes.PRONOUN.toString();
		}
		else if (type.startsWith("PR")) {
			return WordTypes.PRONOUN.toString();
		}
		else if (type.equals("PROP")) {
			return WordTypes.OTHER.toString();
		}
		else if (type.equals("XY")) {
			return WordTypes.OTHER.toString();
		}
		else if (type.length() == 1) {
			return type;
		}
		else {
			return this.type;
		}
	}

	public ArrayList<WordNode> getNextNode(RelationPatternNode tn) {
		ArrayList<WordNode> result = new ArrayList<WordNode>();
		if (this.getType().equals(tn.getType())) {
			RelationPatternNode prev = (RelationPatternNode) tn.getPrevious();
			if (prev != null) {
				if (prev != tn && this.predecessor != this) {
					return this.predecessor.getNextNode(prev);
				}
			}
			else if (!tn.getSuccessors().isEmpty()) {
				for (WordNode wordNode : this.getSuccessors()) {
					if (this != wordNode) {
						for (PatternNode succTN : tn.getSuccessors()) {
							result.addAll(wordNode.getNextNode((RelationPatternNode) succTN));
						}
					}
				}
			}
			else {
				result.add(this);
			}
		}

		return result;
	}

	public ArrayList<WordNode> getAllNodes(PhrasePatternNode tn) {
		ArrayList<WordNode> result = new ArrayList<WordNode>();
		ArrayList<WordNode> returnValue = new ArrayList<WordNode>();
		ArrayList<WordNode> doneSuccessors = new ArrayList<WordNode>();
		ArrayList<PatternNode> successors = new ArrayList<PatternNode>();
		PhrasePatternNode prev = (PhrasePatternNode) tn.getPrevious();
		if (this.getType().equals(tn.getType())) {
			if (prev != null && prev != tn) {
				result.add(this);
				returnValue = this.predecessor.getAllNodes(prev);
				if (returnValue.isEmpty()) {
					return new ArrayList<WordNode>();
				}
				else {
					result.addAll(returnValue);
				}

				return result;
			}
			else if (!tn.getSuccessors().isEmpty()) {
				result.add(this);

				successors = tn.getSuccessors();
				Collections.sort(successors);
				for (PatternNode succTN : successors) {
					for (WordNode wordNode : this.getSuccessors()) {
						if (!doneSuccessors.contains(wordNode)) {
							returnValue = wordNode.getAllNodes((PhrasePatternNode) succTN);
							if (!returnValue.isEmpty()) {
								result.addAll(returnValue);
								doneSuccessors.add(wordNode);
								continue;
							}
						}
					}
				}
				if (doneSuccessors.size() != successors.size()) {
					return new ArrayList<WordNode>();
				}
			}
			else {
				result.add(this);
			}
		}

		return result;
	}

	public Boolean checkForPredecessor(WordNode target) {
		if (this == target) {
			return true;
		}
		else {
			if (this.predecessor == this) {
				return false;
			}
			else {
				return this.predecessor.checkForPredecessor(target);
			}
		}
	}

	public ArrayList<WordNode> getSubtreeNodes() {
		ArrayList<WordNode> returnList = new ArrayList<WordNode>();
		returnList.add(this);
		for (WordNode wordNode : this.successors) {
			returnList.addAll(wordNode.getSubtreeNodes());
		}
		return returnList;
	}

	@Override
	public int compareTo(WordNode o) {
		if (this.id > o.id)
			return 1;
		else if (this.id < o.id)
			return -1;
		else
			return 0;
	}
}
