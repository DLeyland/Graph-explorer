package coreProcess;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class GraphGenerationListener implements ActionListener {
	
	GraphType type;
	
	public GraphGenerationListener(GraphType type){
		this.type = type;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(type.equals(GraphType.Complete)){
			completeGraph();
		}else if(type.equals(GraphType.Random)){
			randomGraph();
		}else if(type.equals(GraphType.KFunnel)){
			kFunnel();
		}

	}
	
	public void completeGraph(){
		
		JTextField vertNumField = new JTextField();
		JTextField relFitField = new JTextField();
		Object[] message={
				"Number of vertices:",vertNumField,
				"Mutant relative fitness:", relFitField
		};
		int option = JOptionPane.showConfirmDialog(null, message, "Simulation options", JOptionPane.OK_CANCEL_OPTION);

		int relFitness;
		if(relFitField.getText().equals("")==true){
			relFitness =2;
		}else{
			relFitness =Integer.valueOf(relFitField.getText());
		}
		
		//Check that ok was pressed and a value was supplied for number of vertices
		if(option==JOptionPane.OK_OPTION && vertNumField.getText().equals("")==false){
			
			int numVerts = Integer.valueOf(vertNumField.getText());
			UserInterface.matrix = new GraphGenerator().completeGraph(numVerts,numVerts/2 ,relFitness);
			
			//Print the matrix we produced
			UserInterface.textArea.append("\n");
			for(List<Integer> line :UserInterface.matrix){
				UserInterface.textArea.append("\n");
				for(Integer num:line){
					UserInterface.textArea.append(String.valueOf(num));
					UserInterface.textArea.append(" ");
				}
			}
		}	
	}
	
	public void randomGraph(){
		JTextField vertNumField = new JTextField();
		JTextField relFitField = new JTextField();
		JCheckBox directedCheck = new JCheckBox();
		Object[] message={
				"Number of vertices:",vertNumField,
				"Mutant relative fitness:",relFitField,
				"Directed graph:",directedCheck
		};
		int option=JOptionPane.showConfirmDialog(null,message,"Graph options",JOptionPane.OK_CANCEL_OPTION);
		
		//Check that ok was pressed and a value was supplied for number of vertices and relative fitness
		if(option==JOptionPane.OK_OPTION && !vertNumField.getText().equals("")&& !relFitField.getText().equals("")){
			
			//Process the inputted data
			int numVerts = Integer.valueOf(vertNumField.getText());
			int mutantFitness = Integer.valueOf(relFitField.getText());
			Boolean isDirected;
			if(directedCheck.isSelected()){
				isDirected=true;
			}else{
				isDirected=false;
			}
			//Produce and print our random graph
			UserInterface.matrix = new GraphGenerator().randomGraph(numVerts, isDirected, mutantFitness);
			UserInterface.textArea.append("Generating new random graph with " + String.valueOf(numVerts) + " vertices:");
			UserInterface.textArea.append("\n");
			for(List<Integer> line :UserInterface.matrix){
				UserInterface.textArea.append("\n");
				for(Integer num:line){
					UserInterface.textArea.append(String.valueOf(num));
					UserInterface.textArea.append(" ");
				}
			}
			UserInterface.textArea.append("\n");
		}
	}
	
	public void kFunnel(){
		JTextField branchFactorField = new JTextField();
		JTextField kValueField = new JTextField();
		
		Object[] message={
				"Branching factor:",branchFactorField,
				"k-value:",kValueField,
		};
		int option=JOptionPane.showConfirmDialog(null,message,"Graph options",JOptionPane.OK_CANCEL_OPTION);
		//Check that ok was pressed and a value was supplied for branching factor and k-value
		if(option==JOptionPane.OK_OPTION && !branchFactorField.getText().equals("")&& !kValueField.getText().equals("")){
			
			//Process the inputted data
			int branchFactor = Integer.valueOf(branchFactorField.getText());
			int kValue = Integer.valueOf(kValueField.getText());
			//Produce and print our k-funnel graph
			UserInterface.matrix = new GraphGenerator().kFunnel(branchFactor,kValue);
			UserInterface.textArea.append("Generating new k-funnel graph");
			UserInterface.textArea.append("\n");
			for(List<Integer> line :UserInterface.matrix){
				UserInterface.textArea.append("\n");
				for(Integer num:line){
					UserInterface.textArea.append(String.valueOf(num));
					UserInterface.textArea.append(" ");
				}
			}
			UserInterface.textArea.append("\n");
		}
	}
	
}
