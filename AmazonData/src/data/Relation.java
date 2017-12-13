package data;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

public class Relation {
	@Expose
	private String							aspectID;
	@Expose
	private String							statementID;
	@Expose
	private String							review;

	private Phrase							aspect;
	private Phrase							statement;

	private ArrayList<RelationPatternNode>	patterns	= new ArrayList<RelationPatternNode>();

	private boolean							found		= false;

	public Relation(Phrase aspect, Phrase statement, String review) {
		this.aspect = aspect;
		this.statement = statement;
		this.aspectID = aspect.getId();
		this.statementID = statement.getId();
		this.review = review;
	}

	public Phrase getAspect() {
		return aspect;
	}

	public String getAspectID() {
		return aspectID;
	}

	public String getReview() {
		return review;
	}

	public Phrase getStatement() {
		return statement;
	}

	public String getStatementID() {
		return statementID;
	}

	public void initialize(Phrases phrases) {
		this.aspect = phrases.getPhrase(this.aspectID);
		this.statement = phrases.getPhrase(this.statementID);
		this.patterns = new ArrayList<RelationPatternNode>();
		this.aspect.addTarget(statement);
	}

	public void setPhrases(Phrase aspect, Phrase statement) {
		this.aspect = aspect;
		this.statement = statement;
	}

	@Override
	public String toString() {
		return this.aspect + " <> " + this.statement;
	}

	public void getLinkRec() {
		RelationPatternNode t = null;
		this.statement.setTagOnExitNodes();
		this.aspect.setTagOnExitNodes();

		for (WordNode word : this.aspect.getExitNodes()) {
			word.setTag(word.searchPathByTag(this.statement.getId(), word));
			t = new RelationPatternNode(word);
			this.patterns.add(t);
			t.initializePatternTree(word, word, this.statement.getId());
		}
	}

	public RelationPatternNode getPatternRoot() {
		if (this.patterns.size() > 0)
			return this.patterns.get(0);
		else
			return null;
	}

	public boolean isFound() {
		return found;
	}

	public void setFound(boolean found) {
		this.found = found;
	}

}
