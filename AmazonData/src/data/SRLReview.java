package data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gson.annotations.Expose;

public class SRLReview {
	@Expose
	private String						fileName;
	@Expose
	private String						reviewerID;
	@Expose
	private String						asin;
	@Expose
	private String						reviewerName;
	@Expose
	private List<Integer>				helpful		= null;
	@Expose
	private String						reviewText;
	@Expose
	private Double						overall;
	@Expose
	private String						summary;
	@Expose
	private Integer						unixReviewTime;
	@Expose
	private String						reviewTime;
	@Expose
	private ArrayList<ReviewSentence>	sentences	= new ArrayList<ReviewSentence>();

	public SRLReview() {
	}

	public SRLReview(String id) {
		this.fileName = id;
		String[] split = this.fileName.split("_");
		this.reviewerID = split[0];
		this.asin = split[1];
		this.asin = this.asin.substring(0, this.asin.length() - 4);
	}

	public void addSentence(ReviewSentence sentence) {
		this.sentences.add(sentence);
	}

	public String getASIN() {
		return this.asin;
	}

	public String getID() {
		return this.asin + "_" + this.reviewerID;
	}

	public String getReviewerID() {
		return this.reviewerID;
	}

	public String getReviewText() {
		return this.reviewText;
	}

	public ArrayList<ReviewSentence> getSentences() {
		return sentences;
	}

	public void initialize() {
		for (ReviewSentence sentence : sentences) {
			sentence.initialize();
		}
	}

	public void setPhrases(Phrases phrases, Relations relations) {
		Iterator<ReviewSentence> itrSentence = this.sentences.iterator();
		Iterator<Phrase> itrClaims = phrases.getClaims().iterator();
		Iterator<Phrase> itrPremises = phrases.getPremises().iterator();
		Boolean cFinished = false;
		Boolean pFinished = false;

		ReviewSentence sentence = null;
		Phrase claim = null;
		Phrase premise = null;
		while (itrSentence.hasNext()) {
			sentence = itrSentence.next();
			if (cFinished) {
				cFinished = false;
				sentence.addPhrase(claim);
			}
			if (pFinished) {
				pFinished = false;
				sentence.addPhrase(premise);
			}
			while (itrClaims.hasNext() && cFinished) {
				claim = itrClaims.next();
				if (claim.getEnd() > sentence.getEnd()) {
					cFinished = true;
				}
				else {
					sentence.addPhrase(claim);
					sentence.addRelations(relations.getByAspect(claim));
				}
			}

			while (itrPremises.hasNext()) {
				premise = itrClaims.next();
				if (premise.getEnd() > sentence.getEnd()) {
					pFinished = true;
				}
				else {
					sentence.addPhrase(premise);
				}
			}
		}
	}

	@Override
	public String toString() {
		return reviewerID + " - " + reviewText;
	}

}
