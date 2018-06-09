import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;

public class SearchEngine {
    /**
     * c'tor
     * @param queriesFile - path to queries file
     * @param index - documents index
     * @param stopWords - stop words to use when searching
     * @param relevanceThreshold - threshold to apply on result's scores.
     * @throws IOException
     */
    SearchEngine(String queriesFile, IndexingEngine index, String [] stopWords, double relevanceThreshold) throws IOException
    {
        this._queryFile = queriesFile;
        CharArraySet stopWordSet = new CharArraySet(Arrays.asList(stopWords),true);
        
        //Standard Analyzer is the most sophisticated analyzer and contains removal of stop words
        this._queryParser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer(stopWordSet));
        this._index = index;
        this._indexSearcher = new IndexSearcher(DirectoryReader.open(index.getIndex()));
        _indexSearcher.setSimilarity(new ClassicSimilarity());
        this._relevanceThreshold = relevanceThreshold;
    }
    
    
	/**
	 * formats search engine results with relevance filtration
	 * @param results - results from search engine
	 * @param threashold - the T value of relevance threshold to enforce 
	 * @return
	 */
	static List<Result> filterResults(TopDocs[] results, Double threashold) {
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
	 * performs actual search of queries using the index
	 * @return a list of Result objects
	 * @throws IOException
	 * @throws ParseException
	 */
    public List<Result> searchQueries() throws IOException, ParseException
    {
        Query [] queries = this.parseQueriesFile(this._queryFile);
        TopDocs[] results = new TopDocs[queries.length];
        
        for(int i=0; i<queries.length;++i)
        {
            // tf-idf scoring - 
            // https://lucene.apache.org/core/3_5_0/scoring.html#Scoring
            // https://lucene.apache.org/core/3_5_0/api/core/org/apache/lucene/search/Similarity.html
            results[i] = this._indexSearcher.search(queries[i], LuceneConstants.MAX_SEARCH);
        }
        
        return filterResults(results, this._relevanceThreshold);
    }
    
    private Query[] parseQueriesFile(String queriesFilePath) throws IOException, ParseException {
        String queriesString = new String(Files.readAllBytes(Paths.get(queriesFilePath)));
        String[] queries = queriesString.split(QUERY_REGEX);
        //remove first empty string as a result of split
        queries = Arrays.copyOfRange(queries, 1, queries.length);
        
        Query[] searchQueries = new Query [queries.length];
        for(int i=0; i<queries.length; ++i)
        {
            searchQueries[i] = _queryParser.parse(_index.normalizeString(queries[i]));
        }
        return searchQueries;
    }
    
    protected IndexSearcher _indexSearcher;
    IndexingEngine _index;
    private QueryParser _queryParser;
    private String _queryFile;
    private double _relevanceThreshold;
    private static final String QUERY_REGEX = "\\*FIND\\s*\\d+.*";
}
