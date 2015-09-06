package coreProcess;
import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.event.MenuKeyListener;
import javax.swing.event.MenuKeyEvent;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.awt.event.*;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;

public class UserInterface extends JPanel implements ActionListener{
	
	JFileChooser fc;
	JMenuBar menuBar;
	JMenu file;
	JMenu process;
	JMenu generate;
	JMenuItem loadGraph;
	JMenuItem moranProc;
	JMenuItem simulation;
	JMenuItem investigation; 
	JMenuItem compSim;
	JMenuItem completeGraph;
	JMenuItem randomGraph;
	JMenuItem kFunnel;
	JMenuItem envInvestigation;
	JMenuItem multiInvestigation;
	JTextArea textArea;
	JScrollPane scrollPane;
	List<List<Integer>> matrix;
	
	
	public UserInterface(){
		super(new BorderLayout());
		
		//create the menuBar and menus then add the menus to the menuBar.
		menuBar = new JMenuBar();
		file = new JMenu("File");
		process = new JMenu("Process");
		generate= new JMenu("Generate graph");
		menuBar.add(file);
		menuBar.add(process);
		menuBar.add(generate);
		
		
		//create and add the menu items for the file menu
		loadGraph = new JMenuItem("Load Graph");
		loadGraph.addActionListener(this);
		file.add(loadGraph);
		
		//create and add the menu items for the process menu
		moranProc = new JMenuItem("Perform evolutionary process");
		moranProc.addActionListener(this);
		process.add(moranProc);
		simulation = new JMenuItem("Run simulation");
		simulation.addActionListener(this);
		process.add(simulation);
		compSim = new JMenuItem("Run complete graph simulation");
		compSim.addActionListener(this);
		process.add(compSim);
		investigation = new JMenuItem("Perform investigation");
		investigation.addActionListener(this);
		process.add(investigation);
		envInvestigation = new JMenuItem("Perform environmental investigation");
		envInvestigation.addActionListener(this);
		process.add(envInvestigation);
		multiInvestigation = new JMenuItem("Perform multi-mutant investigation");
		multiInvestigation.addActionListener(this);
		process.add(multiInvestigation);
		
		//create and add the menu items for the generate menu
		completeGraph = new JMenuItem("Generate a complete graph");
		completeGraph.addActionListener(this);
		generate.add(completeGraph);
		randomGraph = new JMenuItem("Random graph");
		randomGraph.addActionListener(this);
		generate.add(randomGraph);
		kFunnel = new JMenuItem("Generate a k-funnel graph");
		kFunnel.addActionListener(this);
		generate.add(kFunnel);
		
		//create new file chooser
		fc = new JFileChooser();
		
		//create new text area
		textArea = new JTextArea(5,20);
		textArea.setMargin(new Insets(5,20,10,10));
        textArea.setEditable(false);        
        scrollPane = new JScrollPane(textArea); 

        
        //add text area and menuBar to this JPanel
		add(menuBar,BorderLayout.PAGE_START);
		add(scrollPane,BorderLayout.CENTER);
		
		
	}
	
	
	public void actionPerformed(ActionEvent e){
		if (e.getSource()==loadGraph){
			int returnVal = fc.showOpenDialog(UserInterface.this);
			if(returnVal==JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();

				GraphImporter g = new GraphImporter(file);

				matrix = g.getMatrix();

				for(List<Integer> line :matrix){
					textArea.append("\n");
					for(Integer num:line){
						textArea.append(String.valueOf(num));
						textArea.append(" ");
					}
				}
			}
		}
		
		if(e.getSource()==moranProc){
			String resp = JOptionPane.showInputDialog("What is the maximum number of iterations you would like?",null);
			
			MoranProcess m = new MoranProcess(matrix);
			int iter;
			//Have to do this check as can't do valueOf("") and want it such that if no value for max. number of iterations is given the process will continue until completion
			if(resp.equals("")){
				iter = m.runProcess();
			}else{
				iter = m.runProcess(Integer.valueOf(resp));
			}
			
			textArea.append("\n");
			for(List<Integer> line:m.getMatrix()){
				textArea.append("\n");
				for(Integer num:line){
					textArea.append(String.valueOf(num));
					textArea.append(" ");
				}
			}
			textArea.append("\n");
			textArea.append("Number of iterations required for absorption: " + iter);
		}
		
		if(e.getSource()==simulation){
			String resp = JOptionPane.showInputDialog("How many times would you like to run the simulation",null);
			
			if(resp.equals("")){
			}else{
				
				int iter = Integer.valueOf(resp);
				Simulator s = new Simulator();
				s.moranSimulation2(iter,matrix,2);
				int[] results = s.getFixationTimes();
				
				textArea.append("\n");
				textArea.append("Starting simulation: ");
				textArea.append("\n");
				textArea.append("Selected parameters were: Number of runs of Moran process =" +String.valueOf(iter));
				textArea.append("\n");
				textArea.append("\n");
				textArea.append("Frequency array of number of runs that required that number of iterations to reach fixation:");
				textArea.append("\n");
				for(int result:results){
					textArea.append(String.valueOf(result));
					textArea.append(" ");
				}
				
				float fixProb=s.getFixationProb();
				Statistics stats = new Statistics();
				double standardError=stats.freqStandError(results);
				double mean = stats.freqMean(results);
				

				JFreeChart chart = new ChartBuilder().plotGraph(results, "", "Number of iterations to fixation", "Frequency");
				ChartPanel chartPanel = new ChartPanel(chart);
				JFrame chartFrame = new JFrame();
				chartFrame.add(chartPanel);
				chartFrame.pack();
				chartFrame.setVisible(true);
				
				//Print the fixation probability
				textArea.append("\n");
				textArea.append("Fixation probability: ");
				textArea.append(String.valueOf(fixProb));
				
				//Print the mean with standard error
				textArea.append("\n");
				textArea.append("Mean: ");
				textArea.append(String.valueOf(mean));
				textArea.append("+-");
				textArea.append(String.valueOf(standardError));
			}
		}
		
		if(e.getSource()==investigation){
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
				long startTime = System.nanoTime();
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
				
				//Wait till all the threads are finished, note this could cause the application to hang for the user if the threads deadlock
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
				
				long endTime = System.nanoTime();
				
				//The remaining code is for printing and producing graphs of the results
				textArea.append("\n");	
				for(double val:meanArray){	
					textArea.append(String.valueOf(val));
					textArea.append(" ");
				}
				textArea.append("\n");	
				for(double val:meanError){	
					textArea.append(String.valueOf(val));
					textArea.append(" ");
				}
				textArea.append("\n");
				textArea.append("\n");
				for(double val:fixProbArray){	
					textArea.append(String.valueOf(val));
					textArea.append(" ");
				}
				textArea.append("\n");	
				for(double val:fixProbError){	
					textArea.append(String.valueOf(val));
					textArea.append(" ");
				}
				
				textArea.append("\n");
				textArea.append("Run time in seconds was: ");
				textArea.append(String.valueOf((endTime-startTime)/1000000000f));
				
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
		
		if(e.getSource()==multiInvestigation){
			
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
				textArea.append("\n");	
				for(double val:meanArray){	
					textArea.append(String.valueOf(val));
					textArea.append(" ");
				}
				textArea.append("\n");	
				for(double val:meanError){	
					textArea.append(String.valueOf(val));
					textArea.append(" ");
				}
				textArea.append("\n");
				textArea.append("\n");
				for(double val:fixProbArray){	
					textArea.append(String.valueOf(val));
					textArea.append(" ");
				}
				textArea.append("\n");	
				for(double val:fixProbError){	
					textArea.append(String.valueOf(val));
					textArea.append(" ");
				}
				
				textArea.append("\n");
				textArea.append("Run time in seconds was: ");
				textArea.append(String.valueOf((endTime-startTime)/1000000000f));
				
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
		
		if(e.getSource()==envInvestigation){
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
				textArea.append("\n");	
				for(double val:meanArray){	
					textArea.append(String.valueOf(val));
					textArea.append(" ");
				}
				textArea.append("\n");	
				for(double val:meanError){	
					textArea.append(String.valueOf(val));
					textArea.append(" ");
				}
				textArea.append("\n");
				textArea.append("\n");
				for(double val:fixProbArray){	
					textArea.append(String.valueOf(val));
					textArea.append(" ");
				}
				textArea.append("\n");	
				for(double val:fixProbError){	
					textArea.append(String.valueOf(val));
					textArea.append(" ");
				}
				
				textArea.append("\n");
				textArea.append("Run time in seconds was: ");
				textArea.append(String.valueOf((endTime-startTime)/1000000000f));
				
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
		
		if(e.getSource()==completeGraph){
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
				matrix=new GraphGenerator().completeGraph(numVerts,numVerts/2 ,relFitness);
				
				//Print the matrix we produced
				textArea.append("\n");
				for(List<Integer> line :matrix){
					textArea.append("\n");
					for(Integer num:line){
						textArea.append(String.valueOf(num));
						textArea.append(" ");
					}
				}
			}	
		}
		
		if(e.getSource()==randomGraph){
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
				matrix = new GraphGenerator().randomGraph(numVerts, isDirected, mutantFitness);
				textArea.append("Generating new random graph with " + String.valueOf(numVerts) + " vertices:");
				textArea.append("\n");
				for(List<Integer> line :matrix){
					textArea.append("\n");
					for(Integer num:line){
						textArea.append(String.valueOf(num));
						textArea.append(" ");
					}
				}
				textArea.append("\n");
			}
		}
		
		if(e.getSource()==kFunnel){
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
				matrix = new GraphGenerator().kFunnel(branchFactor,kValue);
				textArea.append("Generating new k-funnel graph");
				textArea.append("\n");
				for(List<Integer> line :matrix){
					textArea.append("\n");
					for(Integer num:line){
						textArea.append(String.valueOf(num));
						textArea.append(" ");
					}
				}
				textArea.append("\n");
			}
		}
		
		if(e.getSource()==compSim){
			
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
						matrix = new GraphGenerator().completeGraph(numVerts, x, relFitness);
						s.moranSimulation(iters, matrix);
						runningTotal=runningTotal + s.getFixationProb();
					}
					float fixationProb = runningTotal / numVerts;
					
					textArea.append("\n");
					textArea.append(String.valueOf(fixationProb));
				}
			}
			
		}
		
	}
	
	
	public static void main(String args[]){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run(){
				createAndShowGUI();
			}
		});
	}
	
	
	private static void createAndShowGUI(){
		JFrame frame = new JFrame("Graph explorer");
		
		frame.add(new UserInterface());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
						
	}


}