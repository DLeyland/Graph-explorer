package coreProcess;

import java.util.List;

public class EnvGraph {
	
	private List<List<Integer>> edgeStructure;
	private List<Colour> foregroundColour;
	private List<Colour> backgroundColour;
	private int relFitness;
	
	
	
	public EnvGraph(List<List<Integer>> edgeStructure,List<Colour> foregroundColour,List<Colour> backgroundColour,int relFitness){
		
		this.edgeStructure = edgeStructure;
		this.foregroundColour = foregroundColour;
		this.backgroundColour = backgroundColour;
		this.relFitness = relFitness;
	
	}
	
	public List<List<Integer>> getEdgeStructure(){
		return edgeStructure;
	}
	
	public List<Colour> getForegroundColours(){
		return foregroundColour;
	}
	
	public List<Colour> getBackgroundColours(){
		return backgroundColour;
	}
	
	public int getRelFitness(){
		return relFitness;
	}
}
