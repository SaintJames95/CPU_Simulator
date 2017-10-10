package utilities;
import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;

import instructions.*;

public class Parser {
	
	public static ArrayList<Instruction> parseInst(String filename) throws IOException
	{
		return parseInst(parse(filename));
	}
	
	public static ArrayList<String> parseData(String filename) throws IOException
	{
		return parseData(parse(filename));
	}
	
	//BASE PARSE
	//Input -> ArrayList<String>
	public static ArrayList<ArrayList<String>> parse(String filename) throws IOException
	{
		FileReader fr = new FileReader(filename);
		BufferedReader file = new BufferedReader(fr);
		
		ArrayList<ArrayList<String>> output = new ArrayList<>();
		
		String line = null;
		String[] lineSplit = null;
		
		while ((line = file.readLine()) != null)
		{
			if(line.equals(""))
				continue;
			
			//Handle whitespace
			line = line.replaceAll("\\s+", " ");
			line = line.trim();
			//Destroy commas
			line = line.replaceAll(",", "");
			line = line.toUpperCase();
			lineSplit = line.split("\\s");	
			output.add(new ArrayList<String>(Arrays.asList(lineSplit)));
		}
		
		file.close();
		return output;
	}
	
	//For instructions
	public static ArrayList<Instruction> parseInst(ArrayList<ArrayList<String>> instList)
	{
		ArrayList<Instruction> output = new ArrayList<>();
		
		int counter = 0;
		for(ArrayList<String> instruction : instList)
		{	
			Instruction op = null;
			String label = null;
			int opIndex = 0;
			
			//Only runs max twice assuming good input
			for(String arg : instruction)
			{
				//If Label is here
				if(arg.contains(":"))
				{
					label = arg.replace(":", "");
					opIndex = 1;
					continue;
				}
				
				//At OP 
				else
				{	
					//Instantiate the operation found
					try{
						Class<?> clazz = Class.forName("instructions." + arg);
						op = (Instruction) clazz.newInstance();
					}
					
					catch(Exception e)
					{
						System.out.println("Instruction " + arg + " is not supported");
						break;
					}
					
					op.setLabel(label);
					op.parse(new ArrayList<String>(instruction.subList(opIndex + 1, instruction.size())));
					break;
				}
			}
			op.instAddress = counter;
			op.toString = instruction.toString();
			counter++;
			
			output.add(op);
		}
		return output;
	}
	
	//For Data
	public static ArrayList<String> parseData(ArrayList<ArrayList<String>> s)
	{
		ArrayList<String> output = new ArrayList<>();		
		for (ArrayList<String> l : s)
		{
			output.add(l.get(0));
		}
		return output;
	}
	
	//Convert String RX to int X
	//Assumes that it is formated as such
	public static int registerParse(String reg)
	{
		return Integer.parseInt(reg.substring(1));
	}
	
}
