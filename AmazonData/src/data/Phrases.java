package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Phrases {

	private ArrayList<Phrase>		wordList	= new ArrayList<Phrase>();
	private HashMap<String, Phrase>	wordMap		= new HashMap<String, Phrase>();

	@SerializedName("claims")
	@Expose
	private ArrayList<Phrase>		claims		= new ArrayList<Phrase>();

	@SerializedName("premises")
	@Expose
	private ArrayList<Phrase>		premises	= new ArrayList<Phrase>();

	public Phrases() {
	}

	public void add(Phrase p) {
		this.wordList.add(p);
		if (p.getType().equals("claim")) {
			p.setTypeId("claim", this.claims.size());
			if (!this.claims.contains(p))
				this.claims.add(p);
		}
		else if (p.getType().equals("premise")) {
			p.setTypeId("premise", this.premises.size());
			if (!this.premises.contains(p))
				this.premises.add(p);
		}
	}

	public void appendWords(Phrases other) {
		this.wordList.addAll(other.getWordList());
		for (Phrase claim : other.getClaims()) {
			if (!this.claims.contains(claim))
				this.claims.add(claim);
		}
		for (Phrase premise : other.getPremises()) {
			if (!this.premises.contains(premise))
				this.premises.add(premise);
		}
	}

	public ArrayList<Phrase> getClaims() {
		return claims;
	}

	public Phrase getPhrase(String id) {
		return this.wordMap.get(id);
	}

	public ArrayList<Phrase> getPremises() {
		return premises;
	}

	public ArrayList<Phrase> getWordList() {
		return this.wordList;
	}

	public void initialize() {
		Collections.sort(this.claims);
		Collections.sort(this.premises);
		for (Phrase phrase : this.claims) {
			this.wordList.add(phrase);
			this.wordMap.put(phrase.getId(), phrase);
		}
		for (Phrase phrase : this.premises) {
			this.wordList.add(phrase);
			this.wordMap.put(phrase.getId(), phrase);
		}

	}

	public void remove(Phrase w) {
		this.claims.remove(w);
		this.premises.remove(w);
		this.wordList.remove(w);
	}

	public void removeWords(Phrases other) {
		this.wordList.removeAll(other.getWordList());
		this.claims.removeAll(other.getClaims());
		this.premises.removeAll(other.getPremises());
	}

}
