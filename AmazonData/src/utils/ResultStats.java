package utils;

public class ResultStats {
	private double	hit;
	private double	miss;
	private double	none;
	private int		total;
	private int		totalCorrect;

	public ResultStats() {
		this.hit = 0;
		this.miss = 0;
		this.none = 0;
		this.total = 0;
	}

	public ResultStats(double hit, double miss, double none) {
		this.hit = hit;
		this.miss = miss;
		this.none = none;
	}

	public ResultStats add(ResultStats other) {
		this.hit += other.hit;
		this.miss += other.miss;
		this.none += other.none;
		this.total += other.total;
		this.totalCorrect += other.totalCorrect;
		return this;
	}

	public String getResult() {
		return this.hit + " " + this.miss + " " + (this.hit + this.miss) + " "
				+ (double) this.hit / (double) (this.hit + this.miss) + " " + this.totalCorrect;
	}

	public void addHit() {
		this.hit += 1;
	}

	public void addMiss() {
		this.miss++;
	}

	public void addNone() {
		this.none++;
	}

	public double getPrecision() {
		return this.hit / (this.miss + this.hit);
	}

	public double getRecall() {
		return this.hit / this.totalCorrect;
	}

	public double getF1Score() {
		return 2 * ((this.getPrecision() * this.getRecall()) / (this.getPrecision() + this.getRecall()));
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public void setCorrect(int size) {
		this.totalCorrect = size;

	}

	public void addTotalCorrect(int total) {
		this.totalCorrect += total;
	}
}
