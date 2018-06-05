package src;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class Main {


    
    public static void main(String[] args) {
        try {           

            LuceneConfig config = new LuceneConfig(args[0]);
           
            IndexingEngine ie = new IndexingEngine(config.docsFile, config.mode);
            ie.run();
            
            SearchEngine se = new SearchEngine(config.queryFile, ie, ie.getCorpusTopTerms());
            TopDocs[] results = se.searchQueries();
            
            
            writeResults(config.outputFile, results);
            List<Result> truth = SearchResults.loadFromFile(config.truthFile);
            List<Result> ourResults = SearchResults.results(results, config.relevanceThreashold);
            
            SearchResults.compareSearchResults(ourResults, truth);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void writeResults(String fileName, TopDocs[] results) throws IOException {
        File outFile = new File(fileName);
        if (outFile.exists()) {
            outFile.delete();
        } else {
            outFile.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        Integer queryId = 1;
        for (TopDocs result: results) {
            String resultFormat = queryId.toString();
            for (ScoreDoc score: result.scoreDocs) {
                resultFormat += " " + Integer.toString(score.doc);          
            }
            writer.write(resultFormat);
            writer.newLine();
            ++queryId;
        }
        writer.close();
    }
}
