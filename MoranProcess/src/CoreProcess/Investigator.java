package coreProcess;

import java.util.*;
import java.util.stream.*;

/*
 * This class is used to perform an 'investigation'. This involves repeatedly generating random graphs with vertex numbers increasing up to vertLimit. Each graph then has 
 * a simulation performed on it and the results are collected to find the mean results for each value of the number of vertices.
 */
public class Investigator implements Runnable{
	
	private int vertLimit;
	private int iterations;
	private int graphNumber; 
	private Boolean isDirected; 
	private int mutantFitness; 
	private int vertFloor;
	private SimulationType simType;
	private EnvGraph graph;
	private double redDensity;
	private int numRed;
	int m1;
	
	private List<List<Integer>> matrix;
	private double[] meanArray;
	private double[] fixProbArray;
	private double[] meanError;
	private double[] fixProbError;
	

	public Investigator(int vertLimit,int iterations,int graphNumber,Boolean isDirected,int mutantFitness,int... vertFloor){
		this.vertLimit = vertLimit;
		this.iterations = iterations;
		this.graphNumber = graphNumber;
		this.isDirected = isDirected;
		this.mutantFitness = mutantFitness;
		if(vertFloor[0]!=0){
			this.vertFloor = vertFloor[0];
		}else{
			this.vertFloor = 0;
		}
		simType = SimulationType.MORAN;
		
		meanArray = new double[vertLimit];
		meanArray[0] = 0; meanArray[1] = 0; 
		
		meanError = new double[vertLimit];
		meanError[0] = 0; meanError[1] = 0;
		
		fixProbArray = new double[vertLimit];
		fixProbArray[0] = 0; fixProbArray[1] = 1;
		
		fixProbError = new double[vertLimit];
		fixProbError[0] = 0; fixProbError[1] = 0;
	}
	
	public Investigator(int vertLimit,int iterations,int graphNumber,Boolean isDirected,int mutantFitness,double redDensity,int numRed,int... vertFloor){
		this.vertLimit = vertLimit;
		this.iterations = iterations;
		this.graphNumber = graphNumber;
		this.isDirected = isDirected;
		this.mutantFitness = mutantFitness;
		if(vertFloor[0]!=0){
			this.vertFloor = vertFloor[0];
		}else{
			this.vertFloor = 0;
		}
		this.redDensity = redDensity;
		this.numRed = numRed;
		simType = SimulationType.ENVIRONMENTAL;
		
		meanArray = new double[vertLimit];
		meanArray[0] = 0; meanArray[1] = 0; 
		
		meanError = new double[vertLimit];
		meanError[0] = 0; meanError[1] = 0;
		
		fixProbArray = new double[vertLimit];
		fixProbArray[0] = 0; fixProbArray[1] = 1;
		
		fixProbError = new double[vertLimit];
		fixProbError[0] = 0; fixProbError[1] = 0;
	}
	
	public Investigator(int vertLimit,int iterations,int graphNumber,Boolean isDirected,int mutantFitness,int m1,Boolean dummyVariable,int... vertFloor){
		this.vertLimit = vertLimit;
		this.iterations = iterations;
		this.graphNumber = graphNumber;
		this.isDirected = isDirected;
		this.mutantFitness = mutantFitness;
		if(vertFloor[0]!=0){
			this.vertFloor = vertFloor[0];
		}else{
			this.vertFloor = 0;
		}
		
		this.m1 = m1;
		
		simType = SimulationType.MULTIPLE;
		
		meanArray = new double[vertLimit];
		meanArray[0] = 0; meanArray[1] = 0; 
		
		meanError = new double[vertLimit];
		meanError[0] = 0; meanError[1] = 0;
		
		fixProbArray = new double[vertLimit];
		fixProbArray[0] = 0; fixProbArray[1] = 1;
		
		fixProbError = new double[vertLimit];
		fixProbError[0] = 0; fixProbError[1] = 0;
	}
	
	public void run(){
		GraphGenerator g = new GraphGenerator();
		Statistics stats = new Statistics();
		
		//The outer loop iterates through graphs with increasing number of vertices
		for(int x = vertFloor>2?vertFloor:2; x < vertLimit; x++){					
			System.out.println("Thread Id, vertNumber: " + Thread.currentThread().getId() + ", " + x);
			double[] currentMean = new double[graphNumber];
			double[] currentMeanErr = new double[graphNumber];
			double[] currentFixProb = new double[graphNumber];
			double[] currentFixProbErr = new double[graphNumber];
			Simulator s = new Simulator();
			//This loop generates the required number of graphs of the given vertex number and performs a simulation on each one
			for(int y=0;y<graphNumber;y++){	
				if(y%(graphNumber/10)==0){	
					//System.out.println("Thread Id, completion: " + Thread.currentThread().getId() + ", " + y*100/graphNumber + "%");
				}
				if(simType.equals(SimulationType.MORAN)){
					int branchFactor = 3;
					int kValue = 3;
					//matrix = g.randomGraph(x, isDirected, 1);
					matrix = g.kFunnel(branchFactor, kValue);
					s.moranSimulation2(iterations, matrix,mutantFitness);
				}else if(simType.equals(SimulationType.ENVIRONMENTAL)){
					graph = g.unDirEnvGraph(x, mutantFitness, redDensity, numRed);
					s.envSimulation(iterations, graph);
				}else if(simType.equals(SimulationType.MULTIPLE)){
					matrix = g.randomGraph(x,isDirected,1);
					s.multiMutantSim(iterations, matrix, mutantFitness, m1);
				}

				currentMean[y] = stats.freqMean(s.getFixationTimes());
				currentMeanErr[y] = stats.freqStandError(s.getFixationTimes());
				currentFixProb[y] = s.getFixationProb();
				currentFixProbErr[y] = stats.binomialStandardError(s.getFixationProb(), iterations);
			}	
			
			meanArray[x] = Arrays.stream(currentMean).sum()/currentMean.length;
			meanError[x] = Math.sqrt(Arrays.stream(currentMeanErr).map(i -> i*i).sum());
					
			fixProbArray[x] = Arrays.stream(currentFixProb).sum()/currentFixProb.length;
			fixProbError[x] = Math.sqrt(Arrays.stream(currentFixProbErr).map(i -> i*i).sum());
		}
	}
	
	public double[] getMeanArray(){
		return meanArray;
	}
	
	public double[] getMeanError(){
		return meanError;
	}
	
	public double[] getFixProbArray(){
		return fixProbArray;
	}
	
	public double[] getFixProbError(){
		return fixProbError;
	}
}