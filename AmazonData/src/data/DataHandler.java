package data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.JsonReviewReader;

public class DataHandler {

	private Gson					gson		= null;

	private Review[]				reviews		= new Review[0];
	private HashMap<String, Review>	reviewMap	= new HashMap<String, Review>();
	private JsonReviewReader		rr			= null;
	//public ReviewWriter				rw			= null;

	public DataHandler() {
		GsonBuilder gBuilder = new GsonBuilder();
		gBuilder.excludeFieldsWithoutExposeAnnotation();
		gBuilder.setPrettyPrinting();
		this.gson = gBuilder.create();
		this.rr = new JsonReviewReader(this.gson);
		//this.rw = new ReviewWriter(this.gson);
	}

	public void addRelation(String reviewID, Relation r) {
		this.reviewMap.get(reviewID).addRelation(r);
	}

	public void addWord(String reviewID, Phrase w) {
		this.reviewMap.get(reviewID).addWord(w);
	}

	public ArrayList<Phrase> getAspects(String reviewID) {
		Review temp = this.getReview(reviewID);
		if (temp != null) {
			return temp.getClaims();
		}
		else {
			return new ArrayList<Phrase>();
		}
	}

	public ArrayList<Relation> getRelations(String reviewID) {
		Review temp = this.getReview(reviewID);
		if (temp != null) {
			return temp.getRelations();
		}
		else {
			return new ArrayList<Relation>();
		}
	}

	public Review getReview(String id) {
		return reviewMap.get(id);
	}

	public Review[] getReviews() {
		return this.reviews;
	}

	public ArrayList<Phrase> getSubjects(String reviewID) {
		Review temp = this.getReview(reviewID);
		if (temp != null) {
			return temp.getPremises();
		}
		else {
			return new ArrayList<Phrase>();
		}
	}

	public void loadData(File file) {
		this.reviews = rr.readReviews(file);
		for (Review review : reviews) {
			review.initialize();
			reviewMap.put(review.getReviewID(), review);
		}
	}

	public void removePhrase(String reviewID, Phrase p) {
		Review rev = this.reviewMap.get(reviewID);
		rev.removeWord(p);
		rev.removeRelationByPhrase(p);

	}

	public void removeRelation(String reviewID, Relation r) {
		this.reviewMap.get(reviewID).removeRelation(r);
	}

	public void reset() {
		this.reviews = new Review[0];
		this.reviewMap = new HashMap<String, Review>();
	}
}
