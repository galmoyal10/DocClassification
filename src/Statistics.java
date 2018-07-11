/**
 * utility statistics functions
 */
public class Statistics {
	public static Double precision(Double tp, Double fp) {
		return tp / (tp + fp);
	}
	
	public static Double recall(Double tp, Double total) {
		return tp / total;
	}
	
	public static Double harmonicMean(Double precision, Double recall) {
		return 1 / (((1 / precision) * 0.5) + ((1 / recall) * 0.5));
	}
	
}
