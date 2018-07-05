import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;

public class SearchEngine {
    /**
     * c'tor
     * @param index - documents index
     * @throws IOException
     */
    SearchEngine(IndexingEngine index) throws IOException {
        this._queryParser = new QueryParser(LuceneConstants.CONTENTS, new StandardAnalyzer());
        this._index = index;
        this._indexSearcher = new IndexSearcher(DirectoryReader.open(index.getIndex()));
        _indexSearcher.setSimilarity(new ClassicSimilarity());
        this._k = 0;
    }
    
    public void setK(Integer k) {
    	this._k = k;
    }
    
	/**
	 * performs a query on the index with the given doc
	 * @return an object that contains the relevant document
	 * @throws Exception
	 */
    public Result searchDoc(DocumentInstance doc) throws Exception {
        Query query = this.noramlizeDoc(doc);
        
        // tf-idf scoring - 
        // https://lucene.apache.org/core/3_5_0/scoring.html#Scoring
        // https://lucene.apache.org/core/3_5_0/api/core/org/apache/lucene/search/Similarity.html
        TopDocs result = this._indexSearcher.search(query, this._k);
        
        // formatting result
        List<Integer> docIds = new ArrayList<>();
        for (ScoreDoc score: result.scoreDocs) {
        		docIds.add(score.doc);
        }
		return new Result(doc.docId, docIds.toArray(new Integer[docIds.size()]));
    }
    
    /**
     * normalizes a document to a query form
	 * @throws Exception
     */
    private Query noramlizeDoc(DocumentInstance doc) throws Exception {
		doc.normalize(_index._normalizingStrings);
		return _queryParser.parse(doc.content);
    }
    
    protected IndexSearcher _indexSearcher;
    IndexingEngine _index;
    private QueryParser _queryParser;
    private Integer _k;
}
