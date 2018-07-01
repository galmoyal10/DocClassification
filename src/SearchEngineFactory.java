import java.util.List;

/**
 * Returns the corresponding SearchEngine according to the given mode
 */
public class SearchEngineFactory {
	public static SearchEngine getSE(boolean basicMode, List<DocumentInstance> testDocs, IndexingEngine ie, String [] stopWords) throws Exception
	{
		if (basicMode) {
			return new SearchEngine(testDocs, ie, stopWords, LuceneConstants.BASIC_RELEVANCE_THRESHOLD);
		} 
		return new ImprovedSearchEngine(testDocs, ie, stopWords, LuceneConstants.IMPROVED_RELEVANCE_THRESHOLD);
	}
}
