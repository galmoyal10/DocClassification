package src;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class Main {


    
    public static void main(String[] args) {

    	double testResults [] = new double [25*4];
    	for(double i=0; i<25; i+=0.25)
    	{
	    	try {           
	
	            LuceneConfig config = new LuceneConfig(args[0]);
	            config.relevanceThreashold = i;
	            
	            IndexingEngine ie = new IndexingEngine(config.docsFile, config.basicMode);
	            ie.run();
	            
	            SearchEngine se = new SearchEngine(config.queryFile, ie, ie.getCorpusTopTerms());
	            TopDocs[] results = se.searchQueries();
	            
	            
	            writeResults(config.outputFile, results);
	            List<Result> truth = SearchResults.loadFromFile(config.truthFile);
	            List<Result> ourResults = SearchResults.results(results, config.relevanceThreashold);
	            
	            testResults[(int) (i*4)]=SearchResults.compareSearchResults(ourResults, truth);
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
    	}
    	
    	for(double i=0; i<25; i+=0.25)
    	{
    		System.out.println(testResults[(int) (i*4)]);
    	}
    	
    	double bestT= getIndexOfLargest(testResults)/4.0;
    	System.out.println("Best T is " + bestT);
    }
    
    private static int getIndexOfLargest( double[] array )
    {
      if ( array == null || array.length == 0 ) return -1; // null or empty

      int largest = 0;
      for ( int i = 1; i < array.length; i++ )
      {
          if ( array[i] > array[largest] ) largest = i;
      }
      return largest; // position of the first largest found
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
