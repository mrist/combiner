package data;

import java.util.ArrayList;

public class PatternNode implements Comparable<PatternNode> {

	private String					type;

	private PatternNode				previous	= null;
	private ArrayList<PatternNode>	successors	= new ArrayList<PatternNode>();
	private ProbabilityNode			prob		= null;
	private Boolean					isEnd		= false;

	public PatternNode(WordNode dNode) {
		this.type = dNode.getType();
	}

	@Override
	public boolean equals(Object o) {
		Boolean result = true;
		Boolean innerResult = false;
		PatternNode other = (PatternNode) o;
		result &= other.getType().equals(this.type);
		if ((this.previous == null && other.previous != null) || (this.previous != null && other.previous == null)) {
			return false;
		}
		else if (this.previous != null && this.previous != this && other.previous != null) {
			result &= this.previous.equalWithPath(other.previous, this);
		}
		if (other.successors.size() == this.successors.size()) {
			if (!this.successors.isEmpty() && !other.successors.isEmpty()) {
				for (PatternNode typeNode : this.successors) {
					for (PatternNode otherTypeNode : other.successors) {
						innerResult |= typeNode.equalWithPath(otherTypeNode, this);
					}
					result &= innerResult;
				}
			}
		}
		else {
			return false;
		}

		return result;

	}

	public boolean equalWithPath(PatternNode other, PatternNode path) {
		Boolean result = true;
		Boolean innerResult = false;
		result &= other.getType().equals(this.type);
		if (result) {
			if ((this.previous == null && other.previous != null)
					|| (this.previous != null && other.previous == null)) {
				return false;
			}
			else if (this.previous != null && other.previous != null && this.previous != this
					&& this.previous != path) {
				result &= this.previous.equalWithPath(other.previous, this);
			}

			if (other.successors.size() == this.successors.size()) {
				if (!this.successors.isEmpty()) {
					for (PatternNode typeNode : this.successors) {
						if (typeNode != path) {
							for (PatternNode otherTypeNode : other.successors) {
								innerResult |= typeNode.equalWithPath(otherTypeNode, this);

							}
							result &= innerResult;
						}
					}
				}
			}
			else {
				return false;
			}

		}
		return result;
	}

	public String getType() {
		return type;
	}

	public PatternNode getPrevious() {
		return previous;
	}

	public void setPrevious(PatternNode previous) {
		this.previous = previous;
	}

	public void addSuccessor(PatternNode successor) {
		this.successors.add(successor);
	}

	public void print() {
		this.printRec("");
	}

	public String printSequence() {
		return this.printSequenceRec("");
	}

	public void printRec(String level) {
		System.out.println(level + this.type);
		if (this.previous != null) {
			this.previous.printRec(level + "+ ");
		}
		for (PatternNode typeNode : successors) {
			typeNode.printRec(level + "- ");
		}
	}

	public String printSequenceRec(String sequence) {
		sequence += this.type;
		if (this.previous != null) {
			return this.previous.printSequenceRec(sequence + ", +, ");
		}
		if (this.successors.size() > 1) {
			System.out.println("split");
		}
		for (PatternNode typeNode : successors) {
			return typeNode.printSequenceRec(sequence + ", -, ");
		}
		return sequence;
	}

	public ArrayList<PatternNode> getSuccessors() {
		return this.successors;
	}

	@Override
	public int compareTo(PatternNode o) {
		return this.getDepth() - o.getDepth();
	}

	public int getDepth() {
		int depth = 0;
		int currentDepth = 0;
		for (PatternNode succ : this.getSuccessors()) {
			currentDepth = succ.getDepth();
			if (depth < currentDepth) {
				depth = currentDepth;
			}
		}
		return depth;
	}

	public ProbabilityNode getProbabilityNode() {
		return prob;
	}

	public Boolean getIsEnd() {
		return isEnd;
	}

	public void setIsEnd(Boolean isEnd) {
		this.isEnd = isEnd;
	}

	public ArrayList<PatternNode> getEndNodesRec() {
		ArrayList<PatternNode> endNodes = new ArrayList<PatternNode>();
		if (this.isEnd) {
			endNodes.add(this);
			return endNodes;
		}
		if (this.previous != null) {
			endNodes.addAll(this.previous.getEndNodesRec());
		}
		if (!this.successors.isEmpty()) {
			for (PatternNode succ : this.successors) {
				endNodes.addAll(succ.getEndNodesRec());
			}
		}
		return endNodes;
	}
}
