package coreProcess;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JOptionPane;

public class ProcessListener implements ActionListener {

	GraphType type;
	
	public ProcessListener(GraphType type){
		this.type = type;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(type.equals(GraphType.Random)){
			randomGraphProcess();
		}

	}
	
	private void randomGraphProcess(){
		
		String resp = JOptionPane.showInputDialog("What is the maximum number of iterations you would like?",null);
		
		MoranProcess m = new MoranProcess(UserInterface.matrix);
		int iter;
		//Have to do this check as can't do valueOf("") and want it such that if no value for max. number of iterations is given the process will continue until completion
		if(resp.equals("")){
			iter = m.runProcess();
		}else{
			iter = m.runProcess(Integer.valueOf(resp));
		}
		
		UserInterface.textArea.append("\n");
		for(List<Integer> line:m.getMatrix()){
			UserInterface.textArea.append("\n");
			for(Integer num:line){
				UserInterface.textArea.append(String.valueOf(num));
				UserInterface.textArea.append(" ");
			}
		}
		UserInterface.textArea.append("\n");
		UserInterface.textArea.append("Number of iterations required for absorption: " + iter);
	}
	
}
