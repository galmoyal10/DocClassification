import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Config {

	/**
	 * Parses configuration file, extracting all necessary parameters for the search engine
	 * @param configPath - path to configuration file
	 * @throws IOException
	 */
    Config(String configPath) throws IOException
    {
    	FileReader fileReader = new FileReader(configPath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuffer stringBuffer = new StringBuffer();
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
                	// true == basic, false == improved
                    basicMode = tokens[1].compareTo(BASIC) == 0 ? true : false;
                    break;
                // Extra optional parameter added by us - truth file path
                // When given, the program calculates search engine performance
                case TRUTH_FILE_FIELD:
                	truthFile = tokens[1];
                	break;
            }
            stringBuffer.append(line);
            stringBuffer.append("\n");
        }
        bufferedReader.close();

    }
    
    public String queryFile = null;
    public String docsFile = null;
    public String outputFile = null;
    public String truthFile = null;
    public boolean basicMode = false;    

    
    private static final String QUERY_FILE_FIELD = "queryFile";
    private static final String DOCS_FILE_FIELD = "docsFile";
    private static final String OUT_FILE_FIELD = "outputFile";
    private static final String MODE_FIELD = "retrievalAlgorithm";
    private static final String TRUTH_FILE_FIELD = "truthFile";

    private static final String BASIC = "basic";
}
