package data;

import java.util.ArrayList;

public class Memory{
	
	private static int[] memory = new int[32];
	
	public static void initMemory(ArrayList<String> data)
	{
		int i = 0;
		for(String s: data)
		{
			memory[i] = Integer.parseInt(s, 2);
			i++;
		}
	}

	public static int deref(int address) {
		
		int index = (address - 256) / 4;
		
		//Error Check
		if(index < 0 || index > 31)
		{
			System.out.println("Memory Address is out of bounds");
		}
		
		if((address % 4) != 0)
		{
			System.out.println("Memory address is not divisible by 4");
		}
		
		return memory[index];
	}

	public static void writeToMem(Integer key, Integer data) {
		
		if(key == null)
		{
			return;
		}
		
		int index = (key - 256) / 4;
			
		//Error Check
		if(index < 0 || index > 31)
		{
			System.out.println("Memory Address is out of bounds");
		}
			
		if((key % 4) != 0)
		{
			System.out.println("Memory address is not divisible by 4");
		}

		if(data == null)
		{
			System.out.println("ERROR: Trying to write to memory but no specified"
					+ " data to write");
		}
		
		else
		{
			memory[index] = data;
		}
	}
}

