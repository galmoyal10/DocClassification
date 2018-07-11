
/**
 * Represents the statistics of a single label over a classification batch
 */
public class Label {
	Label(Integer label) {
		this.label = label;
	}
	
	Double labelPrecision() {
		return Statistics.precision(this.truePositive, this.falsePositive);
	}
	
	Double labelRecall() {
		return Statistics.recall(this.truePositive, this.totalDocs);
	}
	
	Double harmonicMean() {
		return Statistics.harmonicMean(labelPrecision(), labelRecall());
	}
	
	public String toString() {
		return "Label " + label + ":\ntrue total: " + this.totalDocs +"\n" +
				"classified: " + this.truePositive + "\n" +
				"wrong classified: " + this.falsePositive + "\n" + 
				"precission: " + this.labelPrecision() +"\n"+
				"recall: " + this.labelRecall() +"\n" +
				"f: " + this.harmonicMean();
	}
	
	public Integer label;
	public Double totalDocs = 0.0;
	public Double truePositive = 0.0;
	public Double falsePositive = 0.0;
}
