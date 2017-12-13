package data;

public class RelationPatternNode extends PatternNode {

	public RelationPatternNode(WordNode dNode) {
		super(dNode);
		// TODO Auto-generated constructor stub
	}

	public void initializePatternTree(WordNode current, WordNode path, String tag) {
		WordNode testedNode = current.getPredecessor();
		Boolean isEnd = true;
		if (testedNode == null)
			return;
		if (testedNode != current && testedNode != path && testedNode.getTag().equals(tag)) {
			RelationPatternNode t = new RelationPatternNode(testedNode);
			this.setPrevious(t);
			t.initializePatternTree(testedNode, current, tag);
			testedNode.setTag("");
			isEnd = false;
		}
		for (WordNode succNode : current.getSuccessors()) {
			if (succNode != path && succNode.getTag().equals(tag) && succNode != current) {
				RelationPatternNode t = new RelationPatternNode(succNode);
				this.addSuccessor(t);
				t.initializePatternTree(succNode, current, tag);
				succNode.setTag("");
				isEnd = false;
			}
		}
		this.setIsEnd(isEnd);
	}
}
