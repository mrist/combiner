package data;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SRLSemanticPredicate {
	@Expose
	private String							category;
	@SerializedName("arguments")
	@Expose
	private ArrayList<SRLSemanticArgument>	arguments	= new ArrayList<SRLSemanticArgument>();

	public SRLSemanticPredicate(String category) {
		this.category = category;
	}

	public void addArgument(SRLSemanticArgument outputSemanticArgument) {
		this.arguments.add(outputSemanticArgument);
	}

	public ArrayList<SRLSemanticArgument> getArguments() {
		return this.arguments;
	}

	public String getCategory() {
		return this.category;
	}

	public void initialize(ReviewSentence reviewSentence) {
		for (SRLSemanticArgument srlSemanticArgument : this.arguments) {
			srlSemanticArgument.initialize(reviewSentence);
			srlSemanticArgument.setPredicate(this);
		}

	}

	public SRLSemanticArgument getOtherArgument(SRLSemanticArgument currentArgument) {
		for (SRLSemanticArgument argument : this.arguments) {
			if (currentArgument != argument) {
				return argument;
			}
		}
		System.out.println("");
		return null;
	}

	public WordNode getOtherArgumentWord(SRLSemanticArgument currentArgument) {
		for (SRLSemanticArgument argument : this.arguments) {
			if (currentArgument != argument) {
				return argument.getWord();
			}
		}
		return null;
	}
}
