import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {
        try {           

        	Config config = new Config(args[0]);
            List<DocumentInstance> trainDocs = CsvParser.parse(config.trainFile);
            KNNClassifier classifier = new KNNClassifier(trainDocs);
            
            List<DocumentInstance> testDocs = CsvParser.parse(config.testFile);
            List<ClassifiedDocument> classifications = new ArrayList<ClassifiedDocument>();
    		for (DocumentInstance testDoc: testDocs) {
    			classifications.add(classifier.classify(testDoc));
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
