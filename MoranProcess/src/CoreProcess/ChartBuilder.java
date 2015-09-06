package CoreProcess;

import java.util.stream.*;
import java.util.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

/*
 * Class to allow you to build a chart using JFreeChart from a supplied int array of data. Although very sparse at the moment, this is
 * implemented as its own class to allow for future expansion of different types / formats of graph
 */
public class ChartBuilder{
	
	
	private XYDataset createDataSet(int[] data){
		 DefaultXYDataset dataSet = new DefaultXYDataset();
		 
		 double[] iterations = IntStream.range(0,data.length).mapToDouble(Double::valueOf).toArray();
		 double[] doubleData = Arrays.stream(data).mapToDouble(Double::valueOf).toArray();
		 double[][] formattedData = {iterations,doubleData};
		 dataSet.addSeries("Series1",formattedData);
		 return dataSet;
	}
	
	private XYDataset createDataSet(double[] data){
		 DefaultXYDataset dataSet = new DefaultXYDataset();
		 
		 double[] iterations = IntStream.range(0,data.length).mapToDouble(Double::valueOf).toArray();
		 double[] doubleData = Arrays.stream(data).toArray();
		 double[][] formattedData = {iterations,doubleData};
		 dataSet.addSeries("Series1",formattedData);
		 return dataSet;
	}
	
	public JFreeChart plotGraph(int[] data,String title, String xLabel,String yLabel){
		XYDataset ds = createDataSet(data);
		
		JFreeChart chart = ChartFactory.createXYLineChart(title,xLabel,yLabel,ds,PlotOrientation.VERTICAL,true,true,false);
		return chart;
	}
	
	public JFreeChart plotGraph(double[] data,String title, String xLabel,String yLabel){
		XYDataset ds = createDataSet(data);
		
		JFreeChart chart = ChartFactory.createXYLineChart(title,xLabel,yLabel,ds,PlotOrientation.VERTICAL,true,true,false);
		return chart;
	}
	
}