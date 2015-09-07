package coreProcess;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

public class InvestigationListener implements ActionListener{
	
	SimulationType simType;
	
	public InvestigationListener(SimulationType simType){
		this.simType = simType;
	}
	
	public void actionPerformed(ActionEvent e){
		if(simType.equals(SimulationType.ENVIRONMENTAL)){
			environmentalInvestigation();
		}else if(simType.equals(SimulationType.MORAN)){
			defaultInvestigation();
		}else if(simType.equals(SimulationType.MULTIPLE)){
			multiInvestigation();
		}
	}
	
	private void defaultInvestigation(){
		JTextField minVertField = new JTextField();
		JTextField vertNumField = new JTextField();
		JTextField iterNumField = new JTextField();
		JTextField graphNumField = new JTextField();
		JTextField relFitField = new JTextField();
		JTextField t1NumField = new JTextField();
		JCheckBox directedCheck = new JCheckBox();
		Object[] message={
				"Minimum number of vertices:",minVertField,
				"Maximum number of vertices:",vertNumField,
				"Iterations per graph:",iterNumField,
				"Graphs per vertex number",graphNumField,
				"Mutant relative fitness", relFitField,
				"Number of type-1 mutants", t1NumField,
				"Directed graph:",directedCheck
		};
		
		int option=JOptionPane.showConfirmDialog(null,message,"Graph options",JOptionPane.OK_CANCEL_OPTION);
		
		//Check that ok was pressed and a value was supplied for number of vertices and number of iterations per graph
		if(option==JOptionPane.OK_OPTION && !vertNumField.getText().equals("")&& !iterNumField.getText().equals("")){
			int vertFloor;
			if(!vertNumField.getText().equals("")){
				vertFloor = Integer.valueOf(minVertField.getText());
			}else{
				vertFloor=0;
			}
			int vertLimit = Integer.valueOf(vertNumField.getText())+1;
			int iterations =Integer.valueOf(iterNumField.getText());
			int graphNumber = Integer.valueOf(graphNumField.getText());
			Boolean isDirected = directedCheck.isSelected();
			int mutantFitness = Integer.valueOf(relFitField.getText());
			int numT1Mutants = Integer.valueOf(t1NumField.getText());
			
		    //Initialise a threadPool and an array of investigators to provide each thread with an Investigator runnable
			int numThreads = 4;
			Investigator[] invArray = new Investigator[numThreads];
			ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
			Future[] futureArray = new Future[numThreads];
			
			//Assign the tasks to the threads
			for(int i=0;i<numThreads;i++){
				invArray[i] = new Investigator(vertLimit,iterations,graphNumber/numThreads,isDirected,mutantFitness,numT1Mutants,false,vertFloor);;
				futureArray[i] = threadPool.submit(invArray[i]);
			}
			
			threadPool.shutdown();
			
		
			try{
				for(Future future: futureArray){
					future.get();
				}

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
			
			//The remaining code is for printing and producing graphs of the results
			UserInterface.textArea.append("\n");	
			for(double val:meanArray){	
				UserInterface.textArea.append(String.valueOf(val));
				UserInterface.textArea.append(" ");
			}
			UserInterface.textArea.append("\n");	
			for(double val:meanError){	
				UserInterface.textArea.append(String.valueOf(val));
				UserInterface.textArea.append(" ");
			}
			UserInterface.textArea.append("\n");
			UserInterface.textArea.append("\n");
			for(double val:fixProbArray){	
				UserInterface.textArea.append(String.valueOf(val));
				UserInterface.textArea.append(" ");
			}
			UserInterface.textArea.append("\n");	
			for(double val:fixProbError){	
				UserInterface.textArea.append(String.valueOf(val));
				UserInterface.textArea.append(" ");
			}
			
			JFreeChart chart = new ChartBuilder().plotGraph(meanArray, "", "Number of vertices", "Mean fixation time");
			ChartPanel chartPanel = new ChartPanel(chart);
			JFrame chartFrame = new JFrame();
			chartFrame.add(chartPanel);
			chartFrame.pack();
			chartFrame.setVisible(true);
			
			JFreeChart chart2 = new ChartBuilder().plotGraph(fixProbArray, "", "Number of vertices", "Fixation probability");
			ChartPanel chartPanel2 = new ChartPanel(chart2);
			JFrame chartFrame2 = new JFrame();
			chartFrame2.add(chartPanel2);
			chartFrame2.pack();
			chartFrame2.setVisible(true);
		}
	}
	
	private void environmentalInvestigation(){
		JTextField minVertField = new JTextField();
		JTextField vertNumField = new JTextField();
		JTextField iterNumField = new JTextField();
		JTextField graphNumField = new JTextField();
		JTextField relFitField = new JTextField();
		JTextField redDensityField = new JTextField();
		JTextField redNumField = new JTextField();
		JCheckBox directedCheck = new JCheckBox();
		Object[] message={
				"Minimum number of vertices:",minVertField,
				"Maximum number of vertices:",vertNumField,
				"Iterations per graph:",iterNumField,
				"Graphs per vertex number",graphNumField,
				"Mutant relative fitness", relFitField,
				"Density of red sites", redDensityField,
				"Starting red population", redNumField,
				"Directed graph:",directedCheck
		};
		
		int option=JOptionPane.showConfirmDialog(null,message,"Graph options",JOptionPane.OK_CANCEL_OPTION);
		
		//Check that ok was pressed and a value was supplied for number of vertices and number of iterations per graph
		if(option==JOptionPane.OK_OPTION && !vertNumField.getText().equals("")&& !iterNumField.getText().equals("")){
			int vertFloor;
			if(!vertNumField.getText().equals("")){
				vertFloor = Integer.valueOf(minVertField.getText());
			}else{
				vertFloor=0;
			}
			int vertLimit = Integer.valueOf(vertNumField.getText())+1;
			int iterations =Integer.valueOf(iterNumField.getText());
			int graphNumber = Integer.valueOf(graphNumField.getText());
			Boolean isDirected = directedCheck.isSelected();
			int mutantFitness = Integer.valueOf(relFitField.getText());
			int numRed = Integer.valueOf(redNumField.getText());
			double redDensity = Double.valueOf(redDensityField.getText());
			
		    //Initialise a threadPool and an array of investigators to provide each thread with an Investigator runnable
			long startTime = System.nanoTime();
			int numThreads = 4;
			Investigator[] invArray = new Investigator[numThreads];
			ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
			Future[] futureArray = new Future[numThreads];
			
			//Assign the tasks to the threads and let them run
			for(int i=0;i<numThreads;i++){
				invArray[i] = new Investigator(vertLimit,iterations,graphNumber/numThreads,isDirected,mutantFitness,redDensity,numRed,vertFloor);
				futureArray[i] = threadPool.submit(invArray[i]);
			}
			
			threadPool.shutdown();
			
			//Get the futures of the threads and handle specific exceptions
			try{
				for(Future future: futureArray){
					future.get();
				}
			}catch(ExecutionException except){
				//Threads are never interrupted here hence no handling
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
			
			long endTime = System.nanoTime();
			
			//The remaining code is for printing and producing graphs of the results
			UserInterface.textArea.append("\n");	
			for(double val:meanArray){	
				UserInterface.textArea.append(String.valueOf(val));
				UserInterface.textArea.append(" ");
			}
			UserInterface.textArea.append("\n");	
			for(double val:meanError){	
				UserInterface.textArea.append(String.valueOf(val));
				UserInterface.textArea.append(" ");
			}
			UserInterface.textArea.append("\n");
			UserInterface.textArea.append("\n");
			for(double val:fixProbArray){	
				UserInterface.textArea.append(String.valueOf(val));
				UserInterface.textArea.append(" ");
			}
			UserInterface.textArea.append("\n");	
			for(double val:fixProbError){	
				UserInterface.textArea.append(String.valueOf(val));
				UserInterface.textArea.append(" ");
			}
			
			UserInterface.textArea.append("\n");
			UserInterface.textArea.append("Run time in seconds was: ");
			UserInterface.textArea.append(String.valueOf((endTime-startTime)/1000000000f));
			
			JFreeChart chart = new ChartBuilder().plotGraph(meanArray, "", "Number of vertices", "Mean fixation time");
			ChartPanel chartPanel = new ChartPanel(chart);
			JFrame chartFrame = new JFrame();
			chartFrame.add(chartPanel);
			chartFrame.pack();
			chartFrame.setVisible(true);
			
			JFreeChart chart2 = new ChartBuilder().plotGraph(fixProbArray, "", "Number of vertices", "Fixation probability");
			ChartPanel chartPanel2 = new ChartPanel(chart2);
			JFrame chartFrame2 = new JFrame();
			chartFrame2.add(chartPanel2);
			chartFrame2.pack();
			chartFrame2.setVisible(true);
		}
	}
	
	private void multiInvestigation(){
		JTextField minVertField = new JTextField();
		JTextField vertNumField = new JTextField();
		JTextField iterNumField = new JTextField();
		JTextField graphNumField = new JTextField();
		JTextField relFitField = new JTextField();
		JCheckBox directedCheck = new JCheckBox();
		Object[] message={
				"Minimum number of vertices:",minVertField,
				"Maximum number of vertices:",vertNumField,
				"Iterations per graph:",iterNumField,
				"Graphs per vertex number",graphNumField,
				"Mutant relative fitness", relFitField,
				"Directed graph:",directedCheck
		};
		
		int option=JOptionPane.showConfirmDialog(null,message,"Graph options",JOptionPane.OK_CANCEL_OPTION);
		
		//Check that ok was pressed and a value was supplied for number of vertices and number of iterations per graph
		if(option==JOptionPane.OK_OPTION && !vertNumField.getText().equals("")&& !iterNumField.getText().equals("")){
			int vertFloor;
			if(!vertNumField.getText().equals("")){
				vertFloor = Integer.valueOf(minVertField.getText());
			}else{
				vertFloor=0;
			}
			int vertLimit = Integer.valueOf(vertNumField.getText())+1;
			int iterations =Integer.valueOf(iterNumField.getText());
			int graphNumber = Integer.valueOf(graphNumField.getText());
			Boolean isDirected = directedCheck.isSelected();
			int mutantFitness = Integer.valueOf(relFitField.getText());
			
		    //Initialise a threadPool and an array of investigators to provide each thread with an Investigator runnable
			long startTime = System.nanoTime();
			int numThreads = 4;
			Investigator[] invArray = new Investigator[numThreads];
			ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
			Future[] futureArray = new Future[numThreads];
			
			//Assign the tasks to the threads
			for(int i=0;i<numThreads;i++){
				invArray[i] = new Investigator(vertLimit,iterations,graphNumber/numThreads,isDirected,mutantFitness,vertFloor);
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
			
			long endTime = System.nanoTime();
			
			//The remaining code is for printing and producing graphs of the results
			UserInterface.textArea.append("\n");	
			for(double val:meanArray){	
				UserInterface.textArea.append(String.valueOf(val));
				UserInterface.textArea.append(" ");
			}
			UserInterface.textArea.append("\n");	
			for(double val:meanError){	
				UserInterface.textArea.append(String.valueOf(val));
				UserInterface.textArea.append(" ");
			}
			UserInterface.textArea.append("\n");
			UserInterface.textArea.append("\n");
			for(double val:fixProbArray){	
				UserInterface.textArea.append(String.valueOf(val));
				UserInterface.textArea.append(" ");
			}
			UserInterface.textArea.append("\n");	
			for(double val:fixProbError){	
				UserInterface.textArea.append(String.valueOf(val));
				UserInterface.textArea.append(" ");
			}
			
			JFreeChart chart = new ChartBuilder().plotGraph(meanArray, "", "Number of vertices", "Mean fixation time");
			ChartPanel chartPanel = new ChartPanel(chart);
			JFrame chartFrame = new JFrame();
			chartFrame.add(chartPanel);
			chartFrame.pack();
			chartFrame.setVisible(true);
			
			JFreeChart chart2 = new ChartBuilder().plotGraph(fixProbArray, "", "Number of vertices", "Fixation probability");
			ChartPanel chartPanel2 = new ChartPanel(chart2);
			JFrame chartFrame2 = new JFrame();
			chartFrame2.add(chartPanel2);
			chartFrame2.pack();
			chartFrame2.setVisible(true);
		}
	}
}