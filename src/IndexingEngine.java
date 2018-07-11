import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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
			d.add(content);
			indexWriter.addDocument(d);
		}
		indexWriter.close();
	}

	private Directory _index;
	private List<DocumentInstance> _docs;
	protected HashMap<String, String> _normalizingStrings = new HashMap<>();
}
