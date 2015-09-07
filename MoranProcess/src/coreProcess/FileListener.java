package coreProcess;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class FileListener implements ActionListener {

	String type;
	
	public FileListener(String type){
		this.type = type;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(type.equals("load")){
			loadGraph();
		}

	}
	
	private void loadGraph(){

		int returnVal = UserInterface.fc.showOpenDialog(null);
		if(returnVal==JFileChooser.APPROVE_OPTION){
			File file = UserInterface.fc.getSelectedFile();

			GraphImporter g = new GraphImporter(file);

			UserInterface.matrix = g.getMatrix();

			for(List<Integer> line :UserInterface.matrix){
				UserInterface.textArea.append("\n");
				for(Integer num:line){
					UserInterface.textArea.append(String.valueOf(num));
					UserInterface.textArea.append(" ");
				}
			}
		}
	}
	
}
