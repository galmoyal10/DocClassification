package src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;


public class SearchResults {
	
	/**
	 * formats search engine results with relevance filtration
	 * @param results - results from search engine
	 * @param threashold - the T value of relevance threshold to enforce 
	 * @return
	 */
	static List<Result> results(TopDocs[] results, Double threashold) {
		List<Result> parsedResults = new ArrayList<>();
		Integer queryId = 1;
		for (TopDocs result: results) 
		{
			List<Integer> docIds = new ArrayList<>();
            for (ScoreDoc score: result.scoreDocs) 
            {
            	if(score.score >= threashold)
            	{
            		docIds.add(score.doc);            		
            	}
            }
			parsedResults.add(new Result(queryId, docIds.toArray(new Integer[docIds.size()])));
			++queryId;
		}
		return parsedResults;
	}
	
	/**
	 * formats search engine results
	 * @param results - results from search engine
	 * @return
	 */
	static List<Result> results(TopDocs[] results)
	{
		return SearchResults.results(results, 0.0);
	}
	
	
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
	 */
	static void compareSearchResults(List<Result> results, List<Result> actual) {
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
	
		System.out.println("Total: Precision - " + (precisions.stream().mapToDouble(f -> f.doubleValue()).sum() / precisions.size()) + 
								" Recall - " + (recalls.stream().mapToDouble(f -> f.doubleValue()).sum() / recalls.size()));
	}
}
