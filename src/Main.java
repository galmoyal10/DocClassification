import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        try {           

        	Config config = new Config(args[0]);
            List<DocumentInstance> trainDocs = CsvParser.parse(config.trainFile);
            Integer k = 6;
            KNNClassifier classifier = new KNNClassifier(trainDocs, k);
            
            List<DocumentInstance> testDocs = CsvParser.parse(config.testFile);
            List<ClassifiedDocument> classifications = new ArrayList<ClassifiedDocument>();
    		for (DocumentInstance testDoc: testDocs) {
    			classifications.add(classifier.classify(testDoc));
    		}
    		// TODO - calc optimal k
    		// TODO - calc micro and macro
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
