

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CronField {
	protected String name;
	protected List<String> validIntValues;
	protected List<String> validTextValues;
	private String data;
	
	public CronField(String data) {
		this.data = data;
		this.validIntValues = new ArrayList<>();
		this.validTextValues = new ArrayList<>();
	}
	
	
	
	
	public String getName() {
		return name;
	}
	
	public List<String> getValidIntValues() {
		return validIntValues;
	}
	
	public  List<String> getValidTextValues() {
		return validTextValues;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	
	public String getData() {
		return data;
	}
}

class DayCronField extends CronField{

	public DayCronField(String data) {
		super(data);
		name = "day of month";
		for(int i=1;i<=31;i++)
				this.validIntValues.add(i+"");
	}
	
}

 class HourCronField extends CronField{

	public HourCronField(String data) {
		super(data);
		this.name = "hour";
		for(int i=0;i<24;i++)
			this.validIntValues.add(i +"");
	}
	
}

class MinuteCronField extends CronField{

	public MinuteCronField(String val) {
		super(val);
		this.name = "minute";
		for(int i=0;i<60;i++)
			this.validIntValues.add(i+"");
	}
	
	
	
}

class MonthCronField extends CronField{

	public MonthCronField(String data) {
		super(data);
		 name = "month";
		for(int i=1;i<=12;i++)
			this.validIntValues.add(i + "");
		validTextValues =   Arrays.asList("JAN",
	            "FEB",
	            "MAR",
	            "APR",
	            "MAY",
	            "JUN",
	            "JUL",
	            "AUG",
	            "SEP",
	            "OCT",
	            "NOV",
	            "DEC"
	            );
		
	
	}
	
	
}

class WeekDayCronField extends CronField{

	public WeekDayCronField(String data) {
		super(data);
		name = "day of week";
		for(int i=0;i<7;i++)
				validIntValues.add(i+"" );
		
		validTextValues = Arrays.asList(  "SUN",
	            "MON",
	            "TUE",
	            "WED",
	            "THU",
	            "FRI",
	            "SAT"
	            );
	}
	
}


public class BuildExpressionForCron {

	String intRegex = "-?\\d+(\\.\\d+)?";
	
	public BuildExpressionForCron() {}
	
	public String buildCronExp(String []args) 
	{
		if(args.length < 1)
			return "No cron expression provided to parse";
		try {
			String []cronFieldValues =  args[0].split(" ",6);
			if(cronFieldValues.length < 6)
				return "Insufficient numbers of parameters passed.";
		CronField []cronField = new CronField[] {new MinuteCronField(cronFieldValues[0]),
				new HourCronField(cronFieldValues[1]),
				new DayCronField(cronFieldValues[2]),
				new MonthCronField(cronFieldValues[3]),
				new WeekDayCronField(cronFieldValues[4])};
		
		StringBuilder finalCronExpression = new StringBuilder();
		
		for(int i=0;i<5;i++)
		{
			finalCronExpression.append(cronField[i].getName() );
			
			addWhiteSpace( finalCronExpression, cronField[i].getName().length());
			
			finalCronExpression.append(  (expand(cronField[i])).trim() + "\n");
			
		}
		finalCronExpression.append("command");
		addWhiteSpace( finalCronExpression,	7);

		finalCronExpression.append( cronFieldValues[5] );

		return finalCronExpression.toString();
		
		}
		catch(Exception ex)
		{
			return ex.getMessage();
		}
		
	}

	private void addWhiteSpace( StringBuilder finalCronExpression, int j) {
		while(j<14)
			{
			finalCronExpression.append(" ");
			j++;
			}
	}
	
	String expand(CronField cronField) throws Exception
	{
		if( cronField.getData().equals("*"))
			return   String.join(" ", cronField.getValidIntValues());
				
		if(cronField.getData().contains(","))
			return expandList(cronField);
		
		 if(cronField.getData().contains("-"))
			return expandInterval(cronField);
		 
		if(cronField.getData().contains("/"))
			return expandData(cronField);
		
		if(cronField.getValidIntValues().contains(cronField.getData()) ||  cronField.getValidTextValues().contains(cronField.getData()) )
			return cronField.getData();
		
		throw new Exception("Not Valid Data");
		
	}
	
	private String expandData(CronField cronField) throws Exception{
		String []interv = cronField.getData().split("/");
		StringBuilder cronExp = new StringBuilder();
		if(interv.length!=2)
			{
			throw new Exception("Invalid format for " + cronField.getName() +" only 2 fileds allowed");
			}
		if(!interv[1].matches(intRegex) )
			{
			throw new Exception("Invalid format for " + cronField.getName() +" interval. "+ interv[1] +" should be an integer ");
			}

		int multiple =  Integer.parseInt(interv[1]); 
		
		if(interv[0].equals("*") )
		{
			for(String ch1: cronField.getValidIntValues())
			{
				if( Integer.parseInt(ch1) %multiple==0 )
					{
					cronExp.append( ch1 + " ");
					}
				
			}
		}
		
		else if(!interv[1].matches(intRegex) )
			{
			throw new Exception("Invalid format for " + cronField.getName() +" interval. "+ interv[1] +" should be an integer or '*'");
			}

		else {
			int start = Integer.parseInt(interv[0]); 
			for(String ch1: cronField.getValidIntValues())
			{
				if( Integer.parseInt(ch1) >= start && (Integer.parseInt(ch1)-start) %multiple==0 )
					{
					cronExp.append( ch1 + " ");
					}
					
			}
		}
	      if(cronExp.length() == 0 )
			throw new Exception(interv[0] + " value should be less then the accepted value for field "+cronField.getName());
		
		return cronExp.toString();
	}

	private String expandInterval(CronField cronField) throws Exception {
		String []interv = cronField.getData().split("-");
		
		if(interv.length!=2)
			{
			throw new Exception("Invalid format for " + cronField.getName() +" only 2 fields allowed.");
			}
	
		
		if(interv[0].matches(intRegex) != interv[1].matches(intRegex))
			{
			throw new Exception("Invalid format for " + cronField.getName() +" range. Both should be of same type");
			}
		
		int start=0,end=0;
		if(interv[0].matches(intRegex))
		{
			start = cronField.getValidIntValues().indexOf(interv[0]);
			end = cronField.getValidIntValues().indexOf(interv[1]);
		}
		else {
			start = cronField.getValidTextValues().indexOf(interv[0]);
			end = cronField.getValidTextValues().indexOf(interv[1]);
		}
		
		if(start==-1 || end==-1)
			{
				throw new Exception("Invalid format for " + cronField.getName() +" range. Invalid start/end value for range expr.");
			}
		
		if(start>end)
			{
			throw new Exception("Invalid format for " + cronField.getName() +" range. "+start +"  should be <= " +end);
			}
		
		return String.join(" ",  cronField.getValidIntValues().subList(start, end+1) );
				
	}

	private String expandList(CronField cronField) throws Exception{
		String []list = cronField.getData().split(",");
		boolean isIntType = list[0].matches(intRegex);
		StringBuilder cronExp = new StringBuilder();
		
		for(String val:list)
		{
			if(val.matches(intRegex)!=isIntType)
			{
				throw new Exception("Invalid format for "+ cronField.getName() +" list. All values of list should have the same type.");
			}
			else if(isIntType)
			{
				if(cronField.getValidIntValues().contains(val)  )
					{
					cronExp.append(val + " ");
					}
				else
					{
					throw new Exception("Invalid format for "+ cronField.getName() +" list."+val +" not in allowed integer values for "+ cronField.getName());
					}

			}
			else {
				if(cronField.getValidTextValues().contains(val)  )
					{
					cronExp.append(val + " ");
					}
				else
					{
					throw new Exception("Invalid format for "+ cronField.getName() +" list."+val +" not in allowed text values for "+ cronField.getName());
					}

			}
		}
		return cronExp.toString() ;
	}

	
}