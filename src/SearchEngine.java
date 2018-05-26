import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class SearchEngine {
	SearchEngine(String queryFile, String docsFile, String outputFile, boolean mode) throws IOException {
		this.parseQueriesFile(queryFile);
		this._docsFile = docsFile;
		this._outputFile = outputFile;
		this._mode = mode;		
	}
	
	public void run() throws IOException {
	}

	private void parseQueriesFile(String queriesFilePath) throws IOException {
		String queriesString = new String(Files.readAllBytes(Paths.get(queriesFilePath)));
		String[] queries = queriesString.split(QUERY_REGEX);
		queries = Arrays.copyOfRange(queries, 1, queries.length);
		this._queries = new String[queries.length][];
		for (int i = 0; i < queries.length; i++) {
			this._queries[i] = queries[i].split("\\s");
		}
	}
	
    private String[][] _queries;
    private String _docsFile;
    private String _outputFile;
    private boolean _mode;

    private static final String QUERY_REGEX = "\\*FIND\\s*\\d+.*";
}
