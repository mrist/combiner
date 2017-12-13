package data;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review {
	@SerializedName("reviewerID")
	@Expose
	private String						reviewerID;
	@SerializedName("asin")
	@Expose
	private String						asin;
	@SerializedName("reviewerName")
	@Expose
	private String						reviewerName;
	@SerializedName("helpful")
	@Expose
	private List<Integer>				helpful		= null;
	@SerializedName("reviewText")
	@Expose
	private String						reviewText;
	@SerializedName("overall")
	@Expose
	private Double						overall;
	@SerializedName("summary")
	@Expose
	private String						summary;
	@SerializedName("unixReviewTime")
	@Expose
	private Integer						unixReviewTime;
	@SerializedName("reviewTime")
	@Expose
	private String						reviewTime;

	@SerializedName("words")
	@Expose
	private Phrases						phrases		= new Phrases();
	@SerializedName("relations")
	@Expose
	private Relations					relations	= new Relations();
	@Expose
	private ArrayList<ReviewSentence>	sentences	= new ArrayList<ReviewSentence>();

	public void addRelation(Relation r) {
		this.relations.add(r);
	}

	public void addWord(Phrase w) {
		w.setId(this.reviewerID);
		this.phrases.add(w);
	}

	public String getAsin() {
		return asin;
	}

	public ArrayList<Phrase> getClaims() {
		return this.phrases.getClaims();
	}

	public List<Integer> getHelpful() {
		return helpful;
	}

	public String getID() {
		return this.asin + "_" + this.reviewerID;
	}

	public Double getOverall() {
		return overall;
	}

	public Phrase getPhrase(String id) {
		Phrase ret = null;
		for (Phrase p : this.phrases.getWordList()) {
			if (p.getId().equals(id)) {
				ret = p;
				break;
			}
		}
		return ret;
	}

	public ArrayList<Phrase> getPremises() {
		return this.phrases.getPremises();
	}

	public ArrayList<Relation> getRelationByPhrase(Phrase phrase) {
		ArrayList<Relation> rev = new ArrayList<Relation>();
		ArrayList<Relation> tempRev = new ArrayList<Relation>();
		tempRev = this.relations.getByAspect(phrase);
		if (tempRev != null) {
			rev.addAll(tempRev);
		}
		tempRev = this.relations.getByStatement(phrase);
		if (tempRev != null) {
			rev.addAll(tempRev);
		}
		return rev;
	}

	public ArrayList<Relation> getRelations() {
		return this.relations.all();
	}

	public String getReviewerID() {
		return reviewerID;
	}

	public String getReviewerName() {
		return reviewerName;
	}

	public String getReviewID() {
		return this.reviewerID + "";
	}

	public String getReviewText() {
		return reviewText;
	}

	public String getReviewTime() {
		return reviewTime;
	}

	public ArrayList<ReviewSentence> getSentences() {
		return sentences;
	}

	public String getSummary() {
		return summary;
	}

	public Integer getUnixReviewTime() {
		return unixReviewTime;
	}

	public void initialize() {
		int start = 0;
		int end = 0;
		this.phrases.initialize();
		for (ReviewSentence sentence : sentences) {
			sentence.initialize();
			start = sentence.getStart();
			end = sentence.getEnd();
			for (Phrase phrase : this.phrases.getClaims()) {
				if (start <= phrase.getEnd() && phrase.getStart() <= end) {
					phrase.setWords(sentence);
					phrase.initialize();
				}
			}
			for (Phrase phrase : this.phrases.getPremises()) {
				if (start <= phrase.getEnd() && phrase.getStart() <= end) {
					phrase.setWords(sentence);
					phrase.initialize();
				}
			}
		}
		this.relations.initialize(this.phrases);

		// TODO Auto-generated method stub

	}

	public void removeRelation(Relation r) {
		this.relations.remove(r);
	}

	public void removeRelationByPhrase(Phrase p) {
		for (Relation rel : this.getRelationByPhrase(p)) {
			this.removeRelation(rel);
		}
	}

	public void removeWord(Phrase w) {
		this.phrases.remove(w);
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public void setHelpful(List<Integer> helpful) {
		this.helpful = helpful;
	}

	public void setOverall(Double overall) {
		this.overall = overall;
	}

	public void setReviewerID(String reviewerID) {
		this.reviewerID = reviewerID;
	}

	public void setReviewerName(String reviewerName) {
		this.reviewerName = reviewerName;
	}

	public void setReviewText(String reviewText) {
		this.reviewText = reviewText;
	}

	public void setReviewTime(String reviewTime) {
		this.reviewTime = reviewTime;
	}

	public void setSentences(ArrayList<ReviewSentence> sentences) {
		this.sentences = sentences;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public void setUnixReviewTime(Integer unixReviewTime) {
		this.unixReviewTime = unixReviewTime;
	}

	@Override
	public String toString() {
		return this.reviewerID;
	}

	public Phrases getAllPhrases() {
		return this.phrases;
	}
}