package data;

public class PhrasePatternNode extends PatternNode {

	public PhrasePatternNode(WordNode dNode) {
		super(dNode);
	}

	public void initializeWordTree(WordNode current, WordNode path, Phrase phrase) {
		PhrasePatternNode t;
		WordNode next = current.getPredecessor();
		if (phrase.contains(next) && next != current && next != path) {
			t = new PhrasePatternNode(next);
			this.setPrevious(t);
			t.initializeWordTree(next, current, phrase);
		}
		for (WordNode succNode : current.getSuccessors()) {
			if (phrase.contains(succNode) && succNode != current && succNode != path) {
				t = new PhrasePatternNode(succNode);
				this.addSuccessor(t);
				t.initializeWordTree(succNode, current, phrase);
			}
		}
	}
}
