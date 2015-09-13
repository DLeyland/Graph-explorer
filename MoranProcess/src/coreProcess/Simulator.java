package coreProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * This class contains methods to perform a 'simulation' then collect data about the results. A simulation is an operation where for a given
 * starting graph a process, such as the Moran process, is performed for a given number of times with the results of each run being recorded
 * as an entry in an array. One of the simulation methods must be run before any of the other methods in the class can be run.
 */
public class Simulator{
	private List<Integer> iterResults; //Each element is the number of iterations that that run of the process took to reach absorption 
	private List<Integer> fixResults; //Each element is an integer indicating the fitness of the type that achieved absorption
	
	
	public Simulator(){
		iterResults = new ArrayList<Integer>();
		fixResults = new ArrayList<Integer>();
	}
	
	/*
	 * Run a simulation performing the Moran process for a number of runs given by the iterations, on the starting matrix given by matrix
	 */
	public void moranSimulation(int iterations, List<List<Integer>> matrix){
		for(int x=0;x<iterations;x++){
			MoranProcess m = new MoranProcess(matrix);
			iterResults.add(m.runProcess());
			fixResults.add(m.getAbsVal());
		}
	}
	
	public void moranSimulation2(int iterations, List<List<Integer>> matrix, int relativeFitness){
		int numVerts = matrix.size();		
		Random rand = new Random();
		for(int x=0;x<iterations;x++){						
			int pos = rand.nextInt(numVerts);
			matrix.get(pos).set(pos,relativeFitness);
			MoranProcess m = new MoranProcess(matrix);
			iterResults.add(m.runProcess());
			fixResults.add(m.getAbsVal());
			matrix.get(pos).set(pos,1);
		}
	}
	
	public void envSimulation(int iterations, EnvGraph graph){
		for(int x=0;x<iterations;x++){
			EnvMoranProc m = new EnvMoranProc(graph);
			int inter = m.runProcess();
			if(inter==0){
				System.out.println("0 case");
				iterResults.add(0);
			}else{
				iterResults.add(inter);
			}
			
			if(m.getFinalColour().equals(Colour.RED)){
				fixResults.add(2);
			}else{
				fixResults.add(1);
			}
		}
	}
	
	public void multiMutantSim(int iterations, List<List<Integer>> matrix,int mutantFitness,int m1){
		List<List<Integer>> filledMatrix = new ArrayList<List<Integer>>();
		int numVerts = matrix.size();
		Random rand = new Random();
		
		//Fill the matrix up with m1 resident-mutant types
		int swapPosition;	
		int initialValue;
		Boolean condition = true;
		while(condition){
			swapPosition = rand.nextInt(numVerts);
			initialValue = matrix.get(swapPosition).get(swapPosition);
			matrix.get(swapPosition).set(swapPosition,-mutantFitness);
			MoranProcess m = new MoranProcess(matrix);
			while(true){
				m.performIteration();
				if(m.numResidents()==numVerts){
					matrix.get(swapPosition).set(swapPosition,initialValue);
					break;
				}else if(numVerts-m.numResidents()==m1||m.numResidents()==0){
					condition = false;
					filledMatrix = m.getMatrix();
					break;
				}
			}
		}
		
		//Introduce the second mutant type and evolve the graph until absorption is reached, this is done iterations times
		MoranProcess m2;
		int valueStore;
		for(int x=0;x<iterations;x++){
			swapPosition = rand.nextInt(numVerts);
			valueStore = filledMatrix.get(swapPosition).get(swapPosition);
			if(valueStore==1){
				filledMatrix.get(swapPosition).set(swapPosition,(mutantFitness));
			}else{
				filledMatrix.get(swapPosition).set(swapPosition,(mutantFitness*mutantFitness));
			}
			m2 = new MoranProcess(filledMatrix);
			iterResults.add(m2.runProcess());
			fixResults.add(m2.getAbsVal());
			filledMatrix.get(swapPosition).set(swapPosition,valueStore);
		}
	}
	
	/*
	 * Returns the fixation probability of the graph the simulation was ran on
	 */
	public float getFixationProb(){
		int x =0;
		for(Integer val:fixResults){
			if(val>1){
				x++;
			}
		}
		return ((float)x/fixResults.size());
	}
	
	public List<Integer> getFixResults(){
		return fixResults;
	}
	
	/*
	 * Returns a frequency distribution of the absorption times that the simulation generated
	 */
	public int[] getAbsorptionTimes(){
		int largestVal = iterResults.get(0);
		for(int currentVal:iterResults){
			if(currentVal>largestVal){
				largestVal=currentVal;
			}
		}
		
		int[] results = new int[largestVal+1];
		for(int currentVal:iterResults){
			results[currentVal]++;
		}
		
		return results;	
	}
	
	/*
	 * Returns a frequency distribution of the fixation times that the simulation generated
	 */
	public int[] getFixationTimes(){
		int largestVal = 0;
		for(int x=0;x<iterResults.size();x++){
			if(fixResults.get(x)>1){
				if(iterResults.get(x)>largestVal){
					largestVal=iterResults.get(x);
				}
			}
		}
		
		int[] results = new int[largestVal+1];
		for(int x=0;x<iterResults.size();x++){
			if(fixResults.get(x)>1){
				results[iterResults.get(x)]++;
			}
		}
		
		return results;
	}


}