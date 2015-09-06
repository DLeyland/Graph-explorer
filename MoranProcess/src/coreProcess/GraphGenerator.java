package coreProcess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/*
 * This class contains methods for generating graphs with certain properties
 */
public class GraphGenerator{
	
	Random rand;
	
	public GraphGenerator(){
		rand = new Random();	
	}
	
	/*
	 * This method generates a complete graph with a number of vertices specified by numVerts, with a mutant of relative 
	 * fitness relFitness and in position mutanatPos
	 */
	public List<List<Integer>> completeGraph(int numVerts,int mutantPos,int relFitness){
		List<List<Integer>> matrix = new ArrayList<List<Integer>>();
		
		//temporary hack to randomly place mutant if you pass mutantPos 1 greater than the number of vertices
		if(mutantPos>=(numVerts+1)){
			mutantPos=(new Random().nextInt(numVerts));
			
		}
		
		for(int x=0;x<numVerts;x++){
			matrix.add(new ArrayList<Integer>());
		}
		
		for(int x=0;x<numVerts;x++){
			for(int y=0;y<numVerts;y++){
				matrix.get(x).add(1);
			}
		}
		
		matrix.get(mutantPos).set(mutantPos, relFitness);
		return matrix;
	}
	
	public EnvGraph unDirEnvGraph(int numVerts,int relativeFitness,double redDensity,int numRed){
		List<List<Integer>> edgeStructure = randomGraph(numVerts,false,1);
		//List<List<Integer>> edgeStructure = completeGraph(numVerts,1,1);
		
		List<Colour> foregroundColour = new ArrayList<Colour>(Collections.nCopies(numVerts,Colour.BLUE));
		List<Colour> backgroundColour = new ArrayList<Colour>(Collections.nCopies(numVerts,Colour.BLUE));
		
		int backRedNum = (int)(numVerts*redDensity);
		
		while(backRedNum>0){
			int i = rand.nextInt(numVerts);
			if(!backgroundColour.get(i).equals(Colour.RED)){
				backgroundColour.set(i,Colour.RED);
				backRedNum--;
			}
		}
		
		while(numRed>0){
			int i = rand.nextInt(numVerts);
			if(!foregroundColour.get(i).equals(Colour.RED)){
				foregroundColour.set(i, Colour.RED);
				numRed--;
			}
		}
		
		for(int x=0;x<numVerts;x++){
			if(foregroundColour.get(x).equals(backgroundColour.get(x))){
				edgeStructure.get(x).set(x,relativeFitness);
			}else{
				edgeStructure.get(x).set(x,1);
			}
		}
		
		EnvGraph graph = new EnvGraph(edgeStructure,foregroundColour,backgroundColour,relativeFitness);
		return graph;
	}
	
	public List<List<Integer>> kFunnel(int branchFactor, int kValue){
		
		int numVerts = ( ( ((int)Math.pow(branchFactor, kValue)) - 1) / (branchFactor - 1) );
		
		List<List<Integer>> matrix = new ArrayList<List<Integer>>();
		for(int x=0;x<numVerts;x++){
			matrix.add(new ArrayList<Integer>());
		}
		for(int x=0;x<numVerts;x++){
			for(int y=0;y<numVerts;y++){
				matrix.get(x).add(0);
			}
		}
		for(int x=0;x<numVerts;x++){
			matrix.get(x).set(x,1);
		}
		
		int bin =1;//bin is 1-indexed to make calculations simpler
		for(int x=0;x<numVerts;x++){
			
			int condition = ((int)Math.pow(branchFactor, bin) - 1) / (branchFactor - 1);
			if(x >= condition){
				bin++;
			}
			
			int yLowBound = ((int)Math.pow(branchFactor, bin) - 1) / (branchFactor - 1);
			int yHighBound = ( (((int)Math.pow(branchFactor, bin+1)) - 1) / (branchFactor - 1));
			if(bin!=kValue){	
				for(int y=yLowBound;y<yHighBound;y++){
					matrix.get(x).set(y, 1);
				}
			}else{
				matrix.get(x).set(0,1);
			}
		}
		
		
		return matrix;
	}
	
	/*
	 * For undirected graphs
	 */
	public List<List<Integer>> graphIterator(List<List<Integer>> inputMatrix){
		List<List<Integer>> newMatrix= new ArrayList<List<Integer>>();
		int numVerts=inputMatrix.size();
		
		for(int x=0;x<numVerts;x++){
			newMatrix.add(new ArrayList<Integer>());
			for(int y=0;y<numVerts;y++){
				int val = Integer.valueOf(inputMatrix.get(x).get(y));
				newMatrix.get(x).add(val);
			}
		}
		
		int xPos=0;
		int yPos=0;
		int edgeCount =0;
		//find the number of edges and coords of smallest edge
		for(int x=1;x<numVerts;x++){
			for(int y=0;y<x;y++){
				if(inputMatrix.get(x).get(y)!=0){
					edgeCount++;
					if(xPos==0){
						xPos=x;
						yPos=y;
					}
				}
			}
		}
		if(edgeCount==0){
			newMatrix.get(1).set(0, 1);
			
			newMatrix.get(0).set(1, 1);
		
		//complete graph was passed in
		}else if(edgeCount==((numVerts*(numVerts-1))/2)){
			newMatrix=inputMatrix;
			
		}else{

			
			A:for(int x=1;x<numVerts;x++){
				for(int y =0;y<x;y++){
					if(inputMatrix.get(x).get(y)!=0){
						xPos=x;
						yPos=y;	
						break A;
					}
				}
			}

			//can add an edge above
			if(inputMatrix.get(xPos).get(yPos+1)==0){
				newMatrix.get(xPos).set(yPos,0);
				newMatrix.get(xPos).set(yPos+1, 1);
				
				newMatrix.get(yPos).set(xPos,0);
				newMatrix.get(yPos+1).set(xPos, 1);
			
			//can add edge  at bottom of next row
			}else if(yPos+1==xPos&&xPos+1!=numVerts&&inputMatrix.get(xPos+1).get(0)==0){
				newMatrix.get(xPos).set(yPos,0);
				newMatrix.get(xPos+1).set(0, 1);
				
				newMatrix.get(yPos).set(xPos,0);
				newMatrix.get(0).set(xPos+1, 1);
			
			//first edge reached the end, add new edge
			}else if(xPos+1==numVerts&&yPos+1==xPos){
				newMatrix.get(1).set(0, 1);
				
				newMatrix.get(0).set(1, 1);
			
			//another edge has been met
			}else{
				
				
				//floodfilled case
				if(xPos==1){
					
					int sequenceLength=0;
					A:for(int x=1;x<numVerts;x++){
						for(int y =0;y<x;y++){
							if(inputMatrix.get(x).get(y)!=0){
								sequenceLength++;
							}else{
								break A;
							}
							
						}
					}
						
					if(sequenceLength==edgeCount){
						
						//clear the edges of newMatrix
						for(int x=1;x<numVerts;x++){
							for(int y =0;y<x;y++){
								newMatrix.get(x).set(y,0);
								
								newMatrix.get(y).set(x,0);
							}
						}
					
						newMatrix.get(1).set(0, 1);
						
						newMatrix.get(0).set(1, 1);
						
						int edgeCountIter = edgeCount;
						//Drip fill newMatrix with edgeCount number of vertices
						B:for(int x = numVerts-1;x>0;x--){
							for(int y =x-1;y>-1;y--){
								newMatrix.get(x).set(y,1);
								
								newMatrix.get(y).set(x,1);
								
								edgeCountIter--;
								if(edgeCountIter==0){
									break B;
								}
							}
						}
						
					}else{
						
						int upperXPos=0;
						int upperYPos=0;
						Boolean afterSequence=false;
						//calculate upperXPos and upperYPos 
						A:for(int x=1;x<numVerts;x++){
							for(int y =0;y<x;y++){
								if(afterSequence==false){
									
									if(inputMatrix.get(x).get(y)!=0){
										
										if(y!=x-1){
											if(inputMatrix.get(x).get(y+1)==0){
												afterSequence=true;
											}
										}else if(x!=numVerts-1){
											if(inputMatrix.get(x+1).get(0)==0){
												afterSequence=true;
											}
										}else{
											System.out.println("Complete graph was passed in");
											
										}
									
									}
								}else{
									if(inputMatrix.get(x).get(y)!=0){
										upperXPos=x;
										upperYPos=y;
										break A;
									}
								}
							}
						}
						
						int sequenceLenIter=sequenceLength-1;
						
						A:for(int x=2;x<numVerts;x++){
							for(int y =0;y<x;y++){
								newMatrix.get(x).set(y,0);
								
								newMatrix.get(y).set(x,0);
								sequenceLenIter--;
								if(sequenceLenIter==0){
									break A;
								}
							}
						}
						
						sequenceLenIter=sequenceLength-1;
						Boolean afterPos=false;
						//Drip fill newMatrix with sequenceLength-1 number of vertices starting below upperXPos, upperYPos
						B:for(int x = numVerts-1;x>0;x--){
							for(int y =x-1;y>-1;y--){
								if(afterPos==false){
									
									if(x==upperXPos&&y==upperYPos){
										newMatrix.get(x).set(y,0);
										
										newMatrix.get(y).set(x,0);
										afterPos=true;
									}
								}else{
									newMatrix.get(x).set(y,1);
									
									newMatrix.get(y).set(x,1);
									sequenceLenIter--;
									if(sequenceLenIter==0){
										break B;
									}
								}
								
							}
						}
					}
				}
				
				
				//can shift the edge we met down
				if(yPos+1!=xPos){
					newMatrix.get(xPos).set(yPos+1,0);
					newMatrix.get(1).set(0, 1);
					
					newMatrix.get(yPos+1).set(xPos,0);
					newMatrix.get(0).set(1, 1);
				
				//have to shift edge we met across and up
				}else{
					newMatrix.get(xPos+1).set(0,0);
					newMatrix.get(1).set(0, 1);
					
					newMatrix.get(0).set(xPos+1,0);
					newMatrix.get(0).set(1, 1);
				}
				
			}
		}
		return newMatrix;
		
	}
			
	public List<List<Integer>> randomGraph(int numVerts,Boolean isDirected,int mutantFitness,int... mutantPos){
		float epsilon=1;
		Integer results[] = new Integer[numVerts+1];
		
		while(true){
			epsilon=epsilon/2;
			Integer[] tree = attempt(epsilon,numVerts);
			if(tree.length!=1){
				results = tree;
				break;
			}
		}
		
		//Decide the number of edges you want 
		int sparseness;
		if(isDirected){
			sparseness = rand.nextInt( (numVerts*(numVerts-2)) + 2) + (numVerts-1);
		}else{
			sparseness = rand.nextInt( ((numVerts*(numVerts-1))/2) - numVerts + 2 ) + (numVerts-1);
		}
		
		//Get the mutantPosition if specified or randomly generate it if not
		int mutantPosition;
		if(mutantPos.length>0){
			mutantPosition = mutantPos[0];
		}else{
			mutantPosition = rand.nextInt(numVerts);
		}
		//for(Integer val:results){
		//	System.out.print(val);
		//	System.out.print(" ");
		//}
		//System.out.print("\n");
		List<List<Integer>> resultMatrix = arrayToMatrix(results,sparseness,isDirected,mutantFitness,mutantPosition);
		return resultMatrix;
	}
	
	private Integer[] attempt(float epsilon,int numVerts){
		Boolean inTree[] = new Boolean[numVerts+1];
		Integer next[] = new Integer[numVerts+1];
		for(int x=1;x<=numVerts;x++){
			inTree[x] = false;
		}
		
		int numRoots = 0;
		for(int x=1;x<=numVerts;x++){
			int u =x;
			while(inTree[u].equals(false)){
				if((1.0-rand.nextFloat())<=epsilon){
					next[u] = null;
					inTree[u] = true;
					numRoots++;
					if(numRoots==2){
						Integer failureArray[] = {0};
						return failureArray;
					}
				}else{
					next[u] = randomSuccessor(u,numVerts);
					u=next[u];
				}
			}
			u=x;
			while(inTree[u].equals(false)){
				inTree[u] = true;
				u=next[u];
			}
		}
		return next;
	}
	
	private int randomSuccessor(int u,int numVerts){
		int x;
		while(true){
			x = rand.nextInt(numVerts)+1;
			if(x!=u){
				return x;
			}
		}
	}
	
	public Integer[] randomTreeWithRoot(int r,int numVerts){
		Boolean[] inTree = new Boolean[numVerts+1];
		Integer next[] = new Integer[numVerts+1];
		
		for(int i=1;i<=numVerts;i++){
			inTree[i] = false;
		}
		next[r] = null;
		inTree[r] = true;
		for(int i=1;i<=numVerts;i++){
			int u=i;
			while(inTree[u].equals(false)){
				next[u] = randomSuccessor(u,numVerts);
				u = next[u];
			}
			u=i;
			while(inTree[u].equals(false)){
				inTree[u] = true;
				u = next[u];
			}
		}
		next[r]=-1;
		return next;
	}
	
	private List<List<Integer>> arrayToMatrix(Integer nextArray[],int sparseness,Boolean isDirected,int mutantFitness,int mutantPos){
		
		List<List<Integer>> matrix = new ArrayList<List<Integer>>();
		for(int x=0;x<nextArray.length-1;x++){
			matrix.add(new ArrayList<Integer>(nextArray.length-1));
		}
		for(int x=0;x<nextArray.length-1;x++){
			for(int y=0;y<nextArray.length-1;y++){
				matrix.get(x).add(0);
			}
		}
		
		//Re-format nextArray to be 0 indexed
		Integer formattedArray[] = new Integer[nextArray.length-1];
		for(int x=0;x<nextArray.length-1;x++){
			if(nextArray[x+1]==null){
				formattedArray[x] = null;
			}
			else{
				formattedArray[x] = nextArray[x+1]-1;
			}
		}
		
		//Add the edges to form the minimum spanning tree, keeping track of the number of edges added
		int edgeCount=0;
		for(int x=0;x<formattedArray.length;x++){
			if(formattedArray[x]!=null){
				matrix.get(x).set(formattedArray[x], 1);
				edgeCount++;
				if(!isDirected){	
					matrix.get(formattedArray[x]).set(x, 1);
				}else{
					matrix.get(formattedArray[x]).set(x, 1);
					edgeCount++;
				}
			}
		}
		
		//Add in the vertex fitnesses
		for(int x=0;x<formattedArray.length;x++){
			if(x!=mutantPos){
				matrix.get(x).set(x,1);
			}else{
				matrix.get(x).set(x,mutantFitness);
			}
		}
		
		//Randomly fill in any remaining edges needed to meet the required sparseness
		while(sparseness-edgeCount>0){
			int x=rand.nextInt(formattedArray.length);
			//int y = randomSuccessor(x+1,formattedArray.length)-1;
			int y = rand.nextInt(formattedArray.length);
			if(matrix.get(x).get(y)==0){
				matrix.get(x).set(y,1);
				if(!isDirected){
					matrix.get(y).set(x,1);
				}
				edgeCount++;
			}
		}
		
		return matrix;
	}
}