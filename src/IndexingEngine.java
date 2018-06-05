package src;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
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
    IndexingEngine(String docsFile, boolean mode) throws Exception {
        this._index = new RAMDirectory();
        this._docsFile = docsFile;
        this._mode = mode;
    }
    
    public void run() throws Exception {
        this.indexDocuments(this.parseDocumentsFile(this._docsFile));
    }

    public Directory getIndexDir()
    {
        return this._index;
    }

    private String[] parseDocumentsFile(String documentsFilePath) throws IOException {
        String documentsString = new String(Files.readAllBytes(Paths.get(documentsFilePath)));
        String[] documents = documentsString.split(DOCUMENT_REGEX);
        return documents;
    }
    
    private String normalizeDoc(String document) throws IOException 
    {
    	document = document.toLowerCase();
        for (Map.Entry<String,String> e: IndexingEngine.ILLEGAL_STRINGS.entrySet())
        {
            document = document.replaceAll(e.getKey(), e.getValue());
        }
        return document;
    }
    
    private void indexDocuments(String[] documents) throws IOException {
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(this._index, config);
        for (String document: documents) {
        	document = this.normalizeDoc(document);
            
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
        TermStats[] commonTerms = HighFreqTerms.getHighFreqTerms(indexReader, STOP_WORDS, LuceneConstants.CONTENTS, new HighFreqTerms.TotalTermFreqComparator());
        int stopWordIndex = 0;
        for (TermStats commonTerm : commonTerms) {
            topStopWords[stopWordIndex] = commonTerm.termtext.utf8ToString(); 
            stopWordIndex++;
        }
        return topStopWords;
    }
    
    private Directory _index;
    private String _docsFile;
    private boolean _mode;
    private static final String DOCUMENT_REGEX = "(?:\\*TEXT.*\\d+.*|\\*STOP)";
    private static final int STOP_WORDS = 20;
    private static final HashMap<String,String> ILLEGAL_STRINGS = new HashMap<>();
    static {
    	ILLEGAL_STRINGS.put("-", " ");

    	// http://snowball.tartarus.org/algorithms/porter/stemmer.html
    	//Porter Stemmer - step 1
    	ILLEGAL_STRINGS.put("sses\\b", "ss");
    	ILLEGAL_STRINGS.put("ies\\b", "i");
    	ILLEGAL_STRINGS.put("s\\b", "");
//    	
//    	//Porter Stemmer - step 2
    	ILLEGAL_STRINGS.put("eed\\b", "ee");
    	ILLEGAL_STRINGS.put("ed\\b", "");
    	ILLEGAL_STRINGS.put("ing\\b", "");
    	
    	ILLEGAL_STRINGS.put("at\\b", "ate");
    	ILLEGAL_STRINGS.put("bl\\b", "ble");
    	ILLEGAL_STRINGS.put("iz\\b", "ize");
    	
        ILLEGAL_STRINGS.put("ational","ate")  ; //    relational     ->  relate
        ILLEGAL_STRINGS.put("tional","tioN"); //     conditional    ->  condition
        ILLEGAL_STRINGS.put("enci","ence")    ; // valenci        ->  valence
        ILLEGAL_STRINGS.put("anci","ance")    ; // hesitanci      ->  hesitance
        ILLEGAL_STRINGS.put("izer","ize")     ; // digitizer      ->  digitize
        ILLEGAL_STRINGS.put("abli","able")    ; // conformabli    ->  conformable
        ILLEGAL_STRINGS.put("alli","al")      ; // radicalli      ->  radical
        ILLEGAL_STRINGS.put("entli","ent")    ; //  differentli    ->  different
        ILLEGAL_STRINGS.put("eli","e")        ; //vileli        - >  vile
        ILLEGAL_STRINGS.put("ousli","ous")    ; //  analogousli    ->  analogous
        ILLEGAL_STRINGS.put("ization","ize")  ; //    vietnamization ->  vietnamize
        ILLEGAL_STRINGS.put("ation","ate")    ; //  predication    ->  predicate
        ILLEGAL_STRINGS.put("ator","ate")     ; // operator       ->  operate
        ILLEGAL_STRINGS.put("alism","al")     ; //  feudalism      ->  feudal
        ILLEGAL_STRINGS.put("iveness","ive")  ; //    decisiveness   ->  decisive
        ILLEGAL_STRINGS.put("fulness","ful")  ; //    hopefulness    ->  hopeful
        ILLEGAL_STRINGS.put("ousness","ous")  ; //    callousness    ->  callous
        ILLEGAL_STRINGS.put("aliti","al")     ; //  formaliti      ->  formal
        ILLEGAL_STRINGS.put("iviti","ive")    ; //  sensitiviti    ->  sensitive
        ILLEGAL_STRINGS.put("biliti","ble")   ; //   sensibiliti    ->  sensible
    
    }
}
