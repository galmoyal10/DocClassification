import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Config {

	/**
	 * Parses configuration file, extracting all necessary parameters for the classifier
	 * @param configPath - path to configuration file
	 * @throws IOException
	 */
	Config(String configPath) throws IOException
    {
    	FileReader fileReader = new FileReader(configPath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
        	if (line.trim().length() == 0) {
        		continue;
        	}
            String[] tokens = line.split("=");
            String value = tokens[1].trim();
            switch (tokens[0]) {
                case TRAIN_FILE_FIELD:
                	trainFile = value;
                    break;
                case TEST_FILE_FIELD:
                	testFile = value;
                    break;
                case OUT_FILE_FIELD:
                    outputFile = value;
                    break;
                case K_FIELD:
                    k = Integer.parseInt(value);
                    break;
            }
        }
        bufferedReader.close();

    }
    
    public String trainFile = null;
    public String testFile = null;
    public String outputFile = null;
    public Integer k = null;

    
    private static final String TRAIN_FILE_FIELD = "trainFile";
    private static final String TEST_FILE_FIELD = "testFile";
    private static final String OUT_FILE_FIELD = "outputFile";
    private static final String K_FIELD = "k";
    public static final Integer DOC_ID_FIELD = 0;
    public static final Integer LABEL_FIELD = 1;
    public static final Integer TITLE_FIELD = 2;
    public static final Integer CONTENT_FIELD = 3;

}
