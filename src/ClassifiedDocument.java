
/**
 * A convenience representation of a single document classification
 */
public class ClassifiedDocument extends DocumentInstance {

	public int classificationLabel;
	
	/**
	 * c'tor
	 * @param docId	- document id
	 * @param truelabel	- actual label of document
	 * @param content - docuemnt's content
	 * @param classifiedLabel - label assigned by classifier
	 */
	public ClassifiedDocument(int docId, int truelabel, String content, int classifiedLabel) {
		super(docId, truelabel, content);
		this.classificationLabel = classifiedLabel;
	}
	
	/**
	 * c'tor
	 * @param doc - document instance to initialize from
	 * @param classifiedLabel - label assigned by classifier
	 */
	public ClassifiedDocument(DocumentInstance doc, int classifiedLabel) {
		super(doc);
		this.classificationLabel = classifiedLabel;
	}

}
