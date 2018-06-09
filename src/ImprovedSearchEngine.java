import java.io.IOException;

import org.apache.lucene.search.similarities.BM25Similarity;

public class ImprovedSearchEngine extends SearchEngine{
	ImprovedSearchEngine(String queryFile, IndexingEngine ie, String [] stopWords, double relevanceThreshold) throws IOException
	{
		super(queryFile, ie, stopWords, relevanceThreshold);
		_indexSearcher.setSimilarity(new BM25Similarity());
	}
}
