import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.queryparser.classic.ParseException;

/**
 * knn classifier impl
 */
public class KNNClassifier {
	/**
	 * c'tor
	 * @param trainingSet - training set to initialize classifier with
	 * @throws Exception
	 */
	KNNClassifier(List<DocumentInstance> trainingSet) throws Exception {
		_trainingSet = trainingSet;
		_index = new IndexingEngine(trainingSet);
		_index.run();
		_neighborsRetriever = new SearchEngine(_index);
	}
	
	/**
	 * sets the k parameter of the KNN classifier
	 * @param k
	 */
	public void setK(Integer k) {
		this._neighborsRetriever.setK(k);
	}
	
	/**
	 * classifies a single document
	 * @param testDoc - doc to classify
	 * @return the classification result
	 * @throws Exception
	 * @throws ParseException
	 */
	ClassifiedDocument classify(DocumentInstance testDoc) throws Exception, ParseException {
		Result neighbors = this._neighborsRetriever.searchDoc(testDoc);
		Map<Integer, Integer> possibleClasses = new HashMap<Integer, Integer>();
		// going thru neighbors of document and counting their labels
		for (Integer docId: neighbors.documents) 
		{
			Integer possibleClassification = this._trainingSet.get(docId).label;
			Integer classificationCount = possibleClasses.getOrDefault(possibleClassification, 0);
			possibleClasses.put(possibleClassification, ++classificationCount);
        }
		
		// searching for label with the most neighbors
		int maxClassificationCount = 0;
		int classification = 0;
		for (Map.Entry<Integer, Integer> possibleClassification : possibleClasses.entrySet()) {
			if (possibleClassification.getValue() > maxClassificationCount) {
				maxClassificationCount = possibleClassification.getValue();
				classification = possibleClassification.getKey();
			}
		}
		return new ClassifiedDocument(testDoc, classification);
	}
	
	private List<DocumentInstance> _trainingSet;
	private SearchEngine _neighborsRetriever;
	private IndexingEngine _index;
}
