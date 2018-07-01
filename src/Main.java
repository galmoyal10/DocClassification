import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        try {           

        	Config config = new Config("C:\\Users\\GALMOYAL\\Documents\\DocClassification\\Externals\\parameters.txt");
            List<DocumentInstance> trainDocs = CsvParser.parse(config.trainFile);
            
            IndexingEngine ie = IndexFactory.getIndex(false, trainDocs);
            ie.run();
            
            List<DocumentInstance> testDocs = CsvParser.parse(config.testFile);
    		SearchEngine se = SearchEngineFactory.getSE(false, testDocs, ie, ie.getCorpusTopTerms());
    		List<Result> results = se.searchQueries();
    		
    		ArrayList<ClassifiedDocument> resultDocs = new ArrayList<ClassifiedDocument>();
    		
    		for(Result result : results)
    		{
    			int []classHist = new int[256];
    			for (Integer docId: result.documents) 
    			{
    				// TODO: make this more efficient.
    				Optional<DocumentInstance> doc = trainDocs.stream().filter(item->item.docId==docId).findFirst();
    				if(!doc.isPresent())
    				{
    					System.out.println("WTFFF?!?!");
    				}
                    classHist[doc.get().docId]++;
                }
    			
    			//Collections.max((Collection<? extends T>) Arrays.asList(classHist));
    		}
    		/*
    		writeResults(config.outputFile, results);
    		if (config.truthFile != null) {    			
    			List<Result> truth = SearchResults.loadFromFile(config.truthFile);
    			double[] res = SearchResults.compareSearchResults(results, truth);
    		}*/
    		
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
