package coreProcess;

import java.util.*;
import java.io.*;
import java.io.File;


/*
 * This class reads a graph from a text file and puts it into a 2d arrayList which can be accessed with a get method.
 * It takes a file name (including file path) as a parameter.
 */
public class GraphImporter{
	
	private List<List<Integer>> matrix ;
	private String path;

	
	public GraphImporter(String path){
		//TODO: add useful exception handling
		this.path=path;
		try{
			List<String> stringList = readFile();
			matrix = string2Num(stringList);
		}catch (IOException e){
			
		}
				
	}
	
	public GraphImporter(File file){
		//TODO: add useful exception handling
		try{
			List<String> stringList = readFile2(file);
			matrix = string2Num(stringList);
		}catch (IOException e){
	
		}
		
	}
	
	
	private List<String> readFile2(File file) throws IOException{
		FileReader fr = new FileReader(file);
		BufferedReader reader = new BufferedReader(fr);
	
	    String currentLine;
	    
	    List<String> result = new ArrayList<String>();
	    while((currentLine = reader.readLine())!=null){
	    	result.add(currentLine);
	    }
	    
	    reader.close();
	    return result;
	}
	/*
	 * Reads the matrix from the file into a list where each entry is a string representation of 1 line of the matrix.
	 */
	private List<String> readFile() throws IOException{
		FileReader fr = new FileReader(path);
		BufferedReader reader = new BufferedReader(fr);
	
	    String currentLine;
	    
	    List<String> result = new ArrayList<String>();
	    while((currentLine = reader.readLine())!=null){
	    	result.add(currentLine);
	    }
	    
	    reader.close();
	    return result;
	}

	/*
	 * Converts the string representation from readFile() into an Integer and separates the lines into separate values
	 */
	private List<List<Integer>> string2Num(List<String> stringList){
		List<List<Integer>> resMatrix = new ArrayList<List<Integer>>();
		
		
		for(String item:stringList){
			
			List<Integer> numList = new ArrayList<Integer>();
			String[] currentNums = item.split("\\s");
			for(String s:currentNums){
				numList.add(Integer.valueOf(s));
			}
			resMatrix.add(numList);
			
		}
		return resMatrix;
	}
	
	
	public List<List<Integer>> getMatrix(){
		return matrix;
	}
	
}