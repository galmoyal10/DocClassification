import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Main {
    public static void main(String[] args) {
        try {           

        	Config config = new Config(args[0]);
            List<DocumentInstance> trainDocs = CsvParser.parse(config.trainFile);
            KNNClassifier classifier = new KNNClassifier(trainDocs);
            ArrayList<ClassifiedDocument> testResults = new ArrayList<ClassifiedDocument>();
            classifier.setK(config.k);
            
            List<DocumentInstance> testDocs = CsvParser.parse(config.testFile);
            
            // a data structure for aggregating statistics on each label
    		Map<Integer, Label> labelBenchmark = new HashMap<Integer, Label>();
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
    			// document is counted in the total docs counter of its true label anyway    			
    			trueLabel.totalDocs++;
    			
    			// if labels are equal, counted as a true positive in the true label
    			if (classification.label == classification.classificationLabel) {
    				trueLabel.truePositive++;
    			} else {
    				// else, counted as a false positive in the classified label
    				Label classifiedLabel = labelBenchmark.get(classification.classificationLabel);
    				classifiedLabel.falsePositive++;
    			}
    			
    			testResults.add(classification);
    		}
    		
    		// after accumulating results, calculate macro and micro
    		Double totalF = 0.0;
    		Double totalTP = 0.0;
    		Double totalFP = 0.0;
    		for (Map.Entry<Integer, Label> label: labelBenchmark.entrySet()) {
    			Label currentLabel = label.getValue();
    			totalF += currentLabel.harmonicMean();
    			totalTP += currentLabel.truePositive;
    			totalFP += currentLabel.falsePositive;
    		}
    		
			Double macro = totalF / (double)labelBenchmark.size();
			Double totalPrecision = Statistics.precision(totalTP, totalFP);
			Double totalRecall = Statistics.recall(totalTP, (double)testDocs.size());
			Double micro = Statistics.harmonicMean(totalPrecision, totalRecall);
			
			System.out.println(macro + " " + micro + " " + ((macro + micro)/2));
    		writeResultFile(testResults, config.outputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void writeResultFile(List<ClassifiedDocument> results, String outputPath) throws IOException
	{
        File outFile = new File(outputPath);
        if (outFile.exists()) {
            outFile.delete();
        } else {
            outFile.createNewFile();
        }
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
		
		for(ClassifiedDocument res : results)
		{
			writer.write(res.docId + ", " + res.classificationLabel + ", " + res.label + "\n");
		}
		writer.close();
	}
}
