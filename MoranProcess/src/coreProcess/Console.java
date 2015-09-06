package coreProcess;

import java.io.FileWriter;
import java.io.Writer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Console {

	
	
	public static void main(String args[]){
		resultGen(args);
	}
	
	public static void resultGen(String args[]){
		
		int iterations;
		int graphNumber;
		int vertFloor;
		int vertLimit;
		//if(args.length==0){			
			iterations = 1250;
			graphNumber = 40;
			vertFloor = 0;
			vertLimit = 10;
						
		//}else{
			/*vertFloor = Integer.parseInt(args[0]);
			vertLimit = Integer.parseInt(args[1]);
			iterations = Integer.parseInt(args[2]);
			graphNumber = Integer.parseInt(args[3]);*/
		//}	
		
		Boolean isDirected = false;
		int mutantFitness = 3;
		
		int numThreads = 4;
		Investigator[] invArray = new Investigator[numThreads];
		ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
		Future[] futureArray = new Future[numThreads];
		
		//Assign the tasks to the threads
		for(int i=0;i<numThreads;i++){
			//basic EGT
			//invArray[i] = new Investigator(vertLimit,iterations,graphNumber/numThreads,isDirected,mutantFitness,vertFloor);
			//environmental
			invArray[i] = new Investigator(vertLimit,iterations,graphNumber/numThreads,isDirected,mutantFitness,0.6,1,vertFloor);
			//multi-mutant
			//invArray[i] = new Investigator(vertLimit,iterations,graphNumber/numThreads,isDirected,mutantFitness,5,false,vertFloor);
			futureArray[i] = threadPool.submit(invArray[i]);
		}
		
		threadPool.shutdown();
		
		//Wait till all the threads are finished, note this could cause the application to hang for the user if the threads deadlock
		try{
			for(Future future: futureArray){
				future.get();
			}
			//threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}catch(ExecutionException except){					
			System.out.println(except);
			except.printStackTrace();
		}catch(InterruptedException ex){
			System.out.println(ex);
		}
		
		//The next two blocks average the results of the different threads into 1 array
		double[] meanArray = new double[vertLimit];
		double[] meanError = new double[vertLimit];
		double[] fixProbArray = new double[vertLimit];
		double[] fixProbError = new double[vertLimit];
		
		for(int x=0;x<vertLimit;x++){
			for(Investigator i:invArray){
				meanArray[x] += i.getMeanArray()[x];
				meanError[x] += Math.pow(i.getMeanError()[x], 2);
				fixProbArray[x] += i.getFixProbArray()[x];
				fixProbError[x] += Math.pow(i.getFixProbError()[x], 2);
			}
			meanArray[x] = meanArray[x]/numThreads;
			fixProbArray[x] = fixProbArray[x]/numThreads;
			
			meanError[x] = Math.sqrt(meanError[x]);
			fixProbError[x] = Math.sqrt(fixProbError[x]);
		}

		try (Writer writer = new FileWriter("/home/dave/simResults.txt")) {
		//try (Writer writer = new FileWriter("C:/Users/David/Documents/simResults.txt")) {	
			//String nl = System.getProperty("line.separator");
			String nl = "\r\n";
			for(double val:meanArray){
				writer.write(String.format("%.3f", val) + " ");
			}
			writer.write(nl);
			for(double val:meanError){
				writer.write(String.format("%.3f", val) + " ");
			}
			writer.write(nl);
			writer.write(nl);
			for(double val:fixProbArray){
				writer.write(String.format("%.3f", val) + " ");
			}
			writer.write(nl);
			for(double val:fixProbError){
				writer.write(String.format("%.3f", val) + " ");
			}
			
		}catch(Exception e){
			
		}
	}
}
