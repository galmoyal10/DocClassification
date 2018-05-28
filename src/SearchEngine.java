import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class SearchEngine {
	SearchEngine(String queryFile, String docsFile, String outputFile, boolean mode) throws Exception {
		this._index = new RAMDirectory();
		this._queryFile = queryFile;
		this._docsFile = docsFile;
		this._outputFile = outputFile;
		this._mode = mode;		
		this._topStopWords = new String[STOP_WORDS];
	}
	
	public void run() throws Exception {
		this.parseQueriesFile(this._queryFile);
		this.indexDocuments(this.parseDocumentsFile(this._docsFile));
		this.corpusTopTerms();
	}

	private void parseQueriesFile(String queriesFilePath) throws IOException {
		String queriesString = new String(Files.readAllBytes(Paths.get(queriesFilePath)));
		String[] queries = queriesString.split(QUERY_REGEX);
		//remove first empty string as a result of split
		queries = Arrays.copyOfRange(queries, 1, queries.length);
		this._queries = queries;
	}

	private String[] parseDocumentsFile(String documentsFilePath) throws IOException {
		String documentsString = new String(Files.readAllBytes(Paths.get(documentsFilePath)));
		//TODO - split better
		String[] documents = documentsString.split(DOCUMENT_REGEX);
		return documents;
	}
	
	private void indexDocuments(String[] documents) throws IOException {
		StandardAnalyzer analyzer = new StandardAnalyzer();
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		IndexWriter indexWriter = new IndexWriter(this._index, config);
		for (String document: documents) {
			Document d = new Document();
			FieldType f = new FieldType();
			f.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
			Field content = new Field(FIELD_NAME, document, f);
			d.add(content);
			indexWriter.addDocument(d);
		}
		indexWriter.close();
	}
	
	private void corpusTopTerms() throws Exception {
		IndexReader indexReader = DirectoryReader.open(this._index);
		TermStats[] commonTerms = HighFreqTerms.getHighFreqTerms(indexReader, STOP_WORDS, FIELD_NAME, new HighFreqTerms.TotalTermFreqComparator());
		int stopWordIndex = 0;
		for (TermStats commonTerm : commonTerms) {
			this._topStopWords[stopWordIndex] = commonTerm.termtext.utf8ToString(); 
		    stopWordIndex++;
		}
	}
	
    private String[] _queries;
    private Directory _index;
    private String[] _topStopWords;
    
	private String _queryFile;
    private String _docsFile;
    private String _outputFile;
    private boolean _mode;

    private static final String QUERY_REGEX = "\\*FIND\\s*\\d+.*";
    private static final String DOCUMENT_REGEX = "\\*TEXT.*\\d+.*";
    private static final String FIELD_NAME = "content";
    private static final int STOP_WORDS = 20;
}
