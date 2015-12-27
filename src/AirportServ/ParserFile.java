package AirportServ;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ParserFile {
	
	  // PRIVATE 
	  private final Path fFilePath;
	  private final static Charset ENCODING = StandardCharsets.UTF_8;  
	  private SimuParam param;
	 /**
	   Constructor.
	   @param aFileName full name of an existing, readable file.
	 * @throws IOException 
	  */
	  public ParserFile(String aFileName) throws IOException{
		setParam(new SimuParam());
	    fFilePath = Paths.get(aFileName);
	    processLineByLine();
	    
	  }
	  
	  
	  /** Template method that calls {@link #processLine(String)}.  */
	  public final void processLineByLine() throws IOException {
	    try (Scanner scanner =  new Scanner(fFilePath, ENCODING.name())){
	      while (scanner.hasNextLine()){
	        processLine(scanner.nextLine());
	      }      
	    }
	  }
	  
	  /**
	   * 
	   * @param aLine
	   */
	  protected void processLine(String aLine){
	    //use a second Scanner to parse the content of each line 
	    @SuppressWarnings("resource")
		Scanner scanner = new Scanner(aLine);
	    scanner.useDelimiter("=");
	    if (aLine.charAt(0) == '#' || aLine.length()<4)
	    	return;
	    if (scanner.hasNext()){
	      //assumes the line has a certain structure
	      String name = scanner.next();
	      String value = scanner.next();
	      log("Name is : " + quote(name.trim()) + ", and Value is : " + quote(value.trim()));
	      param.procces(name.trim(), value.trim());
	    }
	    else {
	      log("Empty or invalid line. Unable to process.");
	    }
	    //scanner.close();
	  }
	  

	  
	  private static void log(Object aObject){
	    System.out.println(String.valueOf(aObject));
	  }
	  
	  private String quote(String aText){
	    String QUOTE = "'";
	    return QUOTE + aText + QUOTE;
	  }


	public SimuParam getParam() {
		return param;
	}


	public void setParam(SimuParam param) {
		this.param = param;
	}
}
