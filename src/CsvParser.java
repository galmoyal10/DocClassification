
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {

	/**
	 * parses a CSV file
	 * @param filePath - path to file to parse
	 * @return a list of all rows in the csv file
	 * @throws IOException
	 */
    public static List<DocumentInstance> parse(String filePath) throws IOException {

    	// Parse CSV to String[]
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        ArrayList<String[]> rows = new ArrayList<String[]>();
        try {
            br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null) {
                rows.add(line.split(cvsSplitBy));
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        
        // Parse String[] to docs
        List<DocumentInstance> processedTrainDocuments = new ArrayList<DocumentInstance>();
        Integer c = 0;
        for (String[] row: rows) {
        	try {
        		c++;
        		processedTrainDocuments.add(new DocumentInstance(Integer.parseInt(row[Config.DOC_ID_FIELD])
	        			, Integer.parseInt(row[Config.LABEL_FIELD])
						, row[Config.TITLE_FIELD],
						row[Config.CONTENT_FIELD]));	
        	} catch (Exception e) {
        		System.out.println(row[0]);
        		
        	}
        	
        }
        
        return processedTrainDocuments;
    }

}