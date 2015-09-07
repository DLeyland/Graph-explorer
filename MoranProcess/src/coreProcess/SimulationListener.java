package coreProcess;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class SimulationListener implements ActionListener {

	GraphType type;
	
	public SimulationListener(GraphType type) {
		this.type = type;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(type.equals(GraphType.Random)){
			randomGraphSimulation();
		}else if(type.equals(GraphType.Complete)){
			completeGraphSimulation();
		}

	}
	
	private void randomGraphSimulation(){
		String resp = JOptionPane.showInputDialog("How many times would you like to run the simulation",null);
		
		if(resp.equals("")){
		}else{
			
			int iter = Integer.valueOf(resp);
			Simulator s = new Simulator();
			s.moranSimulation2(iter,UserInterface.matrix,2);
			int[] results = s.getFixationTimes();
			
			UserInterface.textArea.append("\n");
			UserInterface.textArea.append("Starting simulation: ");
			UserInterface.textArea.append("\n");
			UserInterface.textArea.append("Selected parameters were: Number of runs of Moran process =" +String.valueOf(iter));
			UserInterface.textArea.append("\n");
			UserInterface.textArea.append("\n");
			UserInterface.textArea.append("Frequency array of number of runs that required that number of iterations to reach fixation:");
			UserInterface.textArea.append("\n");
			for(int result:results){
				UserInterface.textArea.append(String.valueOf(result));
				UserInterface.textArea.append(" ");
			}
			
			float fixProb=s.getFixationProb();
			Statistics stats = new Statistics();
			double standardError = stats.freqStandError(results);
			double mean = stats.freqMean(results);
			

			JFreeChart chart = new ChartBuilder().plotGraph(results, "", "Number of iterations to fixation", "Frequency");
			ChartPanel chartPanel = new ChartPanel(chart);
			JFrame chartFrame = new JFrame();
			chartFrame.add(chartPanel);
			chartFrame.pack();
			chartFrame.setVisible(true);
			
			//Print the fixation probability
			UserInterface.textArea.append("\n");
			UserInterface.textArea.append("Fixation probability: ");
			UserInterface.textArea.append(String.valueOf(fixProb));
			
			//Print the mean with standard error
			UserInterface.textArea.append("\n");
			UserInterface.textArea.append("Mean: ");
			UserInterface.textArea.append(String.valueOf(mean));
			UserInterface.textArea.append("+-");
			UserInterface.textArea.append(String.valueOf(standardError));
		}
	}
	
	private void completeGraphSimulation(){
		JTextField vertNumField = new JTextField();
		JTextField simNumField = new JTextField();
		JTextField relFitField = new JTextField();
		Object[] message = {
			    "Number of vertices:", vertNumField,
			    "Number of iterations:", simNumField,
			    "Relative fitness:", relFitField
		};
		int option = JOptionPane.showConfirmDialog(null, message, "Simulation options", JOptionPane.OK_CANCEL_OPTION);
		
		if (option == JOptionPane.OK_OPTION) {
			if(vertNumField.getText().equals("")==false&&simNumField.getText().equals("")==false){
				
				int numVerts = Integer.valueOf(vertNumField.getText());
				int iters = Integer.valueOf(simNumField.getText());
				int relFitness =  Integer.valueOf(relFitField.getText());
				float runningTotal=0;
				Simulator s = new Simulator();
				
				for(int x=0;x<numVerts;x++){
					UserInterface.matrix = new GraphGenerator().completeGraph(numVerts, x, relFitness);
					s.moranSimulation(iters, UserInterface.matrix);
					runningTotal=runningTotal + s.getFixationProb();
				}
				float fixationProb = runningTotal / numVerts;
				
				UserInterface.textArea.append("\n");
				UserInterface.textArea.append(String.valueOf(fixationProb));
			}
		}
	}
}
