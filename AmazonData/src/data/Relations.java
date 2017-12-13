package data;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Relations {
	@SerializedName("relations")
	@Expose
	private ArrayList<Relation>						relations				= new ArrayList<Relation>();
	private HashMap<Phrase, ArrayList<Relation>>	relationsByAspect		= new HashMap<Phrase, ArrayList<Relation>>();
	private HashMap<Phrase, ArrayList<Relation>>	relationsByStatement	= new HashMap<Phrase, ArrayList<Relation>>();

	public Relations() {
	}

	public void add(Relation r) {
		this.relations.add(r);

		Phrase tempWord = r.getAspect();
		ArrayList<Relation> tempRelations = new ArrayList<Relation>();

		if (relationsByAspect.containsKey(tempWord)) {
			tempRelations = relationsByAspect.get(tempWord);
		}
		tempRelations.add(r);
		relationsByAspect.put(tempWord, tempRelations);

		tempWord = r.getStatement();
		tempRelations = new ArrayList<Relation>();
		if (relationsByStatement.containsKey(tempWord)) {
			tempRelations = relationsByStatement.get(tempWord);
		}
		tempRelations.add(r);
		relationsByStatement.put(tempWord, tempRelations);
	}

	public void addAll(ArrayList<Relation> relations) {
		for (Relation relation : relations) {
			this.add(relation);
		}

	}

	public ArrayList<Relation> all() {
		return relations;
	}

	public void append(Relations other) {
		this.relations.addAll(other.all());
		this.relationsByAspect.putAll(other.relationsByAspect);
		this.relationsByStatement.putAll(other.relationsByStatement);
	}

	public ArrayList<Relation> getByAspect(Phrase aspect) {
		return this.relationsByAspect.get(aspect);
	}

	public ArrayList<Relation> getByStatement(Phrase statement) {
		return this.relationsByStatement.get(statement);
	}

	public void initialize(Phrases phrases) {
		for (Relation relation : relations) {
			relation.initialize(phrases);

			Phrase tempWord = relation.getAspect();
			ArrayList<Relation> tempRelations = new ArrayList<Relation>();

			if (relationsByAspect.containsKey(tempWord)) {
				tempRelations = relationsByAspect.get(tempWord);
			}
			tempRelations.add(relation);
			relationsByAspect.put(tempWord, tempRelations);

			tempWord = relation.getStatement();
			tempRelations = new ArrayList<Relation>();
			if (relationsByStatement.containsKey(tempWord)) {
				tempRelations = relationsByStatement.get(tempWord);
			}
			tempRelations.add(relation);
			relationsByStatement.put(tempWord, tempRelations);
		}
	}

	public void remove(Relation r) {
		this.relations.remove(r);

		Phrase tempWord = r.getAspect();
		ArrayList<Relation> tempRelations = new ArrayList<Relation>();

		if (relationsByAspect.containsKey(tempWord)) {
			tempRelations = relationsByAspect.get(tempWord);
			tempRelations.remove(r);
			relationsByAspect.put(tempWord, tempRelations);
		}

		tempWord = r.getStatement();
		tempRelations = new ArrayList<Relation>();
		if (relationsByStatement.containsKey(tempWord)) {
			tempRelations = relationsByStatement.get(tempWord);
			tempRelations.remove(r);
			relationsByStatement.put(tempWord, tempRelations);
		}
	}

	public double getRecall() {
		double hit = 0;
		for (Relation relation : relations) {
			if (relation.isFound()) {
				hit++;
			}
		}
		return hit / this.relations.size();
	}

	public ArrayList<Relations> split10() {
		ArrayList<Relations> rels = new ArrayList<Relations>();
		int parts = this.relations.size() / 10;
		int counter = 0;
		Relations currentSplit = new Relations();
		for (Relation rel : this.relations) {
			counter++;
			currentSplit.add(rel);
			if (counter > parts) {
				rels.add(currentSplit);
				currentSplit = new Relations();
				counter = 0;
			}
		}
		rels.add(currentSplit);
		return rels;
	}

	public ArrayList<Phrase> getStatements() {
		ArrayList<Phrase> temp = new ArrayList<Phrase>();
		for (Phrase phrase : this.relationsByStatement.keySet()) {
			temp.add(phrase);
		}
		return temp;
	}

	public ArrayList<Phrase> getAspects() {
		ArrayList<Phrase> temp = new ArrayList<Phrase>();
		for (Phrase phrase : this.relationsByAspect.keySet()) {
			temp.add(phrase);
		}
		return temp;
	}

	public Relations getClearedRelations() {
		Relations rel = new Relations();
		for (Relation relation : this.all()) {
			if (relation.getAspect().getExitNodes().size() == 1 && relation.getStatement().getExitNodes().size() == 1) {
				rel.add(relation);
			}
		}
		return rel;
	}

	public Relations getRelationsToAdjectives() {
		Relations rel = new Relations();
		for (Relation relation : this.all()) {
			if (relation.getAspect().getExitNodes().size() == 1 && relation.getStatement().getExitNodes().size() == 1) {
				if (relation.getStatement().getExitNodes().get(0).getType().equals("ADJECTIVE")) {
					rel.add(relation);
				}
			}
		}
		return rel;
	}

	public ArrayList<Relations> getCleared10Split() {
		Relations clearedRels = new Relations();
		for (Relation relation : this.getClearedRelations().all()) {
			if (relation.getAspect().getExitNodes().size() == 1 && relation.getStatement().getExitNodes().size() == 1) {
				clearedRels.add(relation);
			}
		}
		ArrayList<Relations> rels = new ArrayList<Relations>();
		int parts = clearedRels.all().size() / 10;
		int counter = 0;
		Relations currentSplit = new Relations();
		for (Relation rel : clearedRels.all()) {
			counter++;
			currentSplit.add(rel);
			if (counter > parts) {
				rels.add(currentSplit);
				currentSplit = new Relations();
				counter = 0;
			}
		}
		rels.add(currentSplit);
		return rels;
	}
}
