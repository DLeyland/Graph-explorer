package coreProcess;

import java.util.*;

/*
 * This class is used to perform the Moran process on a graph, the current state of the graph is kept in matrix, which is initially read in from a file.
 */
public class MoranProcess{

	
	protected List<List<Integer>> matrix;
	protected Random rand;
	protected int numVerts;
	protected int absVal;
	int mutantFitness = 2;
	
	public MoranProcess(List<List<Integer>> inputMatrix){
		matrix = new ArrayList<List<Integer>>();
		rand = new Random();
		numVerts = inputMatrix.size();
		absVal=1;
		
		for(int x=0;x<numVerts;x++){
			matrix.add(new ArrayList<Integer>());
		}
		
		for(int x=0;x<numVerts;x++){
			for(int y=0;y<numVerts;y++){
				int val = Integer.valueOf(inputMatrix.get(x).get(y));
				matrix.get(x).add(val);
			}
		}
		
	}
	
	/*
	 * This method picks which vertex should reproduce at this time step, where the chance to be
	 * picked is proportional to the vertex's reproductive fitness.
	 */
	protected int reprPicker(){
		double cumulative =0;
		//add all of the vertex's fitness such that a mutant with relative fitness r=2 will take up twice the probability space as
		//a resident with r=1
		for(int x=0;x<numVerts;x++){
			cumulative += matrix.get(x).get(x); 		
		}

		double scaleFactor = 1.0 - rand.nextDouble(); //Make the double's range inclusive of 1 but exclusive of 0
		double targetVal = scaleFactor * cumulative;
		
		cumulative =0;
		int targetVert=-1;
		for(int x =0;x<numVerts;x++){
			cumulative += matrix.get(x).get(x);
			if(cumulative>=targetVal){
				targetVert=x;
				break;
			}
		}
		return targetVert; //Index starts at 0
	}
	
	/*
	 * This method picks a neighbour for the reproducing vertex to replace, takes the number of the reproducing vertex as a parameter,
	 * the larger the weight of the connecting edge the more likely that neighbour is to be picked.
	 */
	protected int neighPicker(int vertex){
		double cumulative =0;
		for(int x=0;x<numVerts;x++){
			if(x==vertex){ //want edge weights not targetVert's relative fitness
				
			}else{
				cumulative += matrix.get(x).get(vertex); 
			}			
		}
		
		if(cumulative==0){
			System.out.println("Ruh-roh!");
			return vertex;
		}

		double scaleFactor = 1.0 - rand.nextDouble(); //Make the double's range inclusive of 1 but exclusive of 0
		double targetVal = scaleFactor * cumulative;
		
		cumulative =0;
		int targetVert=-1;
		for(int x =0;x<numVerts;x++){
			if(x==vertex){ 
				//want edge weights not targetVert's relative fitness
			}else{
				cumulative += matrix.get(x).get(vertex); 
			}
			if(cumulative>=targetVal){
				targetVert=x;
				break;
			}
		}
		return targetVert; //Index starts at 0
	}
	
	/*
	 * Tests whether absorption has been reached. Absorption occurs when the mutant either achieves fixation or goes extinct. This will count a
	 * mutant with relative fitness of 1 as having reached absorption
	 */
	protected boolean isAbsorbed(){
		int val = matrix.get(0).get(0);
		for(int x=1;x<numVerts;x++){
			if(matrix.get(x).get(x)==val){

			}else{
				return false;
			}
		}
		absVal=val;
		return true;
	}
	
	/*
	 * Performs one iteration of the Moran process.
	 */
	protected void performIteration(){
		int reprVertex = reprPicker();
		int deadVertex = neighPicker(reprVertex);
		matrix.get(deadVertex).set(deadVertex,matrix.get(reprVertex).get(reprVertex));
	}
	
	/*
	 * This method performs the Moran process for a number of iterations defined by an optional parameter that sets the maximum number of iterations 
	 * performed before breaking, if not given it will run until absorption is reached (potentially infinitely!). The number returned is the 
	 * number of iterations that were required to reach absorption, if it returns the number of iterations you specified +1 then it didn't reach
	 * absorption in the time given.
	 */
	public int runProcess(int... iterations){
		if(iterations.length>0){
			for(int x =iterations[0];x>0;x--){
				performIteration();
				if(isAbsorbed()==true){
					return (iterations[0]-x);
				}
			}
			return (iterations[0]+1);
		}
		else{
			int x = 0;
			while(!isAbsorbed()){
				performIteration();
				x++;
				//FPRAS
				/*if((numVerts - numResidents())>=Math.pow((numVerts*(mutantFitness-1)), 0.25)){
					break;
				}*/
				//if(x>10000){
					//System.out.println("10000 boundary passed");
				//}
			}
			return x;
		}
	}
	
	/*
	 * Getter method to access matrix
	 */
	public List<List<Integer>> getMatrix(){
		return matrix;
	}
	
	/*
	 * getter method to access absVal
	 */
	public int getAbsVal(){
		return absVal;
	}
	
	public int numResidents(){
		int numResidents=0;
		for(int x=0; x<numVerts;x++){
			if(matrix.get(x).get(x)==1){
				numResidents++;
			}
		}
		return numResidents;
	}
	
}