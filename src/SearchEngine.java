import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;

public class SearchEngine {
	
	SearchEngine(String queryFile, IndexingEngine ie, String [] stopWords) throws IOException
	{
		this._queryFile = queryFile;
		CharArraySet stopWordSet = new CharArraySet(Arrays.asList(stopWords),true);
		
		//Standard Analyzer is the most sophisticated analyzer and contains removal of stop words
		this._queryParser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer(stopWordSet));
		this._indexSearcher = new IndexSearcher(DirectoryReader.open(ie.getIndexDir()));
	}
	
	public TopDocs[] searchQueries() throws IOException, ParseException
	{
		Query [] queries = this.parseQueriesFile(this._queryFile);
		TopDocs[] results = new TopDocs[queries.length];
		
		for(int i=0; i<queries.length;++i)
		{
			// 	TODO: how to filter this to be only results above T score?
			// TODO: Is this tf-idf?
			results[i] = this._indexSearcher.search(queries[i], LuceneConstants.MAX_SEARCH);
		}
		
		return results;
	}
	
	
	private Query[] parseQueriesFile(String queriesFilePath) throws IOException, ParseException {
		String queriesString = new String(Files.readAllBytes(Paths.get(queriesFilePath)));
		String[] queries = queriesString.split(QUERY_REGEX);
		//remove first empty string as a result of split
		queries = Arrays.copyOfRange(queries, 1, queries.length);
		
		Query[] searchQueries = new Query [queries.length];
		for(int i=0; i<queries.length; ++i)
		{
			searchQueries[i] = _queryParser.parse(queries[i]);
		}
		return searchQueries;
	}
	
	private IndexSearcher _indexSearcher;
	private QueryParser _queryParser;
	private String _queryFile;
    private static final String QUERY_REGEX = "\\*FIND\\s*\\d+.*";
}
