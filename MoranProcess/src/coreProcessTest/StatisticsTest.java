package coreProcessTest;

import static org.junit.Assert.assertEquals;

import java.util.stream.IntStream;

import org.junit.Test;

import coreProcess.Statistics;


public class StatisticsTest {	
	
	@Test
	public void meanTest() {
		Statistics s = new Statistics();
		double expected = s.mean(new int[]{1,2,3});
		assertEquals(2,expected,0.0);
	}

	
}
