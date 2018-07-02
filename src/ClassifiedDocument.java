
public class ClassifiedDocument extends DocumentInstance {

	public int classificationLabel;
	
	public ClassifiedDocument(int docId, int truelabel, String title, String content, int classifiedLabel) {
		super(docId, truelabel, title, content);
		this.classificationLabel = classifiedLabel;
	}
	
	public ClassifiedDocument(DocumentInstance doc, int classifiedLabel) {
		super(doc);
		this.classificationLabel = classifiedLabel;
	}

}
