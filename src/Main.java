import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Main {

	private static final String QUERY_FILE_FIELD = "queryFile";
	private static final String DOCS_FILE_FIELD = "docsFile";
	private static final String OUT_FILE_FIELD = "outputFile";
	private static final String MODE_FIELD = "retrievalAlgorithm";

	private static final String BASIC = "basic";
	
    public static void main(String[] args) {
    	try {    		
    		String paramFileName = args[0];
    		File paramFile = new File(paramFileName);
    		FileReader fileReader = new FileReader(paramFile);
    		BufferedReader bufferedReader = new BufferedReader(fileReader);
    		StringBuffer stringBuffer = new StringBuffer();
    		
    		String queryFile = null;
    		String docsFile = null;
    		String outputFile = null;
    		boolean mode = false;
    		
    		String line;
    		while ((line = bufferedReader.readLine()) != null) {
    			String[] tokens = line.split("=");
    			switch (tokens[0]) {
    				case QUERY_FILE_FIELD:
    					queryFile = tokens[1];
    					break;
    				case DOCS_FILE_FIELD:
    					docsFile = tokens[1];
    					break;
    				case OUT_FILE_FIELD:
    					outputFile = tokens[1];
    					break;
    				case MODE_FIELD:
    					mode = tokens[1].compareTo(BASIC) == 0 ? true : false;
    					break;
    			}
    			stringBuffer.append(line);
    			stringBuffer.append("\n");
    		}
    		fileReader.close();

    		SearchEngine se = new SearchEngine(queryFile, docsFile, outputFile, mode);
    		se.run();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}
