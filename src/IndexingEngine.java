import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


public class IndexingEngine {
	/**
	 * Initializes Indexing Engine
	 * @param docsFile - path to documents file
	 * @throws Exception
	 */
	IndexingEngine(List<DocumentInstance> docs) throws Exception {
		this._index = new RAMDirectory();
		this._docs = docs;

		_normalizingStrings.put("-", " ");
	}

	/**
	 * indexes the given documents
	 * @throws Exception
	 */
	public void run() throws Exception {
		this.indexDocuments(this._docs);
	}

	/**
	 * retrieves the created index
	 */
	public Directory getIndex() {
		return this._index;
	}
	/*
	private String[] parseDocumentsFile(String documentsFilePath) throws IOException {
		String documentsString = new String(Files.readAllBytes(Paths.get(documentsFilePath)));
		String[] documents = documentsString.split(DOCUMENT_REGEX);
		return documents;
	}*/



	/**
	 * performs the indexing
	 * @param documents - an array of documents
	 * @throws IOException
	 */
	private void indexDocuments(List<DocumentInstance> documents) throws IOException {
		IndexWriter indexWriter = new IndexWriter(this._index, new IndexWriterConfig(new StandardAnalyzer()));
		for (DocumentInstance document : documents) {
			document.normalize(this._normalizingStrings);

			Document d = new Document();
			FieldType f = new FieldType();
			f.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
			Field content = new Field(LuceneConstants.CONTENTS, document.content, f);
			Field title = new Field(LuceneConstants.TITLE, document.title, f);
			d.add(content);
			d.add(title);
			indexWriter.addDocument(d);
		}
		indexWriter.close();
	}

	/**
	 * returns the top <STOP_WORDS_SIZE> most frequent terms in the corpus
	 * @throws Exception
	 */
	public String[] getCorpusTopTerms() throws Exception {
		String[] topStopWords = new String[STOP_WORDS_SIZE];
		IndexReader indexReader = DirectoryReader.open(this._index);
		TermStats[] commonTerms = HighFreqTerms.getHighFreqTerms(indexReader, STOP_WORDS_SIZE, LuceneConstants.CONTENTS,
				new HighFreqTerms.TotalTermFreqComparator());
		int stopWordIndex = 0;
		for (TermStats commonTerm : commonTerms) {
			topStopWords[stopWordIndex] = commonTerm.termtext.utf8ToString();
			stopWordIndex++;
		}
		return topStopWords;
	}

	private Directory _index;
	private List<DocumentInstance> _docs;
	private static final String DOCUMENT_REGEX = "(?:\\*TEXT.*\\d+.*|\\*STOP)";
	private static final int STOP_WORDS_SIZE = 20;
	protected HashMap<String, String> _normalizingStrings = new HashMap<>();
}
