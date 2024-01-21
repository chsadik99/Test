

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class Test1 {
	BuildExpressionForCron obj;
	 @Before                                         
	   public void setUp() {
		 obj = new BuildExpressionForCron();
	    }

	 @Test
		public void NoCronExprssion() {
			String []arr = new String[0];
			String ret = obj.buildCronExp(arr);
			assertEquals("No cron expression provided to parse",ret);
			
		}

		@Test
		public void IncompleteCronExpr() {
			String []arr = new String[] {"* *"};
			String ret = obj.buildCronExp(arr);
			assertEquals("Insufficient numbers of parameters passed.",ret);
			
		}
		
		@Test
		public void CorrectTest1() {
			String expectedOutput = "minute        0 15 30 45\n"
					+ "hour          8 9 10 11 12 13 14 15 16 17\n"
					+ "day of month  1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31\n"
					+ "month         1 2 3 4 5 6 7 8 9 10 11 12\n"
					+ "day of week   1 2 3 4 5\n"
					+ "command       /usr/bin/jaba";
			
			String cronExpr = "*/15 8-17 * * 1-5 /usr/bin/jaba";
			

			String []args = new String[] {cronExpr};
			String actualOuput = obj.buildCronExp(args);
			assertEquals(expectedOutput,actualOuput);
			
		}
		
		@Test
		public void InvalidRangeForHour() {
			String expectedOutput = "Invalid format for hour range. Invalid start/end value for range expr.";
			
			String cronExpr = "*/15 8-90 * * 1-5 /usr/bin/jaba";
			

			String []args = new String[] {cronExpr};
			String actualOuput = obj.buildCronExp(args);
			assertEquals(expectedOutput,actualOuput);
			
		}
		
		@Test
		public void InvalidtypeForMonth() {
			String expectedOutput = "Invalid format for month interval. df should be an integer ";
			
			String cronExpr = "*/15 8-9 * */df 1-5 /usr/bin/jaba";
			

			String []args = new String[] {cronExpr};
			String actualOuput = obj.buildCronExp(args);
			assertEquals(expectedOutput,actualOuput);
			
		}
		
		@Test
		public void CorrectTest2() {
			String expectedOutput = "minute        20 35 50\n"
					+ "hour          8 9\n"
					+ "day of month  6 11 16 21 26 31\n"
					+ "month         1 2 3 4 5\n"
					+ "day of week   0 1 2 3 4 5 6\n"
					+ "command       /usr/bin/jaba";
			
			String cronExpr = "20/15 8-9 6/5 1-5 * /usr/bin/jaba";
			

			String []args = new String[] {cronExpr};
			String actualOuput = obj.buildCronExp(args);
			assertEquals(expectedOutput,actualOuput);
			
		}

	}