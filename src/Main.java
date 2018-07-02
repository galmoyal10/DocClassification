import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {
    public static void main(String[] args) {
        try {           

        	Config config = new Config(args[0]);
            List<DocumentInstance> trainDocs = CsvParser.parse(config.trainFile);
            
            IndexingEngine ie = IndexFactory.getIndex(false, trainDocs);
            ie.run();
            
            List<DocumentInstance> testDocs = CsvParser.parse(config.testFile);
    		SearchEngine se = new SearchEngine(testDocs, ie, LuceneConstants.BASIC_RELEVANCE_THRESHOLD);
    		List<Result> results = se.searchQueries();
    		
    		ArrayList<ClassifiedDocument> classifications = new ArrayList<ClassifiedDocument>();
    		
    		
    		//TODO - move this shit to KNNClassifier query one document at a time
    		for(Result result : results)
    		{
    			// filling a map of class to the the number of neighbors that match it
    			Map<Integer, Integer> possibleClasses = new HashMap<Integer, Integer>();
    			for (Integer docId: result.documents) 
    			{
    				Integer possibleClassification = trainDocs.get(docId).label;
    				Integer classificationCount = possibleClasses.getOrDefault(possibleClassification, 0);
    				possibleClasses.put(possibleClassification, ++classificationCount);
                }
    			int maxClassificationCount = 0;
    			int classification = 0;
    			for (Map.Entry<Integer, Integer> possibleClassification : possibleClasses.entrySet()) {
    				if (possibleClassification.getValue() > maxClassificationCount) {
    					maxClassificationCount = possibleClassification.getValue();
    					classification = possibleClassification.getKey();
    				}
    			}
    			Integer testDocId = result.queryId;
    			System.out.println("docID" + testDocId + " classified " + classification + " with " + maxClassificationCount + " neighbours");
    			System.out.println("docID true label " + testDocs.get(testDocId).label);
    			classifications.add(new ClassifiedDocument(testDocs.get(testDocId), classification));
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
