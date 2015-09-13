package coreProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EnvMoranProc extends MoranProcess{

	List<Colour> backgroundColour;
	List<Colour> foregroundColour;
	Colour finalColour;
	int relFitness;
	
	public EnvMoranProc(EnvGraph graph){
		super(graph.getEdgeStructure());
		
		backgroundColour = new ArrayList<Colour>();
		for(int x=0;x<numVerts;x++){
			if(graph.getBackgroundColours().get(x).equals(Colour.RED)){
				backgroundColour.add(Colour.RED);
			}else if(graph.getBackgroundColours().get(x).equals(Colour.BLUE)){
				backgroundColour.add(Colour.BLUE);
			}else if(graph.getBackgroundColours().get(x).equals(Colour.GREEN)){
				backgroundColour.add(Colour.GREEN);
			}else{
				backgroundColour.add(null);
			}
		}

		foregroundColour = new ArrayList<Colour>();
		for(int x=0;x<numVerts;x++){
			if(graph.getForegroundColours().get(x).equals(Colour.RED)){
				foregroundColour.add(Colour.RED);
			}else if(graph.getForegroundColours().get(x).equals(Colour.BLUE)){
				foregroundColour.add(Colour.BLUE);
			}else if(graph.getForegroundColours().get(x).equals(Colour.GREEN)){
				foregroundColour.add(Colour.GREEN);
			}else{
				foregroundColour.add(null);
			}
		}
		
		relFitness = Integer.valueOf(graph.getRelFitness());
	}
	
	/*
	 * Tests whether absorption has been reached. Absorption occurs when the mutant either achieves fixation or goes extinct. This will count a
	 * mutant with relative fitness of 1 as having reached absorption
	 */
	@Override
	protected boolean isAbsorbed(){
		Colour comparisonColour = foregroundColour.get(0);
		for(int x=1;x<numVerts;x++){
			if(!foregroundColour.get(x).equals(comparisonColour)){
				return false;
			}
		}
		finalColour = comparisonColour;
		return true;
	}
	
	/*
	 * Performs one iteration of the Moran process.
	 */
	@Override
	protected void performIteration(){
		int reprVertex = reprPicker();
		int deadVertex = neighPicker(reprVertex);
		foregroundColour.set(deadVertex,foregroundColour.get(reprVertex));
		if(foregroundColour.get(deadVertex).equals(backgroundColour.get(deadVertex))){
			matrix.get(deadVertex).set(deadVertex,relFitness);
		}else{
			matrix.get(deadVertex).set(deadVertex,1);
		}
	}
	
	/*
	 * getter method to access absVal
	 */
	public Colour getFinalColour(){
		return finalColour;
	}
	
}
