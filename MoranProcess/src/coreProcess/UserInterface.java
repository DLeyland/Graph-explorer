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

public class UserInterface extends JPanel{
	
	static JFileChooser fc;
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
	static JTextArea textArea;
	JScrollPane scrollPane;
	static List<List<Integer>> matrix;
	
	
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
		loadGraph.addActionListener(new FileListener("load"));
		file.add(loadGraph);
		
		//create and add the menu items for the process menu
		moranProc = new JMenuItem("Perform evolutionary process");
		moranProc.addActionListener(new ProcessListener(GraphType.Random));
		process.add(moranProc);
		simulation = new JMenuItem("Run simulation");
		simulation.addActionListener(new SimulationListener(GraphType.Random));
		process.add(simulation);
		compSim = new JMenuItem("Run complete graph simulation");
		compSim.addActionListener(new SimulationListener(GraphType.Complete));
		process.add(compSim);
		investigation = new JMenuItem("Perform investigation");
		investigation.addActionListener(new InvestigationListener(SimulationType.MORAN));
		process.add(investigation);
		envInvestigation = new JMenuItem("Perform environmental investigation");
		envInvestigation.addActionListener(new InvestigationListener(SimulationType.ENVIRONMENTAL));
		process.add(envInvestigation);
		multiInvestigation = new JMenuItem("Perform multi-mutant investigation");
		multiInvestigation.addActionListener(new InvestigationListener(SimulationType.MULTIPLE));
		process.add(multiInvestigation);
		
		//create and add the menu items for the generate menu
		completeGraph = new JMenuItem("Generate a complete graph");
		completeGraph.addActionListener(new GraphGenerationListener(GraphType.Complete));
		generate.add(completeGraph);
		randomGraph = new JMenuItem("Random graph");
		randomGraph.addActionListener(new GraphGenerationListener(GraphType.Random));
		generate.add(randomGraph);
		kFunnel = new JMenuItem("Generate a k-funnel graph");
		kFunnel.addActionListener(new GraphGenerationListener(GraphType.KFunnel));
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