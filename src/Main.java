import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Main {
	
	public static void writeResultFile(List<ClassifiedDocument> results, String outputPath) throws IOException
	{
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
		
		for(ClassifiedDocument res : results)
		{
			writer.write(res.docId + ", " + res.classificationLabel + ", " + res.label + "\n");
		}
		
		writer.close();
	}
	
    public static void main(String[] args) {
        try {           

        	Config config = new Config(args[0]);
            List<DocumentInstance> trainDocs = CsvParser.parse(config.trainFile);
            KNNClassifier classifier = new KNNClassifier(trainDocs);
            ArrayList<ClassifiedDocument> testResults = new ArrayList<ClassifiedDocument>();
            
            List<DocumentInstance> testDocs = CsvParser.parse(config.testFile);
        	for (int k = 1; k < 100; ++k) {
        		Map<Integer, Label> labelBenchmark = new HashMap<Integer, Label>();
        		System.out.println("Starting batch with k " + k);
                classifier.setK(k);
        		for (DocumentInstance testDoc: testDocs) {
        			ClassifiedDocument classification = classifier.classify(testDoc);
        			// ensure existence of labels in benchmark
        			if (!labelBenchmark.containsKey(testDoc.label)) {
        				labelBenchmark.put(testDoc.label, new Label(testDoc.label));
        			}
        			if (!labelBenchmark.containsKey(classification.classificationLabel)) {
        				labelBenchmark.put(classification.classificationLabel, new Label(classification.classificationLabel));
        			}
        			Label trueLabel = labelBenchmark.get(classification.label);
        			// document is counted in its true label anyway
        			trueLabel.totalDocs++;
        			if (classification.label == classification.classificationLabel) {
        				// if labels are equal, counted as a true positive in the true label
        				trueLabel.truePositive++;
        			} else {
        				// else, counted as a false positive in the classified label
        				Label classifiedLabel = labelBenchmark.get(classification.classificationLabel);
        				classifiedLabel.falsePositive++;
        			}
        			
        			testResults.add(classification);
        		}
        		
        		Double totalF = 0.0;
        		Double totalTP = 0.0;
        		Double totalFP = 0.0;
        		for (Map.Entry<Integer, Label> label: labelBenchmark.entrySet()) {
        			Label currentLabel = label.getValue();
        			totalF += currentLabel.harmonicMean();
        			totalTP += currentLabel.truePositive;
        			totalFP += currentLabel.falsePositive;
        			System.out.println(label.getValue().toString());
        			System.out.println("*********************");
        		}
        		
    			Double macro = totalF / (double)labelBenchmark.size();
    			System.out.println("macro: " + macro);
    			Double totalPrecision = Statistics.precision(totalTP, totalFP);
    			Double totalRecall = Statistics.recall(totalTP, (double)testDocs.size());
    			Double micro = Statistics.harmonicMean(totalPrecision, totalRecall);
    			System.out.println("micro: " + micro);
        		System.out.println("*********************************");
        		System.out.println("*********************************");
        	}
    		// TODO - calc optimal k
        	// TODO - write results to a file
        	writeResultFile(testResults, config.outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
