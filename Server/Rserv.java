package Server;

import java.io.*;
import java.util.*;
import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.*;

public class Rserv {
	 
    public RConnection c = null;

    public Rserv() throws RserveException {
           c = new RConnection();
    }
       
    public void getRVersion() throws RserveException, REXPMismatchException {
           REXP x = c.eval("R.version.string");
           System.out.println("R version : " + x.asString());
    }
    
    public void save_file(String destPath, String Unique, String fileName, List<String> ...line) {
    	
    	String fname = destPath + Unique + "-" + fileName;
    	File f = new File(fname);
    	try{
    		f.createNewFile();
    		
    		FileWriter fr = new FileWriter(f);
    		
    		for(int i = 0 ; i < line.length; i ++) {
    			for(int j = 0 ; j < line[i].size(); j ++) {
    				fr.write(line[i].get(j));
    				fr.write(" ");
    			}
    			fr.write("\n");
    		}
    		fr.close();
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    	System.out.println( fname + " Correctly Saved!");
    }
    
    public void read_file(String srcPath) throws RserveException, REXPMismatchException {
        c.assign("src", srcPath);
        
        String extension = srcPath.substring(srcPath.length()-3, srcPath.length());
        
        // data reading        
        if( extension.equals("txt")) {
        	c.voidEval("data<-read.table(src, stringsAsFactors = F, header = T)");
        } else{
        	c.voidEval("data<-read.csv(src, stringsAsFactors = F, header = T)");
        }
    }    
    
    public void summary(String destPath, String variable) throws RserveException, REXPMismatchException {
    		
    	   String var = "data$" + variable;
    	   c.voidEval("summary = summary(" + var + ")");
    	   
    	   String[] value = c.eval( "as.vector(summary)" ).asStrings();
           String[] names = c.eval( "names(summary)").asStrings();
		   List<String> valueList = new ArrayList<String>(Arrays.asList(value));
		   List<String> namesList = new ArrayList<String>(Arrays.asList(names));
		   save_file(destPath,variable, "vectorSummary.txt", namesList, valueList);
    }

    public void linearModel(String destPath, String response, String ...indep) throws REngineException, REXPMismatchException {
    	   int i;
    	              
           // model construct
           String modelPart1 = "m <- lm(";
           String modelPart2 = response;
           String modelPart3 = "~";
           
           for(i = 0 ; i < indep.length; i++) {
        	   if(i == indep.length - 1) {
        		   modelPart3 = modelPart3 + indep[i];
        	   }
        	   else {
        		   modelPart3 = modelPart3 + indep[i] + "+";   
        	   }
           }
           String modelPart4 = ", data = data )";
           
           String modelStr = modelPart1 + modelPart2 + modelPart3 + modelPart4; 

           // model show
           c.voidEval(modelStr);
    	   String[] value = c.eval( "as.vector(coefficients(m))" ).asStrings();
           String[] names = c.eval( "names(coefficients(m))").asStrings();
		   List<String> valueList = new ArrayList<String>(Arrays.asList(value));
		   List<String> namesList = new ArrayList<String>(Arrays.asList(names));

		   save_file(destPath, response, "linearModel.txt",namesList, valueList);
    }
    
    public void summary_plot(String destPath, String variable, int select) throws REXPMismatchException, REngineException {
        String saveFileName, png, ggplot;
        
        if(select == 1){
           saveFileName = destPath + variable + "_hist.png";
              png = "png(file = \"" + saveFileName + "\")";
              ggplot = "print(ggplot(data=data, aes(x=" + variable + ") ) + "+ "geom_histogram()); dev.off()";
        }
        
        else{
           saveFileName = destPath + variable + "_boxplot.png";
           png = "png(file = \"" + saveFileName + "\")";
           ggplot = "print(ggplot(data=data, aes(x= 1, y =" + variable + ") ) + "+ "geom_boxplot() ); dev.off()";
        }
        
        png = png.replaceAll("\\\\", "/");
         c.eval("library(ggplot2)");
         c.eval("require(ggplot2)");
         c.eval(png);
         c.parseAndEval(ggplot); 
      }
    
    
    // 변수 1개일때 그림 표시
	  public void lm_plot(String destPath, String response, String ...indep) throws REXPMismatchException, REngineException {
			
		   String jpegFile = destPath + response + "-" + indep[0] + "_linearmodel.png";
		   String png = "png(file = \"" + jpegFile + "\")";
//		   String plot = "plot(" + indep[0] + "," + response + ", main = \"Scatter plot of linear model with one variable \") ";
		   String ggplot = "print(ggplot(data=data, aes(x=" + indep[0] + ",y=" + response + ") ) + "
		   		+ "geom_point(shape = 1) + geom_abline(intercept = coef(m)[1], slope = coef(m)[2], col = 'red' ) ); dev.off()";
		   		   
		   png = png.replaceAll("\\\\", "/");
           c.eval("library(ggplot2)");
           c.eval("require(ggplot2)");
		   c.eval(png);
		   c.parseAndEval(ggplot);
	  }
	  
}