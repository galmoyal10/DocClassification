/**
 * Returns the corresponding SearchEngine according to the given mode
 */
public class SearchEngineFactory {
	public static SearchEngine getSE(boolean basicMode, String queryFile, IndexingEngine ie, String [] stopWords) throws Exception
	{
		if (basicMode) {
			return new SearchEngine(queryFile, ie, stopWords, LuceneConstants.BASIC_RELEVANCE_THRESHOLD);
		} 
		return new ImprovedSearchEngine(queryFile, ie, stopWords, LuceneConstants.IMPROVED_RELEVANCE_THRESHOLD);
	}
}
