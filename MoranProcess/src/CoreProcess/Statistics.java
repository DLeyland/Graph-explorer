package CoreProcess;

import java.util.*;
import java.util.stream.*;

/*
 *Class contains methods used to generate statistical information about data 
 *
 */
public class Statistics{
	
	/*
	 * Returns the mean value of an array
	 */
	public double mean(int[] data){
		
		double sum = Arrays.stream(data).sum();

		return sum/data.length;
	}
	
	/*
	 * Returns the mean for a given frequency distribution
	 */
	public double freqMean(int[] data){

		int runningIterTotal = IntStream.range(0,data.length).map( i -> i*data[i]).sum();
		int runningNumTotal = Arrays.stream(data).sum();

		return (double)runningIterTotal/runningNumTotal;
	
	}
	
	/*
	 * Returns the standard error of a sample from a binomial distributon
	 */
	public double binomialStandardError(double p, int numSamples){
		
		return Math.sqrt(p*(1-p)/numSamples);
	}
	
	/*
	 * Returns the standard deviation of a given array
	 */
	public double standardDeviation(int[] data){
		double mean = mean(data);
		double[] variance = new double[data.length]; 
		for(int x=0;x<data.length;x++){
			variance[x]=Math.pow(data[x]-mean,2.0);
		}
		
		double sum = Arrays.stream(variance).sum();
				     
		return Math.sqrt(sum);
	}
	
	/*
	 * Returns the standard deviation of a given frequency distribution
	 */
	public double freqStandardDeviation(int[] data){
		
		int f = Arrays.stream(data).sum();
		int xf = IntStream.range(0,data.length).map(i->i*data[i]).sum();
		int x2f = IntStream.range(0, data.length).map(i->i*i*data[i]).sum();
		
		double var = x2f - ((xf*xf)/f);
		return Math.sqrt(var/f);
	}
	
	public double freqStandError(int[] data){
		
		int f = Arrays.stream(data).sum();
		int xf = IntStream.range(0,data.length).map(i->i*data[i]).sum();
		int x2f = IntStream.range(0, data.length).map(i->i*i*data[i]).sum();

		if(f!=0){
			double var = x2f - ((xf*xf)/f);
			return Math.sqrt(var)/f;
		}else{
			return 0;
		}
	}
}