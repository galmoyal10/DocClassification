public class Result {
	Result(Integer queryId, Integer[] documents) {
		this.queryId = queryId;
		this.documents = documents;
	}
	
	Integer queryId;
	Integer[] documents;
}
