package data;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.annotations.Expose;

public class ReviewSentence {
	@Expose
	private int							number;
	@Expose
	private int							start			= 0;
	@Expose
	private int							end				= 0;
	@Expose
	private WordNode					root			= null;
	@Expose
	private ArrayList<WordNode>			labelledWords	= new ArrayList<WordNode>();
	private HashMap<Integer, WordNode>	wordMap			= new HashMap<Integer, WordNode>();

	private Phrases						phrases			= new Phrases();
	private Relations					relations		= new Relations();

	public ReviewSentence(int number) {
		this.wordMap = new HashMap<Integer, WordNode>();
		this.number = number;
	}

	public void addPhrase(Phrase p) {
		this.phrases.add(p);
	}

	public void addRelations(ArrayList<Relation> relations) {
		this.relations.addAll(relations);

	}

	public void addWord(WordNode w) {
		this.labelledWords.add(w);
	}

	public int getEnd() {
		return end;
	}

	public int getNumber() {
		return this.number;
	}

	public int getStart() {
		return start;
	}

	public ArrayList<WordNode> getWords() {
		return this.labelledWords;
	}

	/**
	 * Fills HashMap and creates graph structure
	 */
	public void initialize() {
		this.wordMap = new HashMap<Integer, WordNode>();
		this.phrases = new Phrases();
		this.relations = new Relations();
		int prevID = 0;
		WordNode prevWord = null;
		for (WordNode labelledWord : labelledWords) {
			this.wordMap.put(labelledWord.getId(), labelledWord);
		}
		for (WordNode labelledWord : labelledWords) {
			labelledWord.initialize(this);
			prevID = labelledWord.getPreviousID();
			if (prevID != 0) {
				root = labelledWord;
				prevWord = this.wordMap.get(prevID);
				prevWord.addSucessors(labelledWord);
			}
			else {
				prevWord = labelledWord;
			}
			labelledWord.setPredcessor(prevWord);
			if (labelledWord.isPredicate()) {
				for (SRLSemanticArgument argument : labelledWord.getArguments()) {
					this.wordMap.get(argument.getId()).addArgumentRoots(labelledWord);
				}
			}
		}

	}

	public void setEnd(int end) {
		this.end = end;
	}

	public void setStart(int start) {
		this.start = start;
	}

	@Override
	public String toString() {
		String r = "(" + this.start + "," + this.end + ")";
		for (WordNode wordNode : labelledWords) {
			r += " " + wordNode.getText();
		}
		return r;

	}

	public WordNode getWord(int index) {
		return this.wordMap.get(index);
	}

	public WordNode getWordBySRL(WordNode aspect) {
		WordNode current = null;
		WordNode prev = aspect;
		WordNode result = null;
		while (current != prev && result == null) {
			current = prev;
			prev = current.getPredecessor();
			for (WordNode wordNode : current.getArgumentRoots()) {
				result = wordNode.getOtherArguments(current);
			}

		}
		return result;
	}

	public WordNode getWordByType(Phrase aspect, String type) {
		WordNode current = null;
		int currentID = this.labelledWords.indexOf(aspect.getExitNodes().get(0));
		int distance = 0;
		int min = currentID;
		int max = currentID;

		while (current == null && !(min <= 0 && max >= this.labelledWords.size() - 1)) {
			min = currentID - distance;
			min = min < 0 ? 0 : min;
			max = currentID + distance;

			max = max > this.labelledWords.size() - 1 ? this.labelledWords.size() - 1 : max;
			distance++;
			if (this.labelledWords.get(max).getType().equals(type)) {
				if (!aspect.contains(current))
					current = this.labelledWords.get(max);
			}
			if (this.labelledWords.get(min).getType().equals(type)) {
				if (!aspect.contains(current))
					current = this.labelledWords.get(min);
			}
		}
		return current;
	}

}
