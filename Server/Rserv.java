package Server;

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
    
    public void read_file(String srcPath) throws RserveException, REXPMismatchException {
        c.assign("src", srcPath);
        
        String extension = srcPath.substring(srcPath.length()-3, srcPath.length());
        
        // data reading        
        if( extension.equals("txt")) {
        	c.voidEval("data<-read.csv(src, stringsAsFactors = F, header = T)");
        } else{
        	c.voidEval("data<-read.table(src, stringsAsFactors = F, header = T)");
        }
    }    
    
    public void summary(String variable) throws RserveException, REXPMismatchException {
    		
    	   String var = "data$" + variable;
    	   c.voidEval("summary = summary(" + var + ")");
    	   
           double[] d = c.eval( "as.vector(summary)" ).asDoubles();

           for (int i = 0; i < d.length; i++) {
                   System.out.println(d[i]);
           }
    }

    public void linearModel(String response, String ...indep) throws REngineException, REXPMismatchException {
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
           double [] coeff = c.eval("coefficients(m)").asDoubles();
           
           for(i = 0 ; i < coeff.length; i++) {
        	   System.out.println(coeff[i]);
           }
    }
    
    public static void main(String[] args) throws REXPMismatchException, REngineException {

       String srcPath = "C:/prac/freshman.csv";
       Rserv Rserv = new Rserv();
       
       Rserv.read_file(srcPath);
       Rserv.linearModel("first", "year", "gender");
       Rserv.summary("year");
    }
}



//c.assign(response, response);
//
//for(i = 0 ; i < indep.length ; i ++) {
//  c.assign(indep[i], indep[i]);
//}
//String x = "x";
//String xarr[] = new String[indep.length];
//for(i = 0 ; i < indep.length; i ++) {
//  x = x + String.valueOf(i+1);
//  c.assign(indep[i], indep[i]);
//  xarr[i] = x;
//  x = x.substring(0, x.length() -1);
//}