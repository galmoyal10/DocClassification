import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;

public class KNNClassifier {
	KNNClassifier(List<DocumentInstance> trainingSet) throws Exception {
		_trainingSet = trainingSet;
		_index = new IndexingEngine(trainingSet);
		_index.run();
		_neighborsRetriever = new SearchEngine(_index, LuceneConstants.BASIC_RELEVANCE_THRESHOLD);
	}
	
	ClassifiedDocument classify(DocumentInstance testDoc) throws Exception, ParseException {
		Result neighbors = this._neighborsRetriever.searchDoc(testDoc);
		Map<Integer, Integer> possibleClasses = new HashMap<Integer, Integer>();
		for (Integer docId: neighbors.documents) 
		{
			Integer possibleClassification = this._trainingSet.get(docId).label;
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
		System.out.println("docID" + testDoc.docId + " classified " + classification + " with " + maxClassificationCount + " neighbours");
		System.out.println("docID true label " + testDoc.label);
		return new ClassifiedDocument(testDoc, classification);
	}
	
	private List<DocumentInstance> _trainingSet;
	private SearchEngine _neighborsRetriever;
	private IndexingEngine _index;
}
