import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class SearchResults {	
	/**
	 * load truth results
	 * @param fileName - path of truth file
	 * @return the list of truth result
	 * @throws IOException
	 */
	static List<Result> loadFromFile(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		List<Result> docs = new ArrayList<>();
		String line;
		try {
		    while ((line = br.readLine()) != null) {
		    	if (line.compareTo("") == 0) {
		    		continue;
		    	}
		    	String[] parsedLine = line.split("\\s+");
		    	Integer queryId = Integer.parseInt(parsedLine[0]);
		    	Integer[] docIds = new Integer[parsedLine.length - 1];
		    	for (int i = 1; i < parsedLine.length; ++i) {
		    		docIds[i-1] = Integer.parseInt(parsedLine[i]);
		    	}
		    	docs.add(new Result(queryId, docIds));
		    }
		} finally {			
			br.close();
		}
		return docs;
	}
	
	/**
	 * compares search engine results with truth
	 * @param results
	 * @param actual
	 * 
	 * @return the combined measure F - harmonic mean of R and P
	 */
	static double[] compareSearchResults(List<Result> results, List<Result> actual) {
		List<Double> precisions = new ArrayList<>();
		List<Double> recalls = new ArrayList<>();
		Iterator<Result> resultsIter = results.iterator();
		Iterator<Result> actualIter = actual.iterator();
		
		while(resultsIter.hasNext() && actualIter.hasNext()) {
			Result result = resultsIter.next();
			Result actualResult = actualIter.next();
			Double[] stats = Result.compare(result, actualResult);
			System.out.println("Query " + result.queryId.toString() + " Precision - " + stats[0].toString() + ", Recall - " + stats[1].toString());
			
			//filter out the irrelevant stats
			if(stats[0]!=Double.POSITIVE_INFINITY ) 
			{
				precisions.add(stats[0]);
			}
			
			if(stats[1] != Double.POSITIVE_INFINITY)
			{
				recalls.add(stats[1]);
			}
		}
		
		double totalPrecision = precisions.stream().mapToDouble(f -> f.doubleValue()).sum() / precisions.size();
		double totalRecall = recalls.stream().mapToDouble(f -> f.doubleValue()).sum() / recalls.size();
		double f = 1/(0.5*(1/totalPrecision + 1/totalRecall));
		
		System.out.println("Total: Precision - " + totalPrecision + 
								" Recall - " + totalRecall +
								" F - " + f) ;
		double res[] = new double[3];
		res[0] = totalPrecision;
		res[1] = totalRecall;
		res[2] = f;
		return res;
	}
}
