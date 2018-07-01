import java.util.List;

public class KNNClassifier {
	KNNClassifier(List<DocumentInstance> trainingSet) {
		_trainingSet = trainingSet;
	}
	
	String classify(DocumentInstance testDoc) {
		//TODO
		return "fuck";
	}
	
	private List<DocumentInstance> _trainingSet;
}
