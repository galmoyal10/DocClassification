package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class LuceneConfig {

	String queryFile = null;
    String docsFile = null;
    String outputFile = null;
    String truthFile = null;
    boolean mode = false;
    double relevanceThreashold = 10;
    
    
    LuceneConfig(String configPath) throws IOException
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
                    mode = tokens[1].compareTo(BASIC) == 0 ? true : false;
                    break;
                case TRUTH_FILE_FIELD:
                	truthFile = tokens[1];
                	break;
            }
            stringBuffer.append(line);
            stringBuffer.append("\n");
        }
        bufferedReader.close();

    }
    
    
    private static final String QUERY_FILE_FIELD = "queryFile";
    private static final String DOCS_FILE_FIELD = "docsFile";
    private static final String OUT_FILE_FIELD = "outputFile";
    private static final String MODE_FIELD = "retrievalAlgorithm";
    private static final String TRUTH_FILE_FIELD = "truthFile";

    private static final String BASIC = "basic";
}
