package data;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

public class Phrase implements Comparable<Phrase> {
	@Expose
	private Integer				start;
	@Expose
	private Integer				end;
	@Expose
	private String				review;
	@Expose
	private String				text;
	@Expose
	private String				id;
	@Expose
	private String				type;

	private ArrayList<WordNode>	words		= new ArrayList<WordNode>();
	private ArrayList<WordNode>	exitNodes	= new ArrayList<WordNode>();
	private ArrayList<Phrase>	targets		= new ArrayList<Phrase>();
	private PhrasePatternNode	pattern;
	private ProbabilityNode		prob;
	private boolean				found		= false;

	public Phrase() {

	}

	public Phrase(int start, int end, String review, String text, String type) {
		this.start = start;
		this.end = end;
		this.review = review;
		this.text = text;
		this.type = type;
	}

	public Phrase(String type) {
		this.type = type;
	}

	@Override
	public int compareTo(Phrase o) {
		return o.getStart() - this.getStart();
	}

	public Boolean equals(Phrase other) {
		return this.text.equals(other.getText());
	}

	public int getEnd() {
		return end;
	}

	public String getId() {
		return id;
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

	public String getText() {
		return text;
	}

	public String getType() {
		return this.type;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTypeId(String type, int count) {
		this.id += "-" + type + "-" + count;
	}

	@Override
	public String toString() {
		return this.text + " (" + this.start + "," + this.end + ")";
	}

	public void setWords(ReviewSentence sentence) {
		for (WordNode wordNode : sentence.getWords()) {
			if (this.start < wordNode.getEnd() && wordNode.getStart() < this.end) {
				this.words.add(wordNode);
			}
		}
	}

	public void initialize() {
		this.exitNodes = new ArrayList<WordNode>();
		this.setExitNode();
	}

	private void setExitNode() {

		WordNode prev;

		for (WordNode current : this.words) {
			prev = current.getPredecessor();
			if (current == prev) {
				this.exitNodes.add(current);
			}
			if (!this.words.contains(prev))
				if (!this.exitNodes.contains(prev))
					this.exitNodes.add(current);
		}
	}

	public ArrayList<WordNode> getExitNodes() {
		return this.exitNodes;
	}

	public void setTagOnExitNodes() {
		for (WordNode word : this.exitNodes) {
			word.setTag(this.getId());
		}
	}

	public ReviewSentence getSentence() {
		return this.words.get(0).getSentence();
	}

	public boolean contains(WordNode w) {
		return this.words.contains(w);
	}

	public void addTarget(Phrase target) {
		if (!targets.contains(target)) {
			this.targets.add(target);
		}
	}

	public ArrayList<Phrase> getTargets() {
		return this.targets;
	}

	public ArrayList<WordNode> getPatternTarget(RelationPatternNode tn) {
		ArrayList<WordNode> result = new ArrayList<WordNode>();
		if (this.getExitNodes().size() > 1) {
			return result;
		}
		for (WordNode node : this.getExitNodes()) {
			result.addAll(node.getNextNode(tn));
		}
		return result;
	}

	public Result getPatternTargetResult(RelationPatternNode tn) {
		Result result = new Result();
		result.setAspect(this);
		result.setRelationPattern(tn);
		if (this.getExitNodes().size() > 1) {
			return result;
		}
		for (WordNode node : this.getExitNodes()) {
			result.setTargetExitNodes(node.getNextNode(tn));
		}
		return result;
	}

	public ArrayList<WordNode> getWords() {
		return this.words;
	}

	public void getPatternRec() {
		PhrasePatternNode t = null;

		for (WordNode word : this.getExitNodes()) {
			t = new PhrasePatternNode(word);
			this.pattern = t;
			t.initializeWordTree(word, word, this);
		}
	}

	public PhrasePatternNode getPatternRoot() {
		return this.pattern;
	}

	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}

	public ProbabilityNode getProbabilityNode() {
		return prob;
	}

	public void setProbabilityNode(ProbabilityNode prob) {
		this.prob = prob;
	}

	public void addWord(WordNode w) {
		this.words.add(w);
	}

}
