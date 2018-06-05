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
    
    public static String normalizeString(String document) throws IOException 
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
        	document = this.normalizeString(document);
            
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
    	
        ILLEGAL_STRINGS.put("ational\\b","ate")  ; //    relational     ->  relate
        ILLEGAL_STRINGS.put("tional\\b","tioN"); //     conditional    ->  condition
        ILLEGAL_STRINGS.put("enci\\b","ence")    ; // valenci        ->  valence
        ILLEGAL_STRINGS.put("anci\\b","ance")    ; // hesitanci      ->  hesitance
        ILLEGAL_STRINGS.put("izer\\b","ize")     ; // digitizer      ->  digitize
        ILLEGAL_STRINGS.put("abli\\b","able")    ; // conformabli    ->  conformable
        ILLEGAL_STRINGS.put("alli\\b","al")      ; // radicalli      ->  radical
        ILLEGAL_STRINGS.put("entli\\b","ent")    ; //  differentli    ->  different
        ILLEGAL_STRINGS.put("eli\\b","e")        ; //vileli        - >  vile
        ILLEGAL_STRINGS.put("ousli\\b","ous")    ; //  analogousli    ->  analogous
        ILLEGAL_STRINGS.put("ization\\b","ize")  ; //    vietnamization ->  vietnamize
        ILLEGAL_STRINGS.put("ation\\b","ate")    ; //  predication    ->  predicate
        ILLEGAL_STRINGS.put("ator\\b","ate")     ; // operator       ->  operate
        ILLEGAL_STRINGS.put("alism\\b","al")     ; //  feudalism      ->  feudal
        ILLEGAL_STRINGS.put("iveness\\b","ive")  ; //    decisiveness   ->  decisive
        ILLEGAL_STRINGS.put("fulness\\b","ful")  ; //    hopefulness    ->  hopeful
        ILLEGAL_STRINGS.put("ousness\\b","ous")  ; //    callousness    ->  callous
        ILLEGAL_STRINGS.put("aliti\\b","al")     ; //  formaliti      ->  formal
        ILLEGAL_STRINGS.put("iviti\\b","ive")    ; //  sensitiviti    ->  sensitive
        ILLEGAL_STRINGS.put("biliti\\b","ble")   ; //   sensibiliti    ->  sensible
        
        
        ILLEGAL_STRINGS.put("icate","ic");		    //		triplicate		->		triplic
        ILLEGAL_STRINGS.put("ative","");				    //		formative		->		form
        ILLEGAL_STRINGS.put("alize","al");		    //		formalize		->		formal
        ILLEGAL_STRINGS.put("iciti","ic");		    //		electriciti		->		electric
        ILLEGAL_STRINGS.put("ical","ic");		    //		electrical		->		electric
        ILLEGAL_STRINGS.put("ful","");			    	//	hopeful		->		hope
        ILLEGAL_STRINGS.put("ness","");			    //		goodness		->		good
        ILLEGAL_STRINGS.put("al","");		    		//	revival		->		reviv
        ILLEGAL_STRINGS.put("ance","");			    	//	allowance		->		allow
        ILLEGAL_STRINGS.put("ence","");			    	//	inference		->		infer
        ILLEGAL_STRINGS.put("er","");			    		//	airliner		->		airlin
        ILLEGAL_STRINGS.put("ic","");			    		//	gyroscopic		->		gyroscop
        ILLEGAL_STRINGS.put("able","");			    	//	adjustable		->		adjust
        ILLEGAL_STRINGS.put("ible","");			    	//	defensible		->		defens
        ILLEGAL_STRINGS.put("ant","");			    	//	irritant		->		irrit
        ILLEGAL_STRINGS.put("ement","");			    	//	replacement		->		replac
        ILLEGAL_STRINGS.put("ment","");			    	//	adjustment		->		adjust
        ILLEGAL_STRINGS.put("ent","");			    	//	dependent		->		depend
        ILLEGAL_STRINGS.put("ion","");		    		//	adoption		->		adopt
        ILLEGAL_STRINGS.put("ou","");		    		//	homologou		->		homolog
        ILLEGAL_STRINGS.put("ism","");			    	//	communism		->		commun
        ILLEGAL_STRINGS.put("ate","");			    	//	activate		->		activ
        ILLEGAL_STRINGS.put("iti","");			    	//	angulariti		->		angular
        ILLEGAL_STRINGS.put("ous","");			    	//	homologous		->		homolog
        ILLEGAL_STRINGS.put("ive","");			    	//	effective		->		effect
        ILLEGAL_STRINGS.put("ize","");			    	//	bowdlerize		->		bowdler
    
    }
}
