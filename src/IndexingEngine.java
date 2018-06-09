import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
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
	IndexingEngine(String docsFile) throws Exception {
		this._index = new RAMDirectory();
		this._docsFile = docsFile;

		_normalizingStrings.put("-", " ");
	}

	public void run() throws Exception {
		this.indexDocuments(this.parseDocumentsFile(this._docsFile));
	}

	public Directory getIndexDir() {
		return this._index;
	}

	private String[] parseDocumentsFile(String documentsFilePath) throws IOException {
		String documentsString = new String(Files.readAllBytes(Paths.get(documentsFilePath)));
		String[] documents = documentsString.split(DOCUMENT_REGEX);
		return documents;
	}

	public String normalizeString(String document) throws IOException {
		document = document.toLowerCase();
		for (Map.Entry<String, String> e : this._normalizingStrings.entrySet()) {
			document = document.replaceAll(e.getKey(), e.getValue());
		}
		return document;
	}

	private void indexDocuments(String[] documents) throws IOException {
		IndexWriter indexWriter = new IndexWriter(this._index, new IndexWriterConfig(new StandardAnalyzer()));
		for (String document : documents) {
			document = normalizeString(document);

			Document d = new Document();
			FieldType f = new FieldType();
			f.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
			Field content = new Field(LuceneConstants.CONTENTS, document, f);
			d.add(content);
			indexWriter.addDocument(d);
		}
		indexWriter.close();
	}

	public String[] getCorpusTopTerms() throws Exception {
		String[] topStopWords = new String[STOP_WORDS];
		IndexReader indexReader = DirectoryReader.open(this._index);
		TermStats[] commonTerms = HighFreqTerms.getHighFreqTerms(indexReader, STOP_WORDS, LuceneConstants.CONTENTS,
				new HighFreqTerms.TotalTermFreqComparator());
		int stopWordIndex = 0;
		for (TermStats commonTerm : commonTerms) {
			topStopWords[stopWordIndex] = commonTerm.termtext.utf8ToString();
			stopWordIndex++;
		}
		return topStopWords;
	}

	private Directory _index;
	private String _docsFile;
	private static final String DOCUMENT_REGEX = "(?:\\*TEXT.*\\d+.*|\\*STOP)";
	private static final int STOP_WORDS = 20;
	protected HashMap<String, String> _normalizingStrings = new HashMap<>();
}
