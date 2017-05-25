package metaEvent;

import java.io.*;
import java.util.*;

public class fileReader {

	   public List<String> read_head(String srcPath){

	        String file = srcPath;
	        String line = "";
	        String extension = srcPath.substring(srcPath.length()-3, srcPath.length());
	        String splitBy;
	        
	        // data reading        
	        if( extension.equals("txt")) {
		         splitBy = " ";
	        } else{
		         splitBy = ",";
	        }
		   
			List<String> fileData = new ArrayList<String>();


	        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        		int cnt = 0;
	            while ((line = br.readLine()) != null  && cnt < 1) {
	            	
	                // use comma as separator
	                String[] data = line.split(splitBy);
	                
	                for(int i = 0 ; i < data.length; i++) {
		                // handle missing value
	                	if("".equals(data[i])) {
	                		data[i] = "0";
	                	}
	                	fileData.add( data[i] );
	                }
	                cnt++;
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	      return fileData;
	   }
	   
}