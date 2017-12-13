package data;

import java.util.ArrayList;
import java.util.HashMap;

import enums.CompType;

public class ProbabilityNode implements Comparable<ProbabilityNode> {
	private String								id			= "";
	private String								type;
	private ProbabilityNode						previousNode;

	private int									occurrence	= 0;
	private int									hit			= 0;
	private int									support		= 0;
	private int									maxSupport	= 0;
	private double								accuracy	= 0.0;
	private HashMap<String, ProbabilityNode>	successors;
	private ArrayList<PatternNode>				roots		= new ArrayList<PatternNode>();
	private ArrayList<PatternNode>				endNodes	= new ArrayList<PatternNode>();
	private PatternNode							tree;
	private double								sortVariable;
	private Boolean								isSet		= false;

	public ProbabilityNode() {
	}

	public ProbabilityNode(Relation rel) {
		rel.getLinkRec();
		this.tree = rel.getPatternRoot();
		this.id = rel.getReview();
		this.inc();
		this.setEndNodes();
	}

	public ProbabilityNode(Phrase p) {
		p.getPatternRec();
		this.tree = p.getPatternRoot();
		this.id = p.getReview();
		this.inc();
	}

	public ProbabilityNode(String type, int support, ProbabilityNode prev) {
		this.type = type;
		this.support = support;
		this.previousNode = prev;
		this.id = prev.getId();
	}

	public ProbabilityNode(String type, int support, String id) {
		this.type = type;
		this.support = support;
	}

	public double getSortVariable() {
		return sortVariable;
	}

	public Boolean getIsSet() {
		return isSet;
	}

	public void setSortVariable(CompType type) {
		switch (type) {
		case ACCURACY:
			isSet = true;
			this.sortVariable = this.accuracy;
			break;
		case SUPPORT:
			isSet = true;
			this.sortVariable = this.getNormalizedSupport();
			break;
		case BOTH:
			isSet = true;
			this.sortVariable = this.getNormalizedValues();
			break;
		default:
			break;
		}
	}

	public void addSuccessor(String type) {
		if (!this.successors.containsKey(type))
			this.successors.put(type, new ProbabilityNode(type, 0, this));
	}

	@Override
	public int compareTo(ProbabilityNode o) {
		if (!o.isSet && !this.isSet) {
			System.out.println("twjgkjkjh");
		}

		if (o.getSortVariable() > this.sortVariable)
			return 1;
		else if (o.getSortVariable() < this.sortVariable)
			return -1;
		else
			return 0;
	}

	@Override
	public boolean equals(Object o) {
		ProbabilityNode p = (ProbabilityNode) o;
		return this.tree.equals(p.getTree());
	}

	public double getAccuracy() {
		return this.accuracy;
	}

	public String getId() {
		return id;
	}

	public ProbabilityNode getPreviousNode() {
		return previousNode;
	}

	public ArrayList<PatternNode> getRoots() {
		return roots;
	}

	public ProbabilityNode getSuccessor(String type) {
		if (this.successors.containsKey(type)) {
			return this.successors.get(type);
		}
		else {
			ProbabilityNode returnNode = new ProbabilityNode(type, 0, this);
			this.successors.put(type, returnNode);
			return returnNode;
		}
	}

	public ArrayList<ProbabilityNode> getSuccessors() {
		return (ArrayList<ProbabilityNode>) this.successors.values();
	}

	public int getTotalSupport() {
		return support;
	}

	public PatternNode getTree() {
		return tree;
	}

	public void inc() {
		this.support += 1;
	}

	public void incSuccessor(String type) {
		if (this.successors.containsKey(type))
			this.successors.get(type).inc();
		else {
			this.successors.put(type, new ProbabilityNode(type, 1, this));
		}
	}

	public String returnType() {
		return this.type;
	}

	public void setAccuracyforPatterns(Relations rels) {
		int hit = 0;
		int all = 0;
		ArrayList<WordNode> results = new ArrayList<WordNode>();
		for (Relation rel : rels.all()) {
			for (WordNode node : rel.getAspect().getExitNodes()) {
				results = node.recursiveGetNextNode(this.getTree());
				if (!results.isEmpty()) {
					all++;
					if (results.containsAll(rel.getStatement().getExitNodes())) {
						hit++;
					}
				}
			}
		}
		this.occurrence = all;
		this.hit = hit;
		if (all == 0) {
			this.accuracy = 0;
		}
		else {
			this.accuracy = (double) hit / (double) all;
		}
	}

	public void setAccuracyforPatterns(ArrayList<Phrase> phrases) {
		int hit = 0;
		int all = 0;
		ArrayList<WordNode> results = new ArrayList<WordNode>();
		for (Phrase p : phrases) {
			for (WordNode node : p.getExitNodes()) {
				results = node.getAllNodes((PhrasePatternNode) this.getTree());
				if (!results.isEmpty()) {
					all++;
					if (results.containsAll(p.getWords())) {
						hit++;
					}
				}
			}
		}
		this.occurrence = all;
		this.hit = hit;
		if (all == 0) {
			this.accuracy = 0;
		}
		else {
			this.accuracy = (double) hit / (double) all;
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getMaxSupport() {
		return maxSupport;
	}

	public void setMaxSupport(int maxSupport) {
		this.maxSupport = maxSupport;
	}

	public double getNormalizedValues() {
		return (this.accuracy + this.getNormalizedSupport()) / 2;
	}

	public double getNormalizedSupport() {
		return (double) this.support / (double) this.maxSupport;
	}

	public void setEndNodes() {
		this.endNodes = this.tree.getEndNodesRec();
	}

	public ArrayList<PatternNode> getEndNodes() {
		return this.endNodes;
	}

	public boolean hasMultipleEndNodes() {
		return this.endNodes.size() > 1;
	}

	public int getOccurrences() {
		return this.occurrence;
	}

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

}
