package utils;

import java.util.Comparator;

import data.ProbabilityNode;

public class Comperers {
	public Comperers() {

	}

	private Comparator<ProbabilityNode>	normalizedValueComperator	= new Comparator<ProbabilityNode>() {
																		@Override
																		public int compare(ProbabilityNode o1,
																				ProbabilityNode o2) {
																			if (o1.getNormalizedValues() < o2
																					.getNormalizedValues())
																				return 1;
																			else if (o1.getNormalizedValues() > o2
																					.getNormalizedValues())
																				return -1;
																			else
																				return 0;
																		}
																	};

	private Comparator<ProbabilityNode>	accuracyComperator			= new Comparator<ProbabilityNode>() {
																		@Override
																		public int compare(ProbabilityNode o1,
																				ProbabilityNode o2) {
																			if (o1.getAccuracy() < o2.getAccuracy())
																				return 1;
																			else if (o1.getAccuracy() > o2
																					.getAccuracy())
																				return -1;
																			else
																				return 0;
																		}
																	};

	private Comparator<ProbabilityNode>	supportComperator			= new Comparator<ProbabilityNode>() {
																		@Override
																		public int compare(ProbabilityNode o1,
																				ProbabilityNode o2) {
																			if (o1.getTotalSupport() < o2
																					.getTotalSupport())
																				return 1;
																			else if (o1.getTotalSupport() > o2
																					.getTotalSupport())
																				return -1;
																			else
																				return 0;
																		}
																	};

	public Comparator<ProbabilityNode> getNVC() {
		return this.normalizedValueComperator;
	}

	public Comparator<ProbabilityNode> getAccC() {
		return this.accuracyComperator;
	}

	public Comparator<ProbabilityNode> getSupC() {
		return this.supportComperator;
	}
}
