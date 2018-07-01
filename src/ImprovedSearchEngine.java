import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.similarities.BM25Similarity;

public class ImprovedSearchEngine extends SearchEngine{
	ImprovedSearchEngine(List<DocumentInstance> testDocs, IndexingEngine ie, String [] stopWords, double relevanceThreshold) throws IOException
	{
		super(testDocs, ie, stopWords, relevanceThreshold);
		_indexSearcher.setSimilarity(new BM25Similarity());
	}
}
