/**
 * @author ${hisham_maged10}
 *https://github.com/hisham-maged10
 * ${DesktopApps}
 */
import java.util.ArrayList;
import java.io.File;
import java.util.Scanner;
import java.util.NoSuchElementException;
import static java.lang.System.out;
import static java.lang.System.err;
public class TestWeatherCSV
{
	public static void main(String[] args)
	{
		testing();
	}
	public static void printColumnPropertyDay(CSVParser csvParser,ColumnChoice colChoice,PropertyChoice propertyChoice)
	{
		String[] columnArr=null;
		if(colChoice.toString().equalsIgnoreCase("temperature"))
		columnArr=csvParser.get("temp");
		else if(colChoice.toString().equalsIgnoreCase("humidity"))
		columnArr=csvParser.get("humid");
		String[] hourArr=csvParser.get("time");
		String[] dayArr=csvParser.get("date");
		if(columnArr==null || hourArr==null || dayArr==null)throw new CSVFileNotLoadedException("The CSV file was not loaded successfully!");	
		int index=getPropertyDay(columnArr,colChoice,propertyChoice);
		if(index==-1){System.out.println("Property doesn't exist!"); return;}
		System.out.println(propertyChoice.toString().toLowerCase()+" "+colChoice.toString().toLowerCase()+" on "+refractorDateUTC(dayArr[index])+" was: "+columnArr[index]+" F"+" at "+hourArr[index]);
	}
	private static int getPropertyDay(String[] columnArr,ColumnChoice colChoice,PropertyChoice propertyChoice)
	{
		double property=getDoublePropertyFromStringArr(columnArr,colChoice,propertyChoice);
		return getPropertyIndexFromStringArr(columnArr,property,colChoice);
	}
	private static double getDoublePropertyFromStringArr(String[] strArr,ColumnChoice colChoice,PropertyChoice propertyChoice)
	{
		int j=0;
		double property=-1;
		do{
		property=(!strArr[j++].equalsIgnoreCase("n/a"))?Double.parseDouble(strArr[(j-1)]):-1;;
		}while(property==-1);
		double temp;
		for(int i=j;i<strArr.length;i++)
		{
			switch(propertyChoice)
			{
			case HIGHEST:	if(property<(temp=(!strArr[i].equalsIgnoreCase("n/a"))?Double.parseDouble(strArr[i]):property-1))
					{	if(colChoice.toString().equalsIgnoreCase("temperature")&&validateTemp(temp))
						property=temp;
						else if(colChoice.toString().equalsIgnoreCase("humidity"))
						property=temp;
					} break;	
			case LOWEST:	if(property>(temp=(!strArr[i].equalsIgnoreCase("n/a"))?Double.parseDouble(strArr[i]):property+1))
					{	if(colChoice.toString().equalsIgnoreCase("temperature")&&validateTemp(temp))
						property=temp;
						else if(colChoice.toString().equalsIgnoreCase("humidity"))
						property=temp;
					} break;
			}
		}	
		return property;
	}
	private static int getPropertyIndexFromStringArr(String[] strArr,double property,ColumnChoice colChoice)
	{
		String propertyStr=null;
		switch(colChoice)
		{
			case TEMPERATURE:propertyStr=Double.toString(property); break;
			case HUMIDITY:propertyStr=Integer.toString((int)property); break;
		}
		for(int i=0;i<strArr.length;i++)
		{
			if(strArr[i].equalsIgnoreCase(propertyStr))
			return i;
		}	
		return -1;
	}
	
	public static void printColumnPropertyLargeScale(ColumnChoice colChoice,PropertyChoice propertyChoice)
	{
		ArrayList<CSVParser> csvArr=CSVParser.makeArrayList(true);
		MutableDouble property=new MutableDouble();
		String[] columnArr=null;
		int csvArrIndex=getColumnPropertyCSVArr(csvArr,property,colChoice,propertyChoice);
		int columnArrIndex=getColumnPropertyCSVStrArr(csvArr,csvArrIndex,property,colChoice);
		if(columnArrIndex==-1){System.out.println("property not found!");return;}
		if(colChoice.toString().equalsIgnoreCase("temperature"))
		columnArr=getNeededArr(csvArr,csvArrIndex,"temp");
		else if(colChoice.toString().equalsIgnoreCase("humidity"))
		columnArr=getNeededArr(csvArr,csvArrIndex,"humid");
		String[] hourArr=getNeededArr(csvArr,csvArrIndex,"time");
		String[] dayArr=getNeededArr(csvArr,csvArrIndex,"date");
		if(columnArr==null|| hourArr==null || dayArr==null) throw new CSVFileNotLoadedException("The CSV file was not loaded successfully!"); 
		String superlativeAdj=(colChoice.toString().equalsIgnoreCase("temperature"))?
				 (propertyChoice.toString().equalsIgnoreCase("highest")?"Hottest":"Coldest"):
				 (propertyChoice.toString().equalsIgnoreCase("highest")?"Humidest":"Dampest");
		System.out.println(superlativeAdj+" day was in file "+csvArr.get(csvArrIndex).getCsvFileName());
		System.out.println(propertyChoice.toString().toLowerCase()+" "+colChoice.toString().toLowerCase()+" on that day was "+columnArr[columnArrIndex]+" F"+" at "+hourArr[columnArrIndex]);
		System.out.println("All the "+colChoice.toString().toLowerCase()+" on that day were: (Date in UTC)");
		for(int i=0;i<dayArr.length;i++)
		{
			System.out.println(dayArr[i]+":"+" "+columnArr[i]);
		}
	}
	public static int getColumnPropertyCSVArr(ArrayList<CSVParser> csvArr,MutableDouble property,ColumnChoice colChoice,PropertyChoice propertyChoice)
	{
		String[] columnArr=null;
		String colName=(colChoice.toString().equalsIgnoreCase("temperature"))?"temp":"humid";
		columnArr=csvArr.get(0).get(colName);
		property.setDoubleValue(getDoublePropertyFromStringArr(columnArr,colChoice,propertyChoice));
		double temp;
		int neededIndex=-1;
		for(int i=0,n=csvArr.size();i<n;i++)
		{
			columnArr=csvArr.get(i).get(colName);
			switch(propertyChoice)
			{
			case HIGHEST: if(property.getDoubleValue()<(temp=getDoublePropertyFromStringArr(columnArr,colChoice,propertyChoice)))
					{ property.setDoubleValue(temp); neededIndex=i; }
					break;
			case LOWEST: if(property.getDoubleValue()>(temp=getDoublePropertyFromStringArr(columnArr,colChoice,propertyChoice)))
					{ property.setDoubleValue(temp); neededIndex=i; }
					break;
			}
		}
		return neededIndex;
	}
	private static int getColumnPropertyCSVStrArr(ArrayList<CSVParser> csvArr,int csvArrIndex,MutableDouble property,ColumnChoice colChoice)
	{
		String colName=(colChoice.toString().equalsIgnoreCase("temperature"))?"temp":"humid";
		String[] neededTempStrArr=csvArr.get(csvArrIndex).get(colName);
		for(int i=0;i<neededTempStrArr.length;i++)
		{
			if(Double.parseDouble(neededTempStrArr[i])==property.getDoubleValue())
			return i;
		}	
		return -1;
	}
	public static void printAverageTempFile(CSVParser csvParser)
	{
		System.out.println("Average temperature in file is "+getAverageTempFile(csvParser));
	}
	private static double getAverageTempForHumidityFile(CSVParser csvParser,int value)
	{
		String[] tempArr=csvParser.get("temp");
		String[] humidArr=csvParser.get("humid");
		return computeAverageTemp(tempArr,humidArr,value);
	}
	private static double getAverageTempFile(CSVParser csvParser)
	{
		String[] tempArr=csvParser.get("temp");
		return computeAverageTemp(tempArr);
	}
	private static double computeAverageTemp(String[] arr,String[] humidArr,int humidityValue)
	{
		double sum=0.0;
		int count=0;
		for(int i=0;i<arr.length;i++)
		{
			if(Integer.parseInt(humidArr[i])>=humidityValue)
			{
			sum+=Double.parseDouble(arr[i]);		
			count++;
			}
		}
		return sum/count;
	}
	public static void printAverageTempForHumidity(CSVParser csvParser)
	{
		int humidVal=-1;
		out.print("Enter the wanted humidity: ");
		try{humidVal=Integer.parseInt(new Scanner(System.in).nextLine());}
		catch(NumberFormatException ex){out.println("Incorrect Response");guiController(csvParser);}	
		catch(NoSuchElementException ex){System.out.println("Termination by outer source, exiting"); System.exit(0);}
		double averageTemp=getAverageTempForHumidityFile(csvParser,humidVal);
		if(averageTemp!=0)
		System.out.println("Average Temp with Humidity greater than or equal to: "+humidVal+" is "+averageTemp);
		else
		System.out.println("No temperatures with humidity greater than or equal to: "+humidVal);
			
	}
	private static double computeAverageTemp(String[] arr)
	{
		double sum=0.0;
		for(int i=0;i<arr.length;i++)
		{
			sum+=Double.parseDouble(arr[i]);		
		}
		return sum/arr.length;
	}
	private static String refractorDateUTC(String str)
	{
		return new java.util.Scanner(str).next();
	}
	private static boolean validateTemp(double temp)
	{
		return (temp>=-126.0 && temp<=136.00)?true:false; 
	}
	private static String[] getNeededArr(ArrayList<CSVParser> csvArr,int csvArrIndex,String str)
	{
		return 	csvArr.get(csvArrIndex).get(str);
	}
	public static void testing()
	{
		CSVParser csvParser=new CSVParser();
		guiController(csvParser);
	}
	private static enum ColumnChoice{TEMPERATURE,HUMIDITY;}
	private static enum PropertyChoice{HIGHEST,LOWEST;}
	/*------------------------------------------------------------GUI SECTION--------------------------------------------------------*/
	public static void guiController(CSVParser csvParser)
	{	
		boolean execute=true;
		do
		{
			gui();
			execute=doOperation(csvParser);
			for(int i=0;i<15;i++) out.print("-"); out.println();
		}while(execute);
	}
	public static boolean doOperation(CSVParser csvParser)
	{
		Scanner input=new Scanner(System.in);
		int choice=0;
		try{choice=Integer.parseInt(input.nextLine());}catch(NumberFormatException ex){out.println("Incorrect Response");guiController(csvParser);}	
		catch(NoSuchElementException ex){System.out.println("Termination by outer source, exiting"); System.exit(0);}
		switch(choice)
		{
			case 1:printColumnPropertyDay(csvParser,ColumnChoice.TEMPERATURE,PropertyChoice.HIGHEST);break;
			case 2:printColumnPropertyDay(csvParser,ColumnChoice.TEMPERATURE,PropertyChoice.LOWEST);break;
			case 3:printColumnPropertyDay(csvParser,ColumnChoice.HUMIDITY,PropertyChoice.LOWEST);break;
			case 4:printColumnPropertyDay(csvParser,ColumnChoice.HUMIDITY,PropertyChoice.HIGHEST);break;
			case 5:printAverageTempFile(csvParser);break;
			case 6:printAverageTempForHumidity(csvParser);break;
			case 7:printColumnPropertyLargeScale(ColumnChoice.TEMPERATURE,PropertyChoice.HIGHEST);break;
			case 8:printColumnPropertyLargeScale(ColumnChoice.TEMPERATURE,PropertyChoice.LOWEST);break;
			case 9:printColumnPropertyLargeScale(ColumnChoice.HUMIDITY,PropertyChoice.LOWEST);break;
			case 10:printColumnPropertyLargeScale(ColumnChoice.HUMIDITY,PropertyChoice.HIGHEST);break;
			case 11:return false;
			default:out.println("Incorrect input!");
		}
		return continueOperation();
	}
	private static boolean continueOperation()
	{
		out.print("Do you want to Reuse the program? (y/n)");
		switch(new Scanner(System.in).nextLine().toLowerCase().charAt(0))
		{
			case 'y':
			case '1':return true;
			case 'n':
			case '0':return false;
			default:out.println("Incorrect response"); return continueOperation();
		}
		
	}
	public static void gui()
	{
		out.print("\nWeather Data Analysis (Application on CSVParser) Version 1.0\n----------------------------------------------------------------\n"+
		"1.Print The Hottest temperature and hour of the day\n"+
		"2.Print The Coldest temperature and hour of the day\n"+
		"3.Print the Dampest humidity and hour of the day\n"+
		"4.Print the Highest humidity and hour of the day\n"+
		"5.Find The Average Temperature of the day\n"+
		"6.Find The Average Temperature for humidity higher than you specify in the day\n"+
		"7.Find The Hottest day of many\n"+
		"8.Find The Coldest day of many\n"+
		"9.Find The Dampest day of many\n"+
		"10.Find The Humdist day of many\n"+
		"11.Exit Program\n"+
		"Enter your Choice: ");
	}
}