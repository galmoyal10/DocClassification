import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        try {           

            Config config = new Config(args[0]);
           
            IndexingEngine ie = new IndexingEngine(config.docsFile, config.basicMode);
            ie.run();
            
            SearchEngine se = new SearchEngine(config.queryFile, ie, ie.getCorpusTopTerms(), LuceneConstants.RELEVANCE_THRESHOLD);
            List<Result> results = se.searchQueries();
            
            
            writeResults(config.outputFile, results);
            List<Result> truth = SearchResults.loadFromFile(config.truthFile);
            
            SearchResults.compareSearchResults(results, truth);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void writeResults(String fileName, List<Result> results) throws IOException {
        File outFile = new File(fileName);
        if (outFile.exists()) {
            outFile.delete();
        } else {
            outFile.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        Integer queryId = 1;
        for (Result result: results) {
            String resultFormat = queryId.toString();
            for (Integer docId: result.documents) {
                resultFormat += " " + Integer.toString(docId);          
            }
            writer.write(resultFormat);
            writer.newLine();
            ++queryId;
        }
        writer.close();
    }
}
