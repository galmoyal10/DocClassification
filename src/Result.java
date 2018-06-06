package src;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Result {
	Result(Integer queryId, Integer[] documents) {
		this.queryId = queryId;
		this.documents = documents;
	}
	
	/**
	 * compares two results
	 * @param result - search engine result
	 * @param expected - expected result
	 * @return a tuple of (precision, recall)
	 */
	static Double[] compare(Result result, Result expected) {
		Set<Integer> s1 = new HashSet<Integer>(Arrays.asList(result.documents));
		Set<Integer> s2 = new HashSet<Integer>(Arrays.asList(expected.documents));
		// intersect to get relevant documents in result
		s1.retainAll(s2);
		Double precision =  (double)result.documents.length == 0 ? Double.POSITIVE_INFINITY : ((double)s1.size() / (double)result.documents.length);
		Double recall = (double)expected.documents.length ==0 ? Double.POSITIVE_INFINITY : ((double)s1.size() / (double)expected.documents.length);
		Double[] stats = new Double[2];
		stats[0] = precision;
		stats[1] = recall;
		return stats;
	}
	
	Integer queryId;
	Integer[] documents;
}
