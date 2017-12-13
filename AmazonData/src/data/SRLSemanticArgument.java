package data;

import com.google.gson.annotations.Expose;

public class SRLSemanticArgument {
	@Expose
	private int						id;
	@Expose
	private String					type;
	private WordNode				word;
	private SRLSemanticPredicate	predicate;

	public SRLSemanticArgument(int id, String type) {
		this.id = id;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void initialize(ReviewSentence reviewSentence) {
		this.word = reviewSentence.getWord(this.id);
		this.word.setSRLArgument(this);
	}

	public SRLSemanticPredicate getPredicate() {
		return predicate;
	}

	public void setPredicate(SRLSemanticPredicate predicate) {
		this.predicate = predicate;
	}

	public SRLSemanticArgument getOtherArgument() {
		return this.predicate.getOtherArgument(this);

	}

	public WordNode getWord() {
		return this.word;
	}

}
